package com.mars.randomteleport.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.protocol.packets.player.ClientTeleport;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.Direction;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import com.mars.randomteleport.utils.WarmupManager;
import com.mars.randomteleport.config.RandomTeleportConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RandomTeleportCommand extends AbstractAsyncCommand {

    private static final Random random = new Random();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final WarmupManager warmupManager;
    private final RandomTeleportConfig config;

    public RandomTeleportCommand(RandomTeleportConfig config) {
        super("rtp", "Randomly teleports you away from spawn");
        this.addAliases("randomtp", "randomteleport");
        this.setPermissionGroup(GameMode.Adventure);
        this.warmupManager = new WarmupManager(config);
        this.config = config;
    }

    public void cleanup() {
        this.warmupManager.shutdown();
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRef == null)
                        return;

                    UUID playerUuid = playerRef.getUuid();
                    long currentTime = System.currentTimeMillis();
                    long cooldownMs = config.getCooldownSeconds() * 1000L;

                    if (cooldowns.containsKey(playerUuid)) {
                        long lastUsed = cooldowns.get(playerUuid);
                        long timePassed = currentTime - lastUsed;

                        if (timePassed < cooldownMs) {
                            long remainingMs = cooldownMs - timePassed;
                            String remainingTime = formatTime(remainingMs);
                            String msg = config.getMessageCooldown().replace("{time}", remainingTime);
                            player.sendMessage(Message.raw(msg));
                            return;
                        }
                    }

                    warmupManager.startWarmup(playerRef, ref, store, world, config.getWarmupSeconds(), () -> {
                        executeRandomTeleport(player, ref, store, world, playerUuid);
                    });

                }, world);
            } else {
                player.sendMessage(Message.raw(config.getMessageNoWorld()));
                return CompletableFuture.completedFuture(null);
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private void executeRandomTeleport(Player player, Ref<EntityStore> ref, Store<EntityStore> store, World world,
            UUID playerUuid) {
        int min = config.getMinDistance();
        int max = config.getMaxDistance();
        double distance = min + random.nextDouble() * (max - min);
        double angle = random.nextDouble() * 2 * Math.PI;

        double randomX = Math.cos(angle) * distance;
        double randomZ = Math.sin(angle) * distance;

        world.execute(() -> {
            double teleportY = -1;
            boolean safeLocationFound = false;

            int chunkX = (int) Math.floor(randomX / 16.0);
            int chunkZ = (int) Math.floor(randomZ / 16.0);
            long chunkIndex = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

            try {
                com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk chunk = world.getChunk(chunkIndex);

                if (chunk != null) {
                    int localX = ((int) randomX) & 15;
                    int localZ = ((int) randomZ) & 15;
                    if (localX < 0)
                        localX += 16;
                    if (localZ < 0)
                        localZ += 16;

                    int scanStart = config.getMaxHeight();
                    int scanEnd = config.getMinHeight();

                    for (int y = scanStart; y > scanEnd; y--) {
                        if (chunk.getFluidId(localX, y, localZ) != 0)
                            continue;

                        int blockId = chunk.getBlock(localX, y, localZ);

                        if (blockId != 0) {
                            int block1 = chunk.getBlock(localX, y + 1, localZ);
                            int block2 = chunk.getBlock(localX, y + 2, localZ);
                            int block3 = chunk.getBlock(localX, y + 3, localZ);

                            int fluidHead1 = chunk.getFluidId(localX, y + 1, localZ);
                            int fluidHead2 = chunk.getFluidId(localX, y + 2, localZ);
                            int fluidHead3 = chunk.getFluidId(localX, y + 3, localZ);

                            boolean headClear = block1 == 0 && block2 == 0 && block3 == 0;
                            boolean noFluids = fluidHead1 == 0 && fluidHead2 == 0 && fluidHead3 == 0;

                            if (headClear && noFluids) {
                                teleportY = y + 1.0;
                                safeLocationFound = true;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                player.sendMessage(Message.raw(config.getMessageError()));
                e.printStackTrace();
            }

            if (!safeLocationFound) {
                player.sendMessage(Message.raw(config.getMessageNoSafeSpot()));
                return;
            }

            var transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform != null) {
                Vector3d targetPosition = new Vector3d(randomX, teleportY, randomZ);
                transform.teleportPosition(targetPosition);

                Position pos = new Position(targetPosition.x, targetPosition.y, targetPosition.z);
                Direction body = new Direction(0f, 0f, 0f);
                Direction look = new Direction(0f, 0f, 0f);
                ModelTransform modelTransform = new ModelTransform(pos, body, look);

                player.getPlayerConnection().write(new ClientTeleport((byte) 0, modelTransform, true));
            }

            cooldowns.put(playerUuid, System.currentTimeMillis());

            String msg = config.getMessageTeleported()
                    .replace("{x}", String.format("%.0f", randomX))
                    .replace("{y}", String.format("%.0f", teleportY))
                    .replace("{z}", String.format("%.0f", randomZ))
                    .replace("{distance}", String.format("%.0f", distance));
            player.sendMessage(Message.raw(msg));
        });
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        if (hours > 0) {
            return String.format("%d hour%s %d minute%s", hours, hours == 1 ? "" : "s", minutes,
                    minutes == 1 ? "" : "s");
        } else if (minutes > 0) {
            return String.format("%d minute%s %d second%s", minutes, minutes == 1 ? "" : "s", seconds,
                    seconds == 1 ? "" : "s");
        } else {
            return String.format("%d second%s", seconds, seconds == 1 ? "" : "s");
        }
    }
}
