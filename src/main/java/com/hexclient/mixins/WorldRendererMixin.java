package com.hexclient.mixins;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for WorldRenderer
 * Provides hooks for world rendering modifications
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    // World rendering hooks can be added here
}