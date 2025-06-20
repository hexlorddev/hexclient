package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoBedBreaker - Automatic bed breaking with bot-level speed and precision
 * Optimized for Bedwars gameplay
 */
public class AutoBedBreaker extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Settings
    private double range = 5.0;
    private boolean instantBreak = true;
    private boolean autoSwitch = true;
    private boolean onlyEnemyBeds = true;
    private boolean surroundBreak = true;
    private boolean predictiveBreaking = true;
    private int breakDelay = 50; // milliseconds
    
    // Breaking state
    private BlockPos currentTarget = null;
    private long lastBreakTime = 0;
    private int breakingTicks = 0;
    private List<BlockPos> queuedBeds = new ArrayList<>();
    
    public AutoBedBreaker() {
        super("AutoBedBreaker", "Automatically breaks beds with perfect timing", FeatureCategory.COMBAT);
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Find beds to break
        List<BlockPos> beds = findNearbyBeds();
        
        if (!beds.isEmpty()) {
            // Select the best bed to target
            BlockPos targetBed = selectBestBed(beds);
            
            if (targetBed != null) {
                breakBed(targetBed);
            }
        }
        
        // Process queued beds
        processQueuedBeds();
    }
    
    private List<BlockPos> findNearbyBeds() {
        List<BlockPos> beds = new ArrayList<>();
        if (mc.player == null || mc.world == null) return beds;
        
        Vec3d playerPos = mc.player.getPos();
        int searchRange = (int) Math.ceil(range);
        
        // Search for beds in range
        for (int x = -searchRange; x <= searchRange; x++) {
            for (int y = -searchRange; y <= searchRange; y++) {
                for (int z = -searchRange; z <= searchRange; z++) {
                    BlockPos pos = BlockPos.ofFloored(playerPos).add(x, y, z);
                    
                    if (playerPos.distanceTo(Vec3d.ofCenter(pos)) <= range) {
                        Block block = mc.world.getBlockState(pos).getBlock();
                        
                        if (block instanceof BedBlock) {
                            if (!onlyEnemyBeds || isEnemyBed(pos)) {
                                beds.add(pos);
                            }
                        }
                    }
                }
            }
        }
        
        return beds;
    }
    
    private BlockPos selectBestBed(List<BlockPos> beds) {
        if (beds.isEmpty()) return null;
        
        BlockPos bestBed = null;
        double bestScore = Double.MAX_VALUE;
        
        for (BlockPos bed : beds) {
            double score = calculateBedPriority(bed);
            if (score < bestScore) {
                bestScore = score;
                bestBed = bed;
            }
        }
        
        return bestBed;
    }
    
    private double calculateBedPriority(BlockPos bed) {
        Vec3d playerPos = mc.player.getPos();
        double distance = playerPos.distanceTo(Vec3d.ofCenter(bed));
        
        // Prioritize closer beds
        double score = distance;
        
        // Lower score for beds with fewer blocks around them (easier to break)
        int surroundingBlocks = countSurroundingBlocks(bed);
        score += surroundingBlocks * 2;
        
        // Prioritize beds that players are near
        boolean hasNearbyPlayer = hasNearbyEnemyPlayers(bed, 10);
        if (hasNearbyPlayer) {
            score -= 5; // Higher priority
        }
        
        return score;
    }
    
    private void breakBed(BlockPos bedPos) {
        if (mc.player == null || mc.interactionManager == null) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBreakTime < breakDelay) return;
        
        // Switch to best tool if enabled
        if (autoSwitch) {
            selectBestTool(bedPos);
        }
        
        // Break surrounding blocks first if enabled
        if (surroundBreak) {
            breakSurroundingBlocks(bedPos);
        }
        
        // Break the bed
        if (instantBreak) {
            performInstantBreak(bedPos);
        } else {
            performNormalBreak(bedPos);
        }
        
        lastBreakTime = currentTime;
    }
    
    private void performInstantBreak(BlockPos bedPos) {
        if (mc.interactionManager == null) return;
        
        // Instant break technique
        Direction side = getOptimalSide(bedPos);
        Vec3d hitVec = Vec3d.ofCenter(bedPos);
        BlockHitResult hitResult = new BlockHitResult(hitVec, side, bedPos, false);
        
        // Multiple break attempts for instant breaking
        for (int i = 0; i < 3; i++) {
            mc.interactionManager.updateBlockBreakingProgress(bedPos, side);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        
        // Force break
        mc.interactionManager.breakBlock(bedPos);
    }
    
    private void performNormalBreak(BlockPos bedPos) {
        if (mc.interactionManager == null) return;
        
        Direction side = getOptimalSide(bedPos);
        
        // Start breaking
        if (currentTarget == null || !currentTarget.equals(bedPos)) {
            currentTarget = bedPos;
            breakingTicks = 0;
            mc.interactionManager.attackBlock(bedPos, side);
        }
        
        // Continue breaking
        mc.interactionManager.updateBlockBreakingProgress(bedPos, side);
        breakingTicks++;
        
        // Check if broken
        if (mc.world.getBlockState(bedPos).isAir()) {
            currentTarget = null;
            breakingTicks = 0;
        }
    }
    
    private void breakSurroundingBlocks(BlockPos bedPos) {
        // Break blocks that might be protecting the bed
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = bedPos.offset(direction);
            Block adjacentBlock = mc.world.getBlockState(adjacentPos).getBlock();
            
            // Break common defense blocks
            if (isDefenseBlock(adjacentBlock)) {
                Direction side = getOptimalSide(adjacentPos);
                Vec3d hitVec = Vec3d.ofCenter(adjacentPos);
                BlockHitResult hitResult = new BlockHitResult(hitVec, side, adjacentPos, false);
                
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
            }
        }
    }
    
    private boolean isDefenseBlock(Block block) {
        return block == Blocks.WOOL ||
               block == Blocks.COBBLESTONE ||
               block == Blocks.STONE ||
               block == Blocks.END_STONE ||
               block == Blocks.OBSIDIAN ||
               block.toString().contains("wool") ||
               block.toString().contains("concrete") ||
               block.toString().contains("terracotta");
    }
    
    private void selectBestTool(BlockPos bedPos) {
        if (mc.player == null) return;
        
        Block bedBlock = mc.world.getBlockState(bedPos).getBlock();
        
        // Find the best tool for breaking beds
        int bestSlot = -1;
        float bestSpeed = 0;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            
            float speed = stack.getMiningSpeedMultiplier(mc.world.getBlockState(bedPos));
            
            // Prefer axes for wooden beds, pickaxes for others
            if (stack.getItem() == Items.DIAMOND_AXE ||
                stack.getItem() == Items.IRON_AXE ||
                stack.getItem() == Items.STONE_AXE ||
                stack.getItem() == Items.WOODEN_AXE ||
                stack.getItem() == Items.NETHERITE_AXE) {
                speed += 5; // Bonus for axes
            }
            
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        
        if (bestSlot != -1) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }
    
    private Direction getOptimalSide(BlockPos pos) {
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d blockCenter = Vec3d.ofCenter(pos);
        Vec3d direction = blockCenter.subtract(playerPos).normalize();
        
        // Find the side closest to the player's look direction
        Direction bestSide = Direction.UP;
        double bestDot = -1;
        
        for (Direction side : Direction.values()) {
            Vec3d sideVec = Vec3d.of(side.getVector());
            double dot = direction.dotProduct(sideVec);
            if (dot > bestDot) {
                bestDot = dot;
                bestSide = side;
            }
        }
        
        return bestSide;
    }
    
    private boolean isEnemyBed(BlockPos bedPos) {
        // Simple check - if bed is not near player's spawn, consider it enemy
        // In a real implementation, you'd check team data
        Vec3d playerPos = mc.player.getPos();
        double distanceFromPlayer = playerPos.distanceTo(Vec3d.ofCenter(bedPos));
        
        return distanceFromPlayer > 20; // Assume beds >20 blocks away are enemy beds
    }
    
    private int countSurroundingBlocks(BlockPos bedPos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = bedPos.offset(direction);
            if (!mc.world.getBlockState(adjacentPos).isAir()) {
                count++;
            }
        }
        return count;
    }
    
    private boolean hasNearbyEnemyPlayers(BlockPos bedPos, double checkRange) {
        Vec3d bedCenter = Vec3d.ofCenter(bedPos);
        Box searchBox = new Box(bedCenter.subtract(checkRange, checkRange, checkRange),
                               bedCenter.add(checkRange, checkRange, checkRange));
        
        return !mc.world.getOtherEntities(mc.player, searchBox).isEmpty();
    }
    
    private void processQueuedBeds() {
        if (queuedBeds.isEmpty()) return;
        
        // Process one queued bed per tick
        BlockPos queuedBed = queuedBeds.remove(0);
        if (mc.world.getBlockState(queuedBed).getBlock() instanceof BedBlock) {
            breakBed(queuedBed);
        }
    }
    
    // Public methods for external control
    public void queueBedBreak(BlockPos bedPos) {
        if (!queuedBeds.contains(bedPos)) {
            queuedBeds.add(bedPos);
        }
    }
    
    public void clearQueue() {
        queuedBeds.clear();
    }
    
    // Getters and setters
    public double getRange() { return range; }
    public void setRange(double range) { this.range = Math.max(1.0, Math.min(10.0, range)); }
    
    public boolean isInstantBreak() { return instantBreak; }
    public void setInstantBreak(boolean instantBreak) { this.instantBreak = instantBreak; }
    
    public boolean isAutoSwitch() { return autoSwitch; }
    public void setAutoSwitch(boolean autoSwitch) { this.autoSwitch = autoSwitch; }
    
    public boolean isOnlyEnemyBeds() { return onlyEnemyBeds; }
    public void setOnlyEnemyBeds(boolean onlyEnemyBeds) { this.onlyEnemyBeds = onlyEnemyBeds; }
    
    public boolean isSurroundBreak() { return surroundBreak; }
    public void setSurroundBreak(boolean surroundBreak) { this.surroundBreak = surroundBreak; }
    
    public boolean isPredictiveBreaking() { return predictiveBreaking; }
    public void setPredictiveBreaking(boolean predictiveBreaking) { this.predictiveBreaking = predictiveBreaking; }
    
    public int getBreakDelay() { return breakDelay; }
    public void setBreakDelay(int breakDelay) { this.breakDelay = Math.max(0, Math.min(1000, breakDelay)); }
}