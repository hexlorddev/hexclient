package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * AutoBridge - Automatic bridging for Bedwars and other games
 * Provides bot-level bridging speed and accuracy
 */
public class AutoBridge extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Bridge settings
    private BridgeMode mode = BridgeMode.NORMAL;
    private double bridgeSpeed = 1.0;
    private boolean safetyCheck = true;
    private boolean autoSelectBlocks = true;
    private boolean godBridge = false;
    private boolean moonwalk = false;
    private boolean breezily = false;
    
    // Bridge state
    private boolean bridging = false;
    private Direction bridgeDirection = Direction.NORTH;
    private int blockCounter = 0;
    private long lastPlaceTime = 0;
    
    public enum BridgeMode {
        NORMAL("Normal"),
        FAST("Fast"),
        GODBRIDGE("God Bridge"),
        NINJA("Ninja"),
        BREEZILY("Breezily"),
        MOONWALK("Moonwalk"),
        WITCHLY("Witchly"),
        ANDROMEDA("Andromeda");
        
        private final String name;
        
        BridgeMode(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public AutoBridge() {
        super("AutoBridge", "Automatic bridging with bot-level precision", FeatureCategory.WORLD);
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Check if we should be bridging
        if (shouldBridge()) {
            performBridge();
        }
    }
    
    private boolean shouldBridge() {
        if (mc.player == null) return false;
        
        // Auto-detect when player is about to fall
        BlockPos belowPlayer = BlockPos.ofFloored(mc.player.getPos()).down();
        boolean isOnEdge = mc.world.getBlockState(belowPlayer).isAir();
        
        // Check if player is moving forward and about to need a bridge
        Vec3d velocity = mc.player.getVelocity();
        boolean isMovingForward = velocity.length() > 0.1;
        
        return isOnEdge && isMovingForward && hasBlocks();
    }
    
    private void performBridge() {
        if (!hasBlocks()) return;
        
        // Get the optimal bridging position
        BlockPos targetPos = getBridgePosition();
        if (targetPos == null) return;
        
        // Select blocks if needed
        if (autoSelectBlocks) {
            selectBridgeBlocks();
        }
        
        // Perform the bridge based on mode
        switch (mode) {
            case NORMAL -> performNormalBridge(targetPos);
            case FAST -> performFastBridge(targetPos);
            case GODBRIDGE -> performGodBridge(targetPos);
            case NINJA -> performNinjaBridge(targetPos);
            case BREEZILY -> performBreezilyBridge(targetPos);
            case MOONWALK -> performMoonwalkBridge(targetPos);
            case WITCHLY -> performWitchlyBridge(targetPos);
            case ANDROMEDA -> performAndromedaBridge(targetPos);
        }
    }
    
    private BlockPos getBridgePosition() {
        if (mc.player == null) return null;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d velocity = mc.player.getVelocity();
        
        // Predict where player will be
        Vec3d futurePos = playerPos.add(velocity.multiply(2));
        
        // Find the position to place block
        BlockPos targetPos = BlockPos.ofFloored(futurePos).down();
        
        // Safety check - make sure we're not placing in dangerous spots
        if (safetyCheck && !isSafeToPlace(targetPos)) {
            return null;
        }
        
        return targetPos;
    }
    
    private void performNormalBridge(BlockPos targetPos) {
        // Standard bridging with timing control
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < (long)(500 / bridgeSpeed)) return;
        
        placeBlock(targetPos);
        lastPlaceTime = currentTime;
    }
    
    private void performFastBridge(BlockPos targetPos) {
        // Fast bridging - minimal delay
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 50) return;
        
        placeBlock(targetPos);
        lastPlaceTime = currentTime;
    }
    
    private void performGodBridge(BlockPos targetPos) {
        // God bridge technique - very fast with specific timing
        if (!godBridge) return;
        
        // Simulate god bridge timing
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 25) return;
        
        // God bridge requires specific positioning
        if (mc.player.getY() % 1.0 < 0.1) {
            placeBlock(targetPos);
            lastPlaceTime = currentTime;
        }
    }
    
    private void performNinjaBridge(BlockPos targetPos) {
        // Ninja bridge - sneaking while bridging
        mc.player.setSneaking(true);
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 75) return;
        
        placeBlock(targetPos);
        lastPlaceTime = currentTime;
    }
    
    private void performBreezilyBridge(BlockPos targetPos) {
        // Breezily bridge technique
        if (!breezily) return;
        
        // Fast placement with specific movement
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 30) return;
        
        placeBlock(targetPos);
        
        // Adjust player position for breezily
        if (mc.player != null) {
            Vec3d pos = mc.player.getPos();
            mc.player.setPosition(pos.x, pos.y, pos.z + 0.1);
        }
        
        lastPlaceTime = currentTime;
    }
    
    private void performMoonwalkBridge(BlockPos targetPos) {
        // Moonwalk bridge - bridging while moving backwards
        if (!moonwalk) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 60) return;
        
        // Place block while adjusting movement
        placeBlock(targetPos);
        
        // Moonwalk movement adjustment
        if (mc.player != null) {
            Vec3d velocity = mc.player.getVelocity();
            mc.player.setVelocity(velocity.x * -0.1, velocity.y, velocity.z * -0.1);
        }
        
        lastPlaceTime = currentTime;
    }
    
    private void performWitchlyBridge(BlockPos targetPos) {
        // Witchly bridge technique
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 40) return;
        
        // Multiple block placement for speed
        placeBlock(targetPos);
        
        // Place additional blocks if possible
        BlockPos leftPos = targetPos.offset(Direction.WEST);
        BlockPos rightPos = targetPos.offset(Direction.EAST);
        
        if (isSafeToPlace(leftPos)) placeBlock(leftPos);
        if (isSafeToPlace(rightPos)) placeBlock(rightPos);
        
        lastPlaceTime = currentTime;
    }
    
    private void performAndromedaBridge(BlockPos targetPos) {
        // Andromeda bridge - extremely fast technique
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < 20) return;
        
        // Rapid placement with prediction
        placeBlock(targetPos);
        
        // Place predicted next blocks
        Vec3d velocity = mc.player.getVelocity();
        BlockPos nextPos = targetPos.add((int)velocity.x, 0, (int)velocity.z);
        
        if (isSafeToPlace(nextPos)) {
            placeBlock(nextPos);
        }
        
        lastPlaceTime = currentTime;
    }
    
    private void placeBlock(BlockPos pos) {
        if (mc.player == null || mc.interactionManager == null) return;
        
        // Check if position is already occupied
        if (!mc.world.getBlockState(pos).isAir()) return;
        
        // Find the face to place on
        Direction placementSide = findPlacementSide(pos);
        if (placementSide == null) return;
        
        BlockPos adjacentPos = pos.offset(placementSide);
        
        // Create hit result
        Vec3d hitVec = Vec3d.ofCenter(adjacentPos).add(Vec3d.of(placementSide.getOpposite().getVector()).multiply(0.5));
        BlockHitResult hitResult = new BlockHitResult(hitVec, placementSide.getOpposite(), adjacentPos, false);
        
        // Place the block
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        mc.player.swingHand(Hand.MAIN_HAND);
        
        blockCounter++;
    }
    
    private Direction findPlacementSide(BlockPos pos) {
        // Try all directions to find a solid block to place against
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.offset(direction);
            if (!mc.world.getBlockState(adjacentPos).isAir()) {
                return direction;
            }
        }
        return Direction.DOWN; // Default to down
    }
    
    private boolean hasBlocks() {
        if (mc.player == null) return false;
        
        // Check for blocks in hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (isValidBridgeBlock(block)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isValidBridgeBlock(Block block) {
        // Common bridging blocks
        return block == Blocks.COBBLESTONE ||
               block == Blocks.DIRT ||
               block == Blocks.STONE ||
               block == Blocks.NETHERRACK ||
               block == Blocks.END_STONE ||
               block == Blocks.SANDSTONE ||
               block == Blocks.WOOL ||
               block == Blocks.PLANKS ||
               block.toString().contains("wool") ||
               block.toString().contains("concrete");
    }
    
    private void selectBridgeBlocks() {
        if (mc.player == null) return;
        
        // Find and select the best bridging block
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (isValidBridgeBlock(block)) {
                    mc.player.getInventory().selectedSlot = i;
                    return;
                }
            }
        }
    }
    
    private boolean isSafeToPlace(BlockPos pos) {
        if (!safetyCheck) return true;
        
        // Check if placing here would be safe
        // Don't place in lava, fire, or other dangerous blocks
        Block blockBelow = mc.world.getBlockState(pos.down()).getBlock();
        return blockBelow != Blocks.LAVA && 
               blockBelow != Blocks.FIRE && 
               blockBelow != Blocks.MAGMA_BLOCK;
    }
    
    // Manual bridge control
    public void startBridge(Direction direction) {
        this.bridging = true;
        this.bridgeDirection = direction;
        blockCounter = 0;
    }
    
    public void stopBridge() {
        this.bridging = false;
        if (mc.player != null) {
            mc.player.setSneaking(false);
        }
    }
    
    // Getters and setters
    public BridgeMode getMode() { return mode; }
    public void setMode(BridgeMode mode) { this.mode = mode; }
    
    public double getBridgeSpeed() { return bridgeSpeed; }
    public void setBridgeSpeed(double bridgeSpeed) { this.bridgeSpeed = Math.max(0.1, Math.min(5.0, bridgeSpeed)); }
    
    public boolean isSafetyCheck() { return safetyCheck; }
    public void setSafetyCheck(boolean safetyCheck) { this.safetyCheck = safetyCheck; }
    
    public boolean isAutoSelectBlocks() { return autoSelectBlocks; }
    public void setAutoSelectBlocks(boolean autoSelectBlocks) { this.autoSelectBlocks = autoSelectBlocks; }
    
    public boolean isGodBridge() { return godBridge; }
    public void setGodBridge(boolean godBridge) { this.godBridge = godBridge; }
    
    public boolean isMoonwalk() { return moonwalk; }
    public void setMoonwalk(boolean moonwalk) { this.moonwalk = moonwalk; }
    
    public boolean isBreezily() { return breezily; }
    public void setBreezily(boolean breezily) { this.breezily = breezily; }
    
    public int getBlockCounter() { return blockCounter; }
    public void resetBlockCounter() { this.blockCounter = 0; }
}