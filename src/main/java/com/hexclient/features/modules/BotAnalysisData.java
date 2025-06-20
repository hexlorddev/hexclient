package com.hexclient.features.modules;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Bot Analysis Data - Helper class for bot detection
 * Tracks player behavior patterns for bot detection algorithms
 */
public class BotAnalysisData {
    
    private final List<Vec3d> positionHistory = new ArrayList<>();
    private final List<Vec3d> velocityHistory = new ArrayList<>();
    private final List<Float[]> rotationHistory = new ArrayList<>();
    
    private int tickCount = 0;
    private int repeatedActionCount = 0;
    private String detectionReason = "";
    
    // Movement pattern tracking
    private long lastMovementTime = 0;
    private Vec3d lastPosition = null;
    private Vec3d lastVelocity = null;
    private float lastYaw = 0;
    private float lastPitch = 0;
    
    // Combat tracking
    private long lastAttackTime = 0;
    private long lastDamageTime = 0;
    private int perfectAimStreak = 0;
    private int inhumanReactionCount = 0;
    
    // Bedwars specific tracking
    private int rapidBlockInteractions = 0;
    private int perfectBridgeBlocks = 0;
    private long lastBlockPlaceTime = 0;
    private int generatorCampingTicks = 0;
    
    public void addPosition(Vec3d position) {
        positionHistory.add(position);
        
        // Keep only recent history
        if (positionHistory.size() > 50) {
            positionHistory.remove(0);
        }
        
        lastPosition = position;
    }
    
    public void addVelocity(Vec3d velocity) {
        velocityHistory.add(velocity);
        
        // Keep only recent history
        if (velocityHistory.size() > 30) {
            velocityHistory.remove(0);
        }
        
        lastVelocity = velocity;
    }
    
    public void addRotation(float yaw, float pitch) {
        rotationHistory.add(new Float[]{yaw, pitch});
        
        // Keep only recent history
        if (rotationHistory.size() > 40) {
            rotationHistory.remove(0);
        }
        
        lastYaw = yaw;
        lastPitch = pitch;
    }
    
    public void updateActionPattern(PlayerEntity player) {
        // Track various action patterns that might indicate bot behavior
        
        // Check for repeated movements
        if (hasRepeatedMovement()) {
            repeatedActionCount++;
        }
        
        // Check for inhuman precision
        if (hasInhumanPrecision()) {
            inhumanReactionCount++;
        }
        
        // Update timestamps
        lastMovementTime = System.currentTimeMillis();
    }
    
    private boolean hasRepeatedMovement() {
        if (positionHistory.size() < 10) return false;
        
        // Check if last 5 movements are identical
        Vec3d baseMovement = null;
        for (int i = positionHistory.size() - 5; i < positionHistory.size() - 1; i++) {
            Vec3d movement = positionHistory.get(i + 1).subtract(positionHistory.get(i));
            
            if (baseMovement == null) {
                baseMovement = movement;
            } else {
                if (baseMovement.distanceTo(movement) > 0.001) {
                    return false;
                }
            }
        }
        
        return baseMovement != null && baseMovement.length() > 0.01;
    }
    
    private boolean hasInhumanPrecision() {
        if (rotationHistory.size() < 5) return false;
        
        // Check for perfectly consistent rotation increments
        Float[] baseRotation = rotationHistory.get(rotationHistory.size() - 5);
        Float[] currentRotation = rotationHistory.get(rotationHistory.size() - 1);
        
        float yawDiff = Math.abs(currentRotation[0] - baseRotation[0]);
        float pitchDiff = Math.abs(currentRotation[1] - baseRotation[1]);
        
        // Inhuman precision: changes are too consistent
        return yawDiff < 0.01 && pitchDiff < 0.01 && (yawDiff > 0 || pitchDiff > 0);
    }
    
    public boolean hasInhumanReactionTime(double threshold) {
        return inhumanReactionCount > 3;
    }
    
    public boolean hasPerfectAimTracking() {
        return perfectAimStreak > 10;
    }
    
    public boolean hasRoboticCombatPattern() {
        // Check for robotic combat patterns
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAttackTime) < 50 && perfectAimStreak > 5;
    }
    
    public boolean hasRapidBlockInteraction() {
        return rapidBlockInteractions > 20;
    }
    
    public boolean hasPerfectBridgePattern() {
        return perfectBridgeBlocks > 15;
    }
    
    public boolean hasInstantBedBreaking() {
        // Check for instant bed breaking patterns
        return rapidBlockInteractions > 10 && (System.currentTimeMillis() - lastBlockPlaceTime) < 25;
    }
    
    public boolean hasRoboticResourceCollection() {
        // Check for robotic resource collection patterns
        return repeatedActionCount > 20;
    }
    
    public boolean hasPerfectGeneratorCamping() {
        return generatorCampingTicks > 100;
    }
    
    public void incrementTickCount() {
        tickCount++;
    }
    
    // Getters
    public List<Vec3d> getPositionHistory() {
        return new ArrayList<>(positionHistory);
    }
    
    public List<Vec3d> getVelocityHistory() {
        return new ArrayList<>(velocityHistory);
    }
    
    public List<Float[]> getRotationHistory() {
        return new ArrayList<>(rotationHistory);
    }
    
    public int getTickCount() {
        return tickCount;
    }
    
    public int getRepeatedActionCount() {
        return repeatedActionCount;
    }
    
    public String getDetectionReason() {
        return detectionReason;
    }
    
    public void setDetectionReason(String reason) {
        this.detectionReason = reason;
    }
    
    // Combat tracking methods
    public void recordAttack() {
        lastAttackTime = System.currentTimeMillis();
    }
    
    public void recordDamage() {
        lastDamageTime = System.currentTimeMillis();
    }
    
    public void incrementPerfectAim() {
        perfectAimStreak++;
    }
    
    public void resetPerfectAim() {
        perfectAimStreak = 0;
    }
    
    // Bedwars tracking methods
    public void recordBlockInteraction() {
        rapidBlockInteractions++;
        lastBlockPlaceTime = System.currentTimeMillis();
    }
    
    public void recordBridgeBlock() {
        perfectBridgeBlocks++;
    }
    
    public void incrementGeneratorCamping() {
        generatorCampingTicks++;
    }
    
    public void resetGeneratorCamping() {
        generatorCampingTicks = 0;
    }
}