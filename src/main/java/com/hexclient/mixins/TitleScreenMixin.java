package com.hexclient.mixins;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for TitleScreen
 * Adds HexClient branding to the title screen
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TitleScreen screen = (TitleScreen) (Object) this;
        
        // Add HexClient branding
        String hexClientText = "HexClient v1.0.0";
        int x = screen.width - screen.textRenderer.getWidth(hexClientText) - 2;
        int y = screen.height - 10;
        
        screen.textRenderer.draw(matrices, hexClientText, x, y, 0x6A5ACD);
    }
}