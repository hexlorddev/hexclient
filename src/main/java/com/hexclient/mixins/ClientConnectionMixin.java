package com.hexclient.mixins;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for ClientConnection
 * Provides hooks for network packet handling
 */
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    // Network packet hooks can be added here
}