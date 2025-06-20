package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * AutoCollector - Automated resource collection with bot-level efficiency
 * Optimized for Bedwars, Skywars, and resource gathering
 */
public class AutoCollector extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Collection settings
    private double range = 10.0;
    private boolean autoPickup = true;
    private boolean magnetMode = false;
    private boolean priorityItems = true;
    private boolean autoSort = true;
    private boolean dropTrash = true;
    private int collectionSpeed = 1; // Items per tick
    
    // Item priorities (higher = more important)
    private final Map<Item, Integer> itemPriorities = new HashMap<>();
    private final Set<Item> trashItems = new HashSet<>();
    
    // Collection state
    private long lastCollectionTime = 0;
    private int itemsCollected = 0;
    private List<ItemEntity> targetItems = new ArrayList<>();
    
    public AutoCollector() {
        super("AutoCollector", "Automated item collection with bot efficiency", FeatureCategory.MISC);
        initializeItemPriorities();
        initializeTrashItems();
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Find nearby items
        findNearbyItems();
        
        // Collect items
        if (autoPickup) {
            collectItems();
        }
        
        // Sort inventory
        if (autoSort) {
            sortInventory();
        }
        
        // Drop trash items
        if (dropTrash) {
            dropTrashItems();
        }
    }
    
    private void initializeItemPriorities() {
        // Bedwars priorities
        itemPriorities.put(Items.DIAMOND, 100);
        itemPriorities.put(Items.EMERALD, 90);
        itemPriorities.put(Items.GOLD_INGOT, 80);
        itemPriorities.put(Items.IRON_INGOT, 70);
        
        // Combat items
        itemPriorities.put(Items.DIAMOND_SWORD, 95);
        itemPriorities.put(Items.IRON_SWORD, 85);
        itemPriorities.put(Items.DIAMOND_ARMOR_TRIM_SMITHING_TEMPLATE, 90); // Diamond armor
        itemPriorities.put(Items.IRON_HELMET, 75);
        itemPriorities.put(Items.IRON_CHESTPLATE, 75);
        itemPriorities.put(Items.IRON_LEGGINGS, 75);
        itemPriorities.put(Items.IRON_BOOTS, 75);
        
        // Utility items
        itemPriorities.put(Items.BOW, 80);
        itemPriorities.put(Items.ARROW, 60);
        itemPriorities.put(Items.SHIELD, 70);
        itemPriorities.put(Items.TOTEM_OF_UNDYING, 100);
        itemPriorities.put(Items.GOLDEN_APPLE, 85);
        itemPriorities.put(Items.ENCHANTED_GOLDEN_APPLE, 100);
        
        // Building blocks
        itemPriorities.put(Items.OBSIDIAN, 90);
        itemPriorities.put(Items.END_STONE, 80);
        itemPriorities.put(Items.COBBLESTONE, 50);
        itemPriorities.put(Items.OAK_PLANKS, 45);
        
        // Food
        itemPriorities.put(Items.COOKED_BEEF, 40);
        itemPriorities.put(Items.BREAD, 35);
        itemPriorities.put(Items.APPLE, 30);
    }
    
    private void initializeTrashItems() {
        // Items to automatically drop
        trashItems.add(Items.DIRT);
        trashItems.add(Items.COBBLESTONE); // Sometimes considered trash
        trashItems.add(Items.STONE);
        trashItems.add(Items.WOODEN_SWORD);
        trashItems.add(Items.WOODEN_AXE);
        trashItems.add(Items.WOODEN_PICKAXE);
        trashItems.add(Items.WOODEN_SHOVEL);
        trashItems.add(Items.WOODEN_HOE);
        trashItems.add(Items.LEATHER_HELMET);
        trashItems.add(Items.LEATHER_CHESTPLATE);
        trashItems.add(Items.LEATHER_LEGGINGS);
        trashItems.add(Items.LEATHER_BOOTS);
    }
    
    private void findNearbyItems() {
        if (mc.player == null || mc.world == null) return;
        
        targetItems.clear();
        Vec3d playerPos = mc.player.getPos();
        Box searchBox = new Box(playerPos.subtract(range, range, range),
                               playerPos.add(range, range, range));
        
        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox);
        
        for (Entity entity : entities) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getStack();
                
                // Check if item is worth collecting
                if (isWorthCollecting(stack)) {
                    targetItems.add(itemEntity);
                }
            }
        }
        
        // Sort by priority and distance
        targetItems.sort(this::compareItems);
    }
    
    private int compareItems(ItemEntity a, ItemEntity b) {
        // First compare by priority
        int priorityA = itemPriorities.getOrDefault(a.getStack().getItem(), 1);
        int priorityB = itemPriorities.getOrDefault(b.getStack().getItem(), 1);
        
        if (priorityA != priorityB) {
            return Integer.compare(priorityB, priorityA); // Higher priority first
        }
        
        // Then compare by distance
        double distanceA = mc.player.distanceTo(a);
        double distanceB = mc.player.distanceTo(b);
        return Double.compare(distanceA, distanceB);
    }
    
    private boolean isWorthCollecting(ItemStack stack) {
        Item item = stack.getItem();
        
        // Don't collect trash items
        if (trashItems.contains(item)) {
            return false;
        }
        
        // Always collect high-priority items
        if (itemPriorities.getOrDefault(item, 0) > 50) {
            return true;
        }
        
        // Check if we need this item
        return needsItem(item);
    }
    
    private boolean needsItem(Item item) {
        if (mc.player == null) return false;
        
        // Check if inventory has space
        if (hasInventorySpace()) {
            return true;
        }
        
        // Check if we don't have this item yet
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return stack.getCount() < stack.getMaxCount(); // Can stack more
            }
        }
        
        return true; // Don't have this item
    }
    
    private void collectItems() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCollectionTime < 50) return; // Throttle collection
        
        int collected = 0;
        for (ItemEntity itemEntity : targetItems) {
            if (collected >= collectionSpeed) break;
            
            if (magnetMode) {
                magnetItem(itemEntity);
            } else {
                moveTowardsItem(itemEntity);
            }
            
            collected++;
        }
        
        lastCollectionTime = currentTime;
    }
    
    private void magnetItem(ItemEntity itemEntity) {
        if (mc.player == null) return;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d itemPos = itemEntity.getPos();
        
        // Pull item towards player
        Vec3d direction = playerPos.subtract(itemPos).normalize();
        Vec3d newVelocity = direction.multiply(0.3);
        
        itemEntity.setVelocity(newVelocity);
    }
    
    private void moveTowardsItem(ItemEntity itemEntity) {
        if (mc.player == null) return;
        
        Vec3d playerPos = mc.player.getPos();
        Vec3d itemPos = itemEntity.getPos();
        double distance = playerPos.distanceTo(itemPos);
        
        // Move towards item if close enough
        if (distance < 2.0) {
            Vec3d direction = itemPos.subtract(playerPos).normalize();
            Vec3d newVelocity = mc.player.getVelocity().add(direction.multiply(0.1));
            mc.player.setVelocity(newVelocity);
        }
    }
    
    private void sortInventory() {
        if (mc.player == null || mc.interactionManager == null) return;
        
        // Simple sorting - move important items to hotbar
        List<ItemStack> importantItems = new ArrayList<>();
        
        // Find important items in inventory
        for (int i = 9; i < 36; i++) { // Main inventory slots
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && itemPriorities.getOrDefault(stack.getItem(), 0) > 60) {
                importantItems.add(stack);
            }
        }
        
        // Move to hotbar if space available
        for (ItemStack important : importantItems) {
            for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
                ItemStack hotbarStack = mc.player.getInventory().getStack(hotbarSlot);
                if (hotbarStack.isEmpty() || 
                    itemPriorities.getOrDefault(hotbarStack.getItem(), 0) < 
                    itemPriorities.getOrDefault(important.getItem(), 0)) {
                    
                    // Swap items (simplified)
                    // In real implementation, use proper slot clicking
                    break;
                }
            }
        }
    }
    
    private void dropTrashItems() {
        if (mc.player == null) return;
        
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && trashItems.contains(stack.getItem())) {
                // Drop the item
                mc.player.dropItem(stack, true);
                mc.player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }
    
    private boolean hasInventorySpace() {
        if (mc.player == null) return false;
        
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    // Collection statistics
    public int getItemsCollected() {
        return itemsCollected;
    }
    
    public void resetCounter() {
        itemsCollected = 0;
    }
    
    // Item priority management
    public void setItemPriority(Item item, int priority) {
        itemPriorities.put(item, priority);
    }
    
    public void addTrashItem(Item item) {
        trashItems.add(item);
    }
    
    public void removeTrashItem(Item item) {
        trashItems.remove(item);
    }
    
    // Getters and setters
    public double getRange() { return range; }
    public void setRange(double range) { this.range = Math.max(1.0, Math.min(20.0, range)); }
    
    public boolean isAutoPickup() { return autoPickup; }
    public void setAutoPickup(boolean autoPickup) { this.autoPickup = autoPickup; }
    
    public boolean isMagnetMode() { return magnetMode; }
    public void setMagnetMode(boolean magnetMode) { this.magnetMode = magnetMode; }
    
    public boolean isPriorityItems() { return priorityItems; }
    public void setPriorityItems(boolean priorityItems) { this.priorityItems = priorityItems; }
    
    public boolean isAutoSort() { return autoSort; }
    public void setAutoSort(boolean autoSort) { this.autoSort = autoSort; }
    
    public boolean isDropTrash() { return dropTrash; }
    public void setDropTrash(boolean dropTrash) { this.dropTrash = dropTrash; }
    
    public int getCollectionSpeed() { return collectionSpeed; }
    public void setCollectionSpeed(int collectionSpeed) { this.collectionSpeed = Math.max(1, Math.min(10, collectionSpeed)); }
}