package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * BotPvP - Advanced bot-level PvP automation
 * Provides inhuman precision and reaction times for competitive play
 */
public class BotPvP extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Combat settings
    private double range = 3.5;
    private boolean autoSword = true;
    private boolean autoShield = true;
    private boolean perfectAim = true;
    private boolean predictiveTargeting = true;
    private boolean autoBlock = true;
    private boolean autoCombo = true;
    private boolean inhumanReactions = true;
    private int attackDelay = 1; // Minimal delay for bot-like speed
    
    // Advanced settings
    private boolean strafeOptimization = true;
    private boolean jumpReset = true;
    private boolean criticalHits = true;
    private boolean autoTotem = true;
    private boolean autoGapple = true;
    private boolean antiKnockback = true;
    
    // Combat state
    private PlayerEntity currentTarget = null;
    private long lastAttackTime = 0;
    private boolean isBlocking = false;
    private int comboCounter = 0;
    private Vec3d lastTargetPos = null;
    
    public BotPvP() {
        super("BotPvP", "Bot-level PvP automation with inhuman precision", FeatureCategory.COMBAT);
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Find the best target
        PlayerEntity target = findBestTarget();
        
        if (target != null) {
            currentTarget = target;
            performBotPvP(target);
        } else {
            currentTarget = null;
            stopBlocking();
        }
        
        // Auto-healing and protection
        if (autoTotem) handleAutoTotem();
        if (autoGapple) handleAutoGapple();
    }
    
    private PlayerEntity findBestTarget() {
        if (mc.player == null || mc.world == null) return null;
        
        Vec3d playerPos = mc.player.getPos();
        Box searchBox = new Box(playerPos.subtract(range, range, range),
                               playerPos.add(range, range, range));
        
        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox);
        
        PlayerEntity bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (player.isDead() || player.getHealth() <= 0) continue;
            
            double distance = mc.player.distanceTo(player);
            if (distance > range) continue;
            
            // Calculate target priority score
            double score = calculateTargetScore(player);
            if (score < bestScore) {
                bestScore = score;
                bestTarget = player;
            }
        }
        
        return bestTarget;
    }
    
    private double calculateTargetScore(PlayerEntity target) {
        double distance = mc.player.distanceTo(target);
        double health = target.getHealth();
        
        // Prioritize closer, lower health targets
        double score = distance + (health / 20.0) * 2;
        
        // Bonus for targets not looking at us
        Vec3d targetLook = target.getRotationVector();
        Vec3d directionToUs = mc.player.getPos().subtract(target.getPos()).normalize();
        double angle = Math.toDegrees(Math.acos(targetLook.dotProduct(directionToUs)));
        
        if (angle > 90) {
            score -= 2; // Lower score = higher priority
        }
        
        return score;
    }
    
    private void performBotPvP(PlayerEntity target) {
        // Switch to optimal weapon
        if (autoSword) {
            selectBestWeapon();
        }
        
        // Perfect aim at target
        if (perfectAim) {
            aimAtTarget(target);
        }
        
        // Predictive targeting for moving targets
        if (predictiveTargeting) {
            aimAtPredictedPosition(target);
        }
        
        // Execute attack sequence
        executeAttackSequence(target);
        
        // Movement optimization
        if (strafeOptimization) {
            optimizeMovement(target);
        }
        
        // Auto-blocking
        if (autoBlock && shouldBlock(target)) {
            startBlocking();
        } else {
            stopBlocking();
        }
    }
    
    private void selectBestWeapon() {
        if (mc.player == null) return;
        
        int bestSlot = -1;
        double bestDamage = 0;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            
            double damage = getItemDamage(stack);
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }
        
        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }
    
    private double getItemDamage(ItemStack stack) {
        Item item = stack.getItem();
        
        if (item instanceof SwordItem sword) {
            return sword.getAttackDamage() + 4; // Base damage + sword damage
        } else if (item instanceof AxeItem axe) {
            return axe.getAttackDamage() + 1; // Axes do more damage but slower
        } else if (item instanceof ToolItem tool) {
            return tool.getAttackDamage() + 1;
        }
        
        return 1.0; // Fist damage
    }
    
    private void aimAtTarget(PlayerEntity target) {
        if (mc.player == null) return;
        
        Vec3d targetPos = target.getEyePos();
        
        // Add small random offset for less obvious bot behavior (optional)
        if (!inhumanReactions) {
            double randomX = (Math.random() - 0.5) * 0.1;
            double randomY = (Math.random() - 0.5) * 0.1;
            double randomZ = (Math.random() - 0.5) * 0.1;
            targetPos = targetPos.add(randomX, randomY, randomZ);
        }
        
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d direction = targetPos.subtract(playerPos).normalize();
        
        double yaw = Math.toDegrees(Math.atan2(-direction.x, direction.z));
        double pitch = Math.toDegrees(-Math.asin(direction.y));
        
        mc.player.setYaw((float) yaw);
        mc.player.setPitch((float) pitch);
    }
    
    private void aimAtPredictedPosition(PlayerEntity target) {
        if (mc.player == null) return;
        
        Vec3d targetPos = target.getPos();
        Vec3d targetVelocity = target.getVelocity();
        
        // Predict where target will be
        double predictionTime = mc.player.distanceTo(target) / 20.0; // Rough prediction
        Vec3d predictedPos = targetPos.add(targetVelocity.multiply(predictionTime));
        
        // Aim at predicted position
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d direction = predictedPos.add(0, target.getEyeHeight(target.getPose()) / 2, 0)
                                      .subtract(playerPos).normalize();
        
        double yaw = Math.toDegrees(Math.atan2(-direction.x, direction.z));
        double pitch = Math.toDegrees(-Math.asin(direction.y));
        
        mc.player.setYaw((float) yaw);
        mc.player.setPitch((float) pitch);
        
        lastTargetPos = targetPos;
    }
    
    private void executeAttackSequence(PlayerEntity target) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < attackDelay) return;
        
        // Critical hit timing
        if (criticalHits && mc.player.fallDistance > 0 && !mc.player.isOnGround()) {
            performAttack(target);
        } else if (!criticalHits) {
            performAttack(target);
        }
        
        // Jump reset for combos
        if (jumpReset && comboCounter > 0 && mc.player.isOnGround()) {
            mc.player.jump();
        }
        
        lastAttackTime = currentTime;
    }
    
    private void performAttack(PlayerEntity target) {
        if (mc.player == null || mc.interactionManager == null) return;
        
        // Stop blocking to attack
        if (isBlocking) {
            stopBlocking();
        }
        
        // Attack the target
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        
        comboCounter++;
        
        // Auto-combo continuation
        if (autoCombo && comboCounter < 3) {
            // Continue combo with optimal timing
            attackDelay = 1; // Very fast for combo
        } else {
            comboCounter = 0;
            attackDelay = 5; // Normal delay after combo
        }
    }
    
    private void optimizeMovement(PlayerEntity target) {
        if (mc.player == null) return;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d direction = targetPos.subtract(playerPos).normalize();
        
        // Strafe around target for better positioning
        Vec3d strafeDirection = new Vec3d(-direction.z, 0, direction.x);
        
        // Apply strafe movement
        Vec3d currentVelocity = mc.player.getVelocity();
        Vec3d newVelocity = currentVelocity.add(strafeDirection.multiply(0.1));
        
        mc.player.setVelocity(newVelocity);
        
        // Maintain optimal distance
        double distance = mc.player.distanceTo(target);
        if (distance < 2.5) {
            // Too close, move back
            Vec3d backDirection = direction.multiply(-0.1);
            mc.player.setVelocity(mc.player.getVelocity().add(backDirection));
        } else if (distance > 3.2) {
            // Too far, move closer
            Vec3d forwardDirection = direction.multiply(0.1);
            mc.player.setVelocity(mc.player.getVelocity().add(forwardDirection));
        }
    }
    
    private boolean shouldBlock(PlayerEntity target) {
        if (!autoShield) return false;
        
        // Check if target is about to attack
        ItemStack targetItem = target.getMainHandStack();
        if (targetItem.getItem() instanceof SwordItem || targetItem.getItem() instanceof AxeItem) {
            // Check if target is looking at us and close
            Vec3d targetLook = target.getRotationVector();
            Vec3d directionToUs = mc.player.getPos().subtract(target.getPos()).normalize();
            double angle = Math.toDegrees(Math.acos(targetLook.dotProduct(directionToUs)));
            
            return angle < 45 && mc.player.distanceTo(target) < 4;
        }
        
        return false;
    }
    
    private void startBlocking() {
        if (mc.player == null || isBlocking) return;
        
        // Switch to shield if available
        if (autoShield) {
            selectShield();
        }
        
        // Start blocking
        if (mc.options.useKey != null) {
            mc.options.useKey.setPressed(true);
            isBlocking = true;
        }
    }
    
    private void stopBlocking() {
        if (!isBlocking) return;
        
        if (mc.options.useKey != null) {
            mc.options.useKey.setPressed(false);
            isBlocking = false;
        }
    }
    
    private void selectShield() {
        if (mc.player == null) return;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof ShieldItem) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }
    
    private void handleAutoTotem() {
        if (mc.player == null) return;
        
        // Check if we need a totem
        if (mc.player.getHealth() < 6 && !hasTotemInOffhand()) {
            moveTotemToOffhand();
        }
    }
    
    private void handleAutoGapple() {
        if (mc.player == null) return;
        
        // Auto-eat golden apples when low health
        if (mc.player.getHealth() < 10) {
            eatGoldenApple();
        }
    }
    
    private boolean hasTotemInOffhand() {
        if (mc.player == null) return false;
        ItemStack offhandStack = mc.player.getOffHandStack();
        return offhandStack.getItem() == Items.TOTEM_OF_UNDYING;
    }
    
    private void moveTotemToOffhand() {
        if (mc.player == null || mc.interactionManager == null) return;
        
        // Find totem in inventory
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                // Move to offhand
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, 
                    net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, 
                    net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
                break;
            }
        }
    }
    
    private void eatGoldenApple() {
        if (mc.player == null) return;
        
        // Find and select golden apple
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                mc.player.getInventory().selectedSlot = i;
                
                // Start eating
                if (mc.options.useKey != null) {
                    mc.options.useKey.setPressed(true);
                }
                break;
            }
        }
    }
    
    // Getters and setters
    public double getRange() { return range; }
    public void setRange(double range) { this.range = Math.max(1.0, Math.min(6.0, range)); }
    
    public boolean isAutoSword() { return autoSword; }
    public void setAutoSword(boolean autoSword) { this.autoSword = autoSword; }
    
    public boolean isAutoShield() { return autoShield; }
    public void setAutoShield(boolean autoShield) { this.autoShield = autoShield; }
    
    public boolean isPerfectAim() { return perfectAim; }
    public void setPerfectAim(boolean perfectAim) { this.perfectAim = perfectAim; }
    
    public boolean isPredictiveTargeting() { return predictiveTargeting; }
    public void setPredictiveTargeting(boolean predictiveTargeting) { this.predictiveTargeting = predictiveTargeting; }
    
    public boolean isInhumanReactions() { return inhumanReactions; }
    public void setInhumanReactions(boolean inhumanReactions) { this.inhumanReactions = inhumanReactions; }
    
    public int getAttackDelay() { return attackDelay; }
    public void setAttackDelay(int attackDelay) { this.attackDelay = Math.max(1, Math.min(100, attackDelay)); }
}