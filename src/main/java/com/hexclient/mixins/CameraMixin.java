package com.hexclient.mixins;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for Camera
 * Provides hooks for camera-related features like FreeCam
 */
@Mixin(Camera.class)
public class CameraMixin {
    // Camera hooks can be added here
}