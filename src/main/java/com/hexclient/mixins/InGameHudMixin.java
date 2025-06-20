package com.hexclient.mixins;

import com.hexclient.core.HexClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for InGameHud
 * Provides hooks for rendering custom HUD elements
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        // Render HexClient HUD elements
        if (HexClient.getInstance() != null) {
            HexClient.getInstance().getGuiManager().renderHud();
        }
    }
}