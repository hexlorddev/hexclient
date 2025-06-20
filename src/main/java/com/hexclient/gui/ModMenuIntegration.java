package com.hexclient.gui;

import com.hexclient.core.HexClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * ModMenu integration for HexClient
 * Provides configuration screen in the ModMenu interface
 */
public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new HexClientConfigScreen(parent);
    }
    
    /**
     * Configuration screen for HexClient
     */
    public static class HexClientConfigScreen extends Screen {
        
        private final Screen parent;
        
        public HexClientConfigScreen(Screen parent) {
            super(Text.literal("HexClient Configuration"));
            this.parent = parent;
        }
        
        @Override
        protected void init() {
            super.init();
            
            // Add configuration buttons and widgets here
            // For now, this is a basic implementation
        }
        
        @Override
        public void render(net.minecraft.client.util.math.MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            
            // Render title
            drawCenteredText(matrices, this.textRenderer, this.title, 
                this.width / 2, 20, 0xFFFFFF);
            
            // Render HexClient info
            String version = "Version: " + HexClient.VERSION;
            drawCenteredText(matrices, this.textRenderer, version,
                this.width / 2, 40, 0x888888);
            
            String description = "Advanced Minecraft Client with Superior Features";
            drawCenteredText(matrices, this.textRenderer, description,
                this.width / 2, 60, 0x888888);
            
            super.render(matrices, mouseX, mouseY, delta);
        }
        
        @Override
        public void onClose() {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }
    }
}