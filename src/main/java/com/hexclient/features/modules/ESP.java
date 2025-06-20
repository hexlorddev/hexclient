package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * ESP - Extra Sensory Perception
 * Highlights entities and objects through walls
 * Common visual feature in most Minecraft clients
 */
public class ESP extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // ESP settings
    private boolean players = true;
    private boolean mobs = true;
    private boolean animals = false;
    private boolean items = true;
    private boolean throughWalls = true;
    private double range = 64.0;
    
    // Colors (ARGB format)
    private int playersColor = 0xFF00FF00; // Green
    private int mobsColor = 0xFFFF0000;    // Red
    private int animalsColor = 0xFF0080FF; // Light Blue  
    private int itemsColor = 0xFFFFFF00;   // Yellow
    
    public ESP() {
        super("ESP", "Highlights entities through walls", FeatureCategory.VISUAL);
    }
    
    @Override
    public void onRender() {
        if (mc.player == null || mc.world == null) return;
        
        MatrixStack matrices = new MatrixStack();
        VertexConsumerProvider.Immediate vertexConsumers = mc.getBufferBuilders().getEntityVertexConsumers();
        
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (mc.player.distanceTo(entity) > range) continue;
            
            if (!shouldRenderEntity(entity)) continue;
            
            int color = getEntityColor(entity);
            if (color == 0) continue;
            
            renderEntityESP(entity, matrices, vertexConsumers, cameraPos, color);
        }
        
        vertexConsumers.draw();
    }
    
    private boolean shouldRenderEntity(Entity entity) {
        if (entity instanceof PlayerEntity && players) return true;
        if (entity instanceof Monster && mobs) return true;
        if (entity instanceof AnimalEntity && animals) return true;
        if (entity instanceof ItemEntity && items) return true;
        return false;
    }
    
    private int getEntityColor(Entity entity) {
        if (entity instanceof PlayerEntity) return playersColor;
        if (entity instanceof Monster) return mobsColor;
        if (entity instanceof AnimalEntity) return animalsColor;
        if (entity instanceof ItemEntity) return itemsColor;
        return 0;
    }
    
    private void renderEntityESP(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                Vec3d cameraPos, int color) {
        
        matrices.push();
        
        // Translate to entity position relative to camera
        Vec3d entityPos = entity.getPos();
        matrices.translate(
            entityPos.x - cameraPos.x,
            entityPos.y - cameraPos.y, 
            entityPos.z - cameraPos.z
        );
        
        // Get entity bounding box
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        
        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        
        // Render filled box with transparency
        VertexConsumer bufferFilled = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
        WorldRenderer.drawBox(matrices, bufferFilled, 
            box.minX, box.minY, box.minZ,
            box.maxX, box.maxY, box.maxZ,
            red, green, blue, alpha * 0.3f);
        
        // Render outline
        VertexConsumer bufferOutline = vertexConsumers.getBuffer(RenderLayer.getLines());
        WorldRenderer.drawBox(matrices, bufferOutline,
            box.minX, box.minY, box.minZ,  
            box.maxX, box.maxY, box.maxZ,
            red, green, blue, alpha);
        
        matrices.pop();
    }
    
    // Settings getters and setters
    public boolean isPlayers() { return players; }
    public void setPlayers(boolean players) { this.players = players; }
    
    public boolean isMobs() { return mobs; }
    public void setMobs(boolean mobs) { this.mobs = mobs; }
    
    public boolean isAnimals() { return animals; }
    public void setAnimals(boolean animals) { this.animals = animals; }
    
    public boolean isItems() { return items; }
    public void setItems(boolean items) { this.items = items; }
    
    public boolean isThroughWalls() { return throughWalls; }
    public void setThroughWalls(boolean throughWalls) { this.throughWalls = throughWalls; }
    
    public double getRange() { return range; }
    public void setRange(double range) { this.range = Math.max(1.0, Math.min(256.0, range)); }
    
    public int getPlayersColor() { return playersColor; }
    public void setPlayersColor(int playersColor) { this.playersColor = playersColor; }
    
    public int getMobsColor() { return mobsColor; }
    public void setMobsColor(int mobsColor) { this.mobsColor = mobsColor; }
    
    public int getAnimalsColor() { return animalsColor; }
    public void setAnimalsColor(int animalsColor) { this.animalsColor = animalsColor; }
    
    public int getItemsColor() { return itemsColor; }
    public void setItemsColor(int itemsColor) { this.itemsColor = itemsColor; }
}