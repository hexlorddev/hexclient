package com.hexclient.utils;

import org.slf4j.LoggerFactory;

/**
 * Centralized logging utility for HexClient
 */
public class Logger {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("HexClient");
    
    public static void info(String message) {
        LOGGER.info("[HexClient] " + message);
    }
    
    public static void warn(String message) {
        LOGGER.warn("[HexClient] " + message);
    }
    
    public static void error(String message) {
        LOGGER.error("[HexClient] " + message);
    }
    
    public static void error(String message, Throwable throwable) {
        LOGGER.error("[HexClient] " + message, throwable);
    }
    
    public static void debug(String message) {
        LOGGER.debug("[HexClient] " + message);
    }
}