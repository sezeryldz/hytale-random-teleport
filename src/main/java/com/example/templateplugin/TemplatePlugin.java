package com.example.templateplugin;

/**
 * Main plugin class.
 * 
 * TODO: Implement your plugin logic here.
 * 
 * @author YourName
 * @version 1.0.0
 */
public class TemplatePlugin {

    private static TemplatePlugin instance;
    
    /**
     * Constructor - Called when plugin is loaded.
     */
    public TemplatePlugin() {
        instance = this;
        System.out.println("[TemplatePlugin] Plugin loaded!");
    }
    
    /**
     * Called when plugin is enabled.
     */
    public void onEnable() {
        System.out.println("[TemplatePlugin] Plugin enabled!");
        
        // TODO: Initialize your plugin here
        // - Load configuration
        // - Register event listeners
        // - Register commands
        // - Start services
    }
    
    /**
     * Called when plugin is disabled.
     */
    public void onDisable() {
        System.out.println("[TemplatePlugin] Plugin disabled!");
        
        // TODO: Cleanup your plugin here
        // - Save data
        // - Stop services
        // - Close connections
    }
    
    /**
     * Get plugin instance.
     */
    public static TemplatePlugin getInstance() {
        return instance;
    }
}
