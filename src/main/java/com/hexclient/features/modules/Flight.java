package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/**
 * Flight - Creative-style flight in survival mode
 * Common feature in most Minecraft clients
 */
public class Flight extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Flight settings
    private double speed = 1.0;
    private FlightMode mode = FlightMode.CREATIVE;
    private boolean antiKick = true;
    
    public enum FlightMode {
        CREATIVE("Creative"),
        JETPACK("Jetpack"),
        VANILLA("Vanilla");
        
        private final String name;
        
        FlightMode(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public Flight() {
        super("Flight", "Allows creative-style flight", FeatureCategory.MOVEMENT);
    }
    
    @Override
    protected void onEnable() {
        super.onEnable();
        if (mc.player != null && mode == FlightMode.CREATIVE) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
        }
    }
    
    @Override
    protected void onDisable() {
        super.onDisable();
        if (mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator()) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().setFlySpeed(0.05f);
        }
    }
    
    @Override
    public void onTick() {
        if (mc.player == null) return;
        
        switch (mode) {
            case CREATIVE -> handleCreativeFlight();
            case JETPACK -> handleJetpackFlight();  
            case VANILLA -> handleVanillaFlight();
        }
        
        if (antiKick) {
            handleAntiKick();
        }
    }
    
    private void handleCreativeFlight() {
        if (mc.player == null) return;
        
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().setFlySpeed((float) (speed * 0.05f));
        
        if (mc.options.jumpKey.isPressed()) {
            mc.player.getAbilities().flying = true;
        }
    }
    
    private void handleJetpackFlight() {
        if (mc.player == null) return;
        
        Vec3d velocity = mc.player.getVelocity();
        
        if (mc.options.jumpKey.isPressed()) {
            // Apply upward velocity
            mc.player.setVelocity(velocity.x, speed * 0.5, velocity.z);
        } else if (mc.options.sneakKey.isPressed()) {
            // Apply downward velocity
            mc.player.setVelocity(velocity.x, -speed * 0.5, velocity.z);
        } else {
            // Maintain current Y velocity with slight decay
            mc.player.setVelocity(velocity.x, velocity.y * 0.9, velocity.z);
        }
        
        // Horizontal movement
        if (mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() ||
            mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed()) {
            
            float yaw = mc.player.getYaw();
            double motionX = 0, motionZ = 0;
            
            if (mc.options.forwardKey.isPressed()) {
                motionX -= Math.sin(Math.toRadians(yaw)) * speed * 0.2;
                motionZ += Math.cos(Math.toRadians(yaw)) * speed * 0.2;
            }
            if (mc.options.backKey.isPressed()) {
                motionX += Math.sin(Math.toRadians(yaw)) * speed * 0.2;
                motionZ -= Math.cos(Math.toRadians(yaw)) * speed * 0.2;
            }
            if (mc.options.leftKey.isPressed()) {
                motionX -= Math.cos(Math.toRadians(yaw)) * speed * 0.2;
                motionZ -= Math.sin(Math.toRadians(yaw)) * speed * 0.2;
            }
            if (mc.options.rightKey.isPressed()) {
                motionX += Math.cos(Math.toRadians(yaw)) * speed * 0.2;
                motionZ += Math.sin(Math.toRadians(yaw)) * speed * 0.2;
            }
            
            mc.player.setVelocity(motionX, velocity.y, motionZ);
        }
    }
    
    private void handleVanillaFlight() {
        if (mc.player == null) return;
        
        // Simple velocity-based flight
        if (mc.options.jumpKey.isPressed()) {
            Vec3d velocity = mc.player.getVelocity();
            mc.player.setVelocity(velocity.x, speed * 0.3, velocity.z);
        }
    }
    
    private void handleAntiKick() {
        if (mc.player == null || mc.world == null) return;
        
        // Periodically apply small downward motion to avoid anti-cheat detection
        if (mc.player.age % 40 == 0) { // Every 2 seconds
            Vec3d velocity = mc.player.getVelocity();
            mc.player.setVelocity(velocity.x, velocity.y - 0.04, velocity.z);
        }
    }
    
    // Getters and setters
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = Math.max(0.1, Math.min(10.0, speed)); }
    
    public FlightMode getMode() { return mode; }
    public void setMode(FlightMode mode) { 
        this.mode = mode;
        if (isEnabled()) {
            onDisable();
            onEnable();
        }
    }
    
    public boolean isAntiKick() { return antiKick; }
    public void setAntiKick(boolean antiKick) { this.antiKick = antiKick; }
}