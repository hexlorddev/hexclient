package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * AutoDodge - Automatic projectile and attack dodging
 * Provides bot-level evasion capabilities
 */
public class AutoDodge extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Dodge settings
    private double detectionRange = 15.0;
    private boolean dodgeArrows = true;
    private boolean dodgeFireballs = true;
    private boolean dodgeAttacks = true;
    private boolean predictiveDodging = true;
    private double dodgeStrength = 1.0;
    
    // Advanced settings
    private boolean matrixDodge = false; // Matrix-style dodging
    private boolean jumpDodge = true;
    private boolean strafeDodge = true;
    private boolean backwardDodge = true;
    
    public AutoDodge() {
        super("AutoDodge", "Automatic dodging with bot-level reflexes", FeatureCategory.COMBAT);
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Detect incoming threats
        List<Entity> threats = detectThreats();
        
        if (!threats.isEmpty()) {
            // Calculate best dodge direction
            Vec3d dodgeDirection = calculateDodgeDirection(threats);
            
            if (dodgeDirection != null) {
                executeDodge(dodgeDirection);
            }
        }
    }
    
    private List<Entity> detectThreats() {
        if (mc.player == null || mc.world == null) return List.of();
        
        Vec3d playerPos = mc.player.getPos();
        
        return mc.world.getOtherEntities(mc.player, 
            mc.player.getBoundingBox().expand(detectionRange))
            .stream()
            .filter(this::isThreat)
            .filter(entity -> isHeadingTowardsPlayer(entity))
            .toList();
    }
    
    private boolean isThreat(Entity entity) {
        if (entity instanceof ProjectileEntity) {
            String entityName = entity.getType().toString().toLowerCase();
            
            if (dodgeArrows && entityName.contains("arrow")) {
                return true;
            }
            if (dodgeFireballs && (entityName.contains("fireball") || entityName.contains("fire_charge"))) {
                return true;
            }
            
            // Other projectiles
            return entityName.contains("projectile") || 
                   entityName.contains("throwable") ||
                   entityName.contains("snowball") ||
                   entityName.contains("egg") ||
                   entityName.contains("ender_pearl");
        }
        
        return false;
    }
    
    private boolean isHeadingTowardsPlayer(Entity entity) {
        if (mc.player == null) return false;
        
        Vec3d entityPos = entity.getPos();
        Vec3d playerPos = mc.player.getPos();
        Vec3d entityVelocity = entity.getVelocity();
        
        // Check if entity is moving towards player
        Vec3d directionToPlayer = playerPos.subtract(entityPos).normalize();
        Vec3d entityDirection = entityVelocity.normalize();
        
        double dot = entityDirection.dotProduct(directionToPlayer);
        
        // If predictive dodging is enabled, predict future collision
        if (predictiveDodging) {
            return willCollideWithPlayer(entity);
        }
        
        return dot > 0.5; // Entity is generally moving towards player
    }
    
    private boolean willCollideWithPlayer(Entity entity) {
        if (mc.player == null) return false;
        
        Vec3d entityPos = entity.getPos();
        Vec3d entityVelocity = entity.getVelocity();
        Vec3d playerPos = mc.player.getPos();
        
        // Predict entity position over next few ticks
        for (int ticks = 1; ticks <= 20; ticks++) {
            Vec3d futureEntityPos = entityPos.add(entityVelocity.multiply(ticks));
            
            // Check if entity will be close to player
            if (futureEntityPos.distanceTo(playerPos) < 2.0) {
                return true;
            }
        }
        
        return false;
    }
    
    private Vec3d calculateDodgeDirection(List<Entity> threats) {
        if (mc.player == null || threats.isEmpty()) return null;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d totalThreatDirection = Vec3d.ZERO;
        
        // Calculate combined threat direction
        for (Entity threat : threats) {
            Vec3d threatPos = threat.getPos();
            Vec3d threatVelocity = threat.getVelocity();
            
            // Predict where threat will be
            Vec3d predictedThreatPos = threatPos.add(threatVelocity.multiply(10));
            
            Vec3d threatDirection = predictedThreatPos.subtract(playerPos).normalize();
            totalThreatDirection = totalThreatDirection.add(threatDirection);
        }
        
        if (totalThreatDirection.length() == 0) return null;
        
        // Calculate optimal dodge direction (perpendicular to threat)
        Vec3d averageThreatDirection = totalThreatDirection.normalize();
        
        // Create perpendicular directions
        Vec3d leftDodge = new Vec3d(-averageThreatDirection.z, 0, averageThreatDirection.x);
        Vec3d rightDodge = new Vec3d(averageThreatDirection.z, 0, -averageThreatDirection.x);
        Vec3d backDodge = averageThreatDirection.multiply(-1);
        
        // Choose best dodge direction based on settings and safety
        Vec3d bestDodge = null;
        
        if (strafeDodge) {
            // Choose left or right based on which is safer
            if (isSafeDodgeDirection(leftDodge)) {
                bestDodge = leftDodge;
            } else if (isSafeDodgeDirection(rightDodge)) {
                bestDodge = rightDodge;
            }
        }
        
        if (bestDodge == null && backwardDodge && isSafeDodgeDirection(backDodge)) {
            bestDodge = backDodge;
        }
        
        // Matrix dodge - combination movement
        if (matrixDodge && bestDodge != null) {
            bestDodge = bestDodge.add(0, jumpDodge ? 0.3 : 0, 0);
        }
        
        return bestDodge;
    }
    
    private boolean isSafeDodgeDirection(Vec3d direction) {
        if (mc.player == null || mc.world == null) return false;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d testPos = playerPos.add(direction.multiply(2));
        
        // Check if dodge direction leads to safe ground
        return !mc.world.getBlockState(
            net.minecraft.util.math.BlockPos.ofFloored(testPos)
        ).isAir() || 
        !mc.world.getBlockState(
            net.minecraft.util.math.BlockPos.ofFloored(testPos.add(0, -1, 0))
        ).isAir();
    }
    
    private void executeDodge(Vec3d dodgeDirection) {
        if (mc.player == null) return;
        
        Vec3d currentVelocity = mc.player.getVelocity();
        Vec3d dodgeVelocity = dodgeDirection.multiply(dodgeStrength * 0.3);
        
        // Apply dodge movement
        Vec3d newVelocity = currentVelocity.add(dodgeVelocity);
        mc.player.setVelocity(newVelocity);
        
        // Jump dodge if enabled and dodge has upward component
        if (jumpDodge && dodgeDirection.y > 0 && mc.player.isOnGround()) {
            mc.player.jump();
        }
        
        // Matrix dodge - lean away from threat
        if (matrixDodge) {
            performMatrixDodge(dodgeDirection);
        }
    }
    
    private void performMatrixDodge(Vec3d dodgeDirection) {
        if (mc.player == null) return;
        
        // Calculate lean angle based on dodge direction
        double leanAngle = Math.atan2(dodgeDirection.x, dodgeDirection.z);
        
        // Apply subtle rotation for matrix effect
        float currentYaw = mc.player.getYaw();
        float newYaw = currentYaw + (float) Math.toDegrees(leanAngle) * 0.1f;
        
        mc.player.setYaw(newYaw);
        
        // Apply velocity in multiple directions for smooth matrix movement
        Vec3d matrixVelocity = dodgeDirection.multiply(0.2);
        Vec3d currentVel = mc.player.getVelocity();
        mc.player.setVelocity(currentVel.add(matrixVelocity));
    }
    
    // Getters and setters
    public double getDetectionRange() { return detectionRange; }
    public void setDetectionRange(double detectionRange) { 
        this.detectionRange = Math.max(1.0, Math.min(30.0, detectionRange)); 
    }
    
    public boolean isDodgeArrows() { return dodgeArrows; }
    public void setDodgeArrows(boolean dodgeArrows) { this.dodgeArrows = dodgeArrows; }
    
    public boolean isDodgeFireballs() { return dodgeFireballs; }
    public void setDodgeFireballs(boolean dodgeFireballs) { this.dodgeFireballs = dodgeFireballs; }
    
    public boolean isDodgeAttacks() { return dodgeAttacks; }
    public void setDodgeAttacks(boolean dodgeAttacks) { this.dodgeAttacks = dodgeAttacks; }
    
    public boolean isPredictiveDodging() { return predictiveDodging; }
    public void setPredictiveDodging(boolean predictiveDodging) { this.predictiveDodging = predictiveDodging; }
    
    public double getDodgeStrength() { return dodgeStrength; }
    public void setDodgeStrength(double dodgeStrength) { 
        this.dodgeStrength = Math.max(0.1, Math.min(3.0, dodgeStrength)); 
    }
    
    public boolean isMatrixDodge() { return matrixDodge; }
    public void setMatrixDodge(boolean matrixDodge) { this.matrixDodge = matrixDodge; }
    
    public boolean isJumpDodge() { return jumpDodge; }
    public void setJumpDodge(boolean jumpDodge) { this.jumpDodge = jumpDodge; }
    
    public boolean isStrafeDodge() { return strafeDodge; }
    public void setStrafeDodge(boolean strafeDodge) { this.strafeDodge = strafeDodge; }
    
    public boolean isBackwardDodge() { return backwardDodge; }
    public void setBackwardDodge(boolean backwardDodge) { this.backwardDodge = backwardDodge; }
}