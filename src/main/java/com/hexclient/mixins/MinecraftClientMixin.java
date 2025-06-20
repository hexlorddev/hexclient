package com.hexclient.mixins;

import com.hexclient.core.HexClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for MinecraftClient
 * Provides hooks into the main client tick loop
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Called every client tick
        if (HexClient.getInstance() != null) {
            HexClient.getInstance().getFeatureManager().onTick();
        }
    }
}