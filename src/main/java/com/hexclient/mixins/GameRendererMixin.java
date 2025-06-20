package com.hexclient.mixins;

import com.hexclient.core.HexClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for GameRenderer
 * Provides hooks for rendering-related features
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        // World render hook for ESP and other visual features
        if (HexClient.getInstance() != null) {
            HexClient.getInstance().getFeatureManager().onRender();
        }
    }
}