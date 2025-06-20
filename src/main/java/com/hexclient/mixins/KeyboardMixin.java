package com.hexclient.mixins;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for Keyboard
 * Provides hooks for keyboard input handling
 */
@Mixin(Keyboard.class)
public class KeyboardMixin {
    // Keyboard input hooks can be added here
}