## **<span style="color: #843fa1;">Random </span> <span style="color: #236fa1;">Teleport</span>**

**![](https://media.forgecdn.net/attachments/description/null/description_c9e573f9-afaa-4b3f-b20b-5f916b0e5876.jpg)**

**A simple random teleportation(rtp) plugin for Hytale servers.**

### **<span style="color: #843fa1;">Features</span>**

*   **/rtp** - Teleport to a random location away from spawn
*   Warmup time asks user not to move to teleport
*   <span style="color: #3598db;">Translation </span> support in config
*   **1 hour cooldown**  default to prevent spam (configurable)
*   **Aliases:** /randomtp, /randomteleport
*   <span style="color: #3598db;">Permission </span> support ( More information 

### <span style="color: #843fa1;"><strong>How to Install</strong></span>

1.  Download the JAR file
2.  Place it in your server /mods folder
3.  Restart the server
4.  Configure it in mods/\_RandomTeleport/config.json or leave it as default 
5.  Use <span style="color: #3598db;">/rtp </span> command in game.

 

**<span style="color: #843fa1;">Configuration:</span>**

> ```
> {
>   "pluginName": "RandomTeleport",
>   "version": "1.1.0",
>   "debugMode": false,
>   "permissions": {
>     "use": "randomteleport.use",
>     "bypassCooldown": "randomteleport.bypass.cooldown",
>     "bypassWarmup": "randomteleport.bypass.warmup"
>   },
>   "tiers": {
>     "diamond": {
>       "permission": "randomteleport.tier.diamond",
>       "cooldownSeconds": 300,
>       "warmupSeconds": 1
>     },
>     "gold": {
>       "permission": "randomteleport.tier.gold",
>       "cooldownSeconds": 900,
>       "warmupSeconds": 2
>     },
>     "silver": {
>       "permission": "randomteleport.tier.silver",
>       "cooldownSeconds": 1800,
>       "warmupSeconds": 3
>     },
>     "bronze": {
>       "permission": "randomteleport.tier.bronze",
>       "cooldownSeconds": 2700,
>       "warmupSeconds": 5
>     }
>   },
>   "defaults": {
>     "cooldownSeconds": 3600,
>     "warmupSeconds": 5,
>     "minDistance": 5000,
>     "maxDistance": 9000,
>     "movementThreshold": 0.5,
>     "minHeight": 120,
>     "maxHeight": 200
>   },
>   "messages": {
>     "cooldown": "You must wait {time} before using /rtp again!",
>     "noPermission": "You don\u0027t have permission to use /rtp!",
>     "noWorld": "You must be in a world to use this command!",
>     "warmupStart": "Teleporting in {seconds} seconds... Don\u0027t move!",
>     "movedCancelled": "Teleportation cancelled! You moved too much.",
>     "noSafeSpot": "Could not find a safe landing spot. Try again!",
>     "error": "Error scanning for safe location.",
>     "teleported": "Teleported to X: {x}, Y: {y}, Z: {z} ({distance} blocks from spawn)",
>     "warning1": "WARNING: RTP is in early development!",
>     "warning2": "May teleport to dangerous locations. Move to cancel."
>   }
> }
> ```

 

**<span style="color: #843fa1;">Permissions: ( You can configure tier <span style="color: #3598db;">cooldown </span>and <span style="color: #3598db;">warmup </span>in config. )</span>**

| Permission                     |Default |Description                    |
| ------------------------------ |------- |------------------------------ |
| <pre><code>randomteleport.use</code></pre> |✓       |&nbsp; Access to rtp           |
| <pre><code>randomteleport.bypass.cooldown</code></pre> |✗       |&nbsp; Skip cooldown entirely  |
| <pre><code>randomteleport.bypass.warmup</code></pre> |✗       |&nbsp; Instant teleport (no warmup) |
| <pre><code>randomteleport.tier.diamond</code></pre> |✗       |&nbsp; 5 min cooldown, 1s warmup |
| <pre><code>randomteleport.tier.gold</code></pre> |✗       |&nbsp; 15 min cooldown, 2s warmup |
| <pre><code>randomteleport.tier.silver</code></pre> |✗       |&nbsp; 30 min cooldown, 3s warmup |
| <pre><code>randomteleport.tier.bronze</code></pre> |✗       |&nbsp; 45 min cooldown, 5s warmup |
 

<span style="color: #3598db;"><strong>Source Code:</strong>&nbsp;<a style="color: #3598db;" href="https://github.com/Vorlas/hytale-random-teleport" rel="nofollow">GitHub</a></span>

<span style="color: #843fa1;">If you find this useful, give it a ⭐ on GitHub and follow me for more plugins!</span>

***