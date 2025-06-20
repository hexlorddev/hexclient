package com.hexclient.gui;

import com.hexclient.core.HexClient;
import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import com.hexclient.utils.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI Manager for HexClient
 * Handles all client GUI components including Click GUI, HUD, and main menu
 */
public class GuiManager {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final HexClient hexClient = HexClient.getInstance();
    
    // GUI States
    private boolean mainGuiOpen = false;
    private boolean clickGuiOpen = false;
    private boolean hudEnabled = true;
    
    // GUI Components
    private ClickGuiScreen clickGuiScreen;
    private HudRenderer hudRenderer;
    
    public GuiManager() {
        Logger.info("Initializing GUI Manager");
        
        this.clickGuiScreen = new ClickGuiScreen();
        this.hudRenderer = new HudRenderer();
        
        Logger.info("GUI Manager initialized");
    }
    
    public void toggleMainGui() {
        mainGuiOpen = !mainGuiOpen;
        if (mainGuiOpen) {
            openMainGui();
        } else {
            closeMainGui();
        }
    }
    
    public void toggleClickGui() {
        clickGuiOpen = !clickGuiOpen;
        if (clickGuiOpen) {
            openClickGui();
        } else {
            closeClickGui();
        }
    }
    
    public void toggleHud() {
        hudEnabled = !hudEnabled;
        Logger.info("HUD " + (hudEnabled ? "enabled" : "disabled"));
    }
    
    private void openMainGui() {
        // Open main configuration GUI
        Logger.info("Opening main GUI");
        // Implementation would open a configuration screen
    }
    
    private void closeMainGui() {
        Logger.info("Closing main GUI");
        mainGuiOpen = false;
    }
    
    private void openClickGui() {
        if (mc.currentScreen == null) {
            mc.setScreen(clickGuiScreen);
            Logger.info("Opening Click GUI");
        }
    }
    
    private void closeClickGui() {
        if (mc.currentScreen == clickGuiScreen) {
            mc.setScreen(null);
        }
        clickGuiOpen = false;
        Logger.info("Closing Click GUI");
    }
    
    public void renderHud() {
        if (hudEnabled && mc.player != null) {
            hudRenderer.render();
        }
    }
    
    // Getters
    public boolean isMainGuiOpen() { return mainGuiOpen; }
    public boolean isClickGuiOpen() { return clickGuiOpen; }
    public boolean isHudEnabled() { return hudEnabled; }
    
    public ClickGuiScreen getClickGuiScreen() { return clickGuiScreen; }
    public HudRenderer getHudRenderer() { return hudRenderer; }
    
    /**
     * Simple Click GUI Screen implementation
     */
    public class ClickGuiScreen extends Screen {
        
        private final List<CategoryPanel> categoryPanels = new ArrayList<>();
        
        public ClickGuiScreen() {
            super(Text.literal("HexClient Click GUI"));
            
            // Initialize category panels
            int x = 10;
            for (FeatureCategory category : FeatureCategory.values()) {
                CategoryPanel panel = new CategoryPanel(category, x, 10);
                categoryPanels.add(panel);
                x += 120; // Panel width + spacing
            }
        }
        
        @Override
        public void render(net.minecraft.client.util.math.MatrixStack matrices, int mouseX, int mouseY, float delta) {
            // Render dark background
            this.renderBackground(matrices);
            
            // Render category panels
            for (CategoryPanel panel : categoryPanels) {
                panel.render(matrices, mouseX, mouseY, delta);
            }
            
            super.render(matrices, mouseX, mouseY, delta);
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (CategoryPanel panel : categoryPanels) {
                if (panel.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        
        @Override
        public boolean shouldPause() {
            return false; // Don't pause the game
        }
    }
    
    /**
     * Category panel for Click GUI
     */
    public class CategoryPanel {
        private final FeatureCategory category;
        private final int x, y;
        private final int width = 110;
        private final int height = 15;
        private boolean expanded = false;
        
        public CategoryPanel(FeatureCategory category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }
        
        public void render(net.minecraft.client.util.math.MatrixStack matrices, int mouseX, int mouseY, float delta) {
            // Render panel background
            fill(matrices, x, y, x + width, y + height, 0x88000000);
            
            // Render category name
            MinecraftClient.getInstance().textRenderer.draw(matrices, 
                category.getDisplayName(), x + 5, y + 5, 0xFFFFFF);
            
            if (expanded) {
                // Render features in this category
                List<Feature> features = hexClient.getFeatureManager().getFeaturesByCategory(category);
                int featureY = y + height + 2;
                
                for (Feature feature : features) {
                    int color = feature.isEnabled() ? 0x8800FF00 : 0x88FF0000;
                    fill(matrices, x, featureY, x + width, featureY + 12, color);
                    
                    MinecraftClient.getInstance().textRenderer.draw(matrices,
                        feature.getName(), x + 5, featureY + 2, 0xFFFFFF);
                    
                    featureY += 14;
                }
            }
        }
        
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                if (button == 0) { // Left click
                    expanded = !expanded;
                    return true;
                }
            }
            
            if (expanded) {
                // Check feature clicks
                List<Feature> features = hexClient.getFeatureManager().getFeaturesByCategory(category);
                int featureY = y + height + 2;
                
                for (Feature feature : features) {
                    if (mouseX >= x && mouseX <= x + width && 
                        mouseY >= featureY && mouseY <= featureY + 12) {
                        
                        if (button == 0) { // Left click to toggle
                            feature.toggle();
                            return true;
                        }
                    }
                    featureY += 14;
                }
            }
            
            return false;
        }
    }
    
    /**
     * HUD Renderer for displaying client information
     */
    public class HudRenderer {
        
        public void render() {
            if (mc.player == null) return;
            
            // Render enabled features list
            renderEnabledFeatures();
            
            // Render client info
            renderClientInfo();
        }
        
        private void renderEnabledFeatures() {
            List<Feature> enabledFeatures = hexClient.getFeatureManager().getAllFeatures()
                .stream().filter(Feature::isEnabled).toList();
            
            int y = 2;
            for (Feature feature : enabledFeatures) {
                mc.textRenderer.draw(new net.minecraft.client.util.math.MatrixStack(),
                    feature.getName(), 2, y, 0xFFFFFF);
                y += 10;
            }
        }
        
        private void renderClientInfo() {
            String clientInfo = "HexClient v" + HexClient.VERSION;
            int x = mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(clientInfo) - 2;
            
            mc.textRenderer.draw(new net.minecraft.client.util.math.MatrixStack(),
                clientInfo, x, 2, 0x6A5ACD);
        }
    }
}