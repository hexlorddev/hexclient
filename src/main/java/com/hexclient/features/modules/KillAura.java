package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * KillAura - Automatically attacks nearby entities
 * Similar to combat features found in Wurst and Meteor clients
 */
public class KillAura extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Settings
    private double range = 4.2;
    private boolean targetPlayers = true;
    private boolean targetMobs = true;
    private boolean targetAnimals = false;
    private boolean requireLineOfSight = true;
    private int attackDelay = 10; // ticks
    private int ticksSinceLastAttack = 0;
    
    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", FeatureCategory.COMBAT);
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        ticksSinceLastAttack++;
        
        if (ticksSinceLastAttack < attackDelay) return;
        
        Entity target = findBestTarget();
        if (target != null) {
            attack(target);
            ticksSinceLastAttack = 0;
        }
    }
    
    private Entity findBestTarget() {
        if (mc.player == null || mc.world == null) return null;
        
        Vec3d playerPos = mc.player.getPos();
        Box searchBox = new Box(playerPos.subtract(range, range, range), 
                               playerPos.add(range, range, range));
        
        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox);
        
        Entity bestTarget = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (!isValidTarget(entity)) continue;
            
            double distance = mc.player.distanceTo(entity);
            if (distance > range) continue;
            
            if (requireLineOfSight && !hasLineOfSight(entity)) continue;
            
            if (distance < closestDistance) {
                closestDistance = distance;
                bestTarget = entity;
            }
        }
        
        return bestTarget;
    }
    
    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        if (living.isDead() || living.getHealth() <= 0) return false;
        
        if (entity instanceof PlayerEntity) {
            return targetPlayers && entity != mc.player;
        }
        
        if (entity instanceof Monster) {
            return targetMobs;
        }
        
        if (entity instanceof AnimalEntity) {
            return targetAnimals;
        }
        
        return false;
    }
    
    private boolean hasLineOfSight(Entity target) {
        if (mc.player == null || mc.world == null) return false;
        
        Vec3d start = mc.player.getEyePos();
        Vec3d end = target.getEyePos();
        
        HitResult result = mc.world.raycast(new RaycastContext(
            start, end, 
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            mc.player
        ));
        
        return result.getType() == HitResult.Type.MISS || 
               (result instanceof EntityHitResult entityHit && entityHit.getEntity() == target);
    }
    
    private void attack(Entity target) {
        if (mc.player == null || mc.interactionManager == null) return;
        
        // Look at the target
        lookAtEntity(target);
        
        // Attack the target
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
    
    private void lookAtEntity(Entity target) {
        if (mc.player == null) return;
        
        Vec3d targetPos = target.getEyePos();
        Vec3d playerPos = mc.player.getEyePos();
        
        Vec3d direction = targetPos.subtract(playerPos).normalize();
        
        double yaw = Math.toDegrees(Math.atan2(-direction.x, direction.z));
        double pitch = Math.toDegrees(-Math.asin(direction.y));
        
        mc.player.setYaw((float) yaw);
        mc.player.setPitch((float) pitch);
    }
    
    // Getters and setters for settings
    public double getRange() { return range; }
    public void setRange(double range) { this.range = Math.max(0, Math.min(6, range)); }
    
    public boolean isTargetPlayers() { return targetPlayers; }
    public void setTargetPlayers(boolean targetPlayers) { this.targetPlayers = targetPlayers; }
    
    public boolean isTargetMobs() { return targetMobs; }
    public void setTargetMobs(boolean targetMobs) { this.targetMobs = targetMobs; }
    
    public boolean isTargetAnimals() { return targetAnimals; }
    public void setTargetAnimals(boolean targetAnimals) { this.targetAnimals = targetAnimals; }
    
    public boolean isRequireLineOfSight() { return requireLineOfSight; }
    public void setRequireLineOfSight(boolean requireLineOfSight) { this.requireLineOfSight = requireLineOfSight; }
    
    public int getAttackDelay() { return attackDelay; }
    public void setAttackDelay(int attackDelay) { this.attackDelay = Math.max(1, attackDelay); }
}