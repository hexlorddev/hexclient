package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Advanced Bot Detection System
 * Detects various types of bots including Bedwars bots, combat bots, and movement bots
 */
public class BotDetector extends Feature {
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Bot detection settings
    private boolean detectMovementBots = true;
    private boolean detectCombatBots = true;
    private boolean detectBedwarsBots = true;
    private boolean detectNamePatterns = true;
    private boolean autoAlert = true;
    
    // Detection thresholds
    private double movementPrecisionThreshold = 0.95; // Very precise movement = bot
    private int repeatedActionThreshold = 5; // Same action repeated
    private double reactionTimeThreshold = 0.1; // Inhuman reaction times
    private int constantSpeedFrames = 30; // Frames of constant speed
    
    // Player tracking data
    private final Map<UUID, BotAnalysisData> playerData = new HashMap<>();
    private final Map<UUID, Boolean> confirmedBots = new HashMap<>();
    
    public BotDetector() {
        super("BotDetector", "Detects and analyzes bot behavior patterns", FeatureCategory.COMBAT);
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Analyze all players in the world
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            
            UUID playerId = player.getUuid();
            BotAnalysisData data = playerData.computeIfAbsent(playerId, k -> new BotAnalysisData());
            
            // Update player data
            updatePlayerData(player, data);
            
            // Perform bot detection checks
            boolean isBotDetected = performBotDetection(player, data);
            
            if (isBotDetected && !confirmedBots.getOrDefault(playerId, false)) {
                confirmedBots.put(playerId, true);
                if (autoAlert) {
                    alertBotDetected(player, data.getDetectionReason());
                }
            }
        }
        
        // Clean up old data
        cleanupOldData();
    }
    
    private void updatePlayerData(PlayerEntity player, BotAnalysisData data) {
        Vec3d currentPos = player.getPos();
        Vec3d currentVelocity = player.getVelocity();
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();
        
        // Update position history
        data.addPosition(currentPos);
        data.addVelocity(currentVelocity);
        data.addRotation(currentYaw, currentPitch);
        
        // Check for specific patterns
        data.updateActionPattern(player);
        data.incrementTickCount();
    }
    
    private boolean performBotDetection(PlayerEntity player, BotAnalysisData data) {
        boolean isBot = false;
        String reason = "";
        
        // Check movement patterns
        if (detectMovementBots && checkMovementBotPattern(data)) {
            isBot = true;
            reason = "Robotic movement pattern detected";
        }
        
        // Check combat patterns
        if (detectCombatBots && checkCombatBotPattern(player, data)) {
            isBot = true;
            reason = "Combat bot behavior detected";
        }
        
        // Check Bedwars specific patterns
        if (detectBedwarsBots && checkBedwarsBotPattern(player, data)) {
            isBot = true;
            reason = "Bedwars bot pattern detected";
        }
        
        // Check name patterns
        if (detectNamePatterns && checkNamePattern(player)) {
            isBot = true;
            reason = "Bot-like username detected";
        }
        
        // Check for inhuman precision
        if (checkInhumanPrecision(data)) {
            isBot = true;
            reason = "Inhuman precision detected";
        }
        
        // Check for repeated actions
        if (checkRepeatedActions(data)) {
            isBot = true;
            reason = "Repeated action pattern detected";
        }
        
        if (isBot) {
            data.setDetectionReason(reason);
        }
        
        return isBot;
    }
    
    private boolean checkMovementBotPattern(BotAnalysisData data) {
        // Check for perfectly straight lines
        if (data.getPositionHistory().size() >= 10) {
            double straightLineAccuracy = calculateStraightLineAccuracy(data.getPositionHistory());
            if (straightLineAccuracy > movementPrecisionThreshold) {
                return true;
            }
        }
        
        // Check for constant speed movement
        if (data.getVelocityHistory().size() >= constantSpeedFrames) {
            if (hasConstantSpeed(data.getVelocityHistory())) {
                return true;
            }
        }
        
        // Check for robotic turning
        if (data.getRotationHistory().size() >= 20) {
            if (hasRoboticRotation(data.getRotationHistory())) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkCombatBotPattern(PlayerEntity player, BotAnalysisData data) {
        // Check for instant reaction times
        if (data.hasInhumanReactionTime(reactionTimeThreshold)) {
            return true;
        }
        
        // Check for perfect aim tracking
        if (data.hasPerfectAimTracking()) {
            return true;
        }
        
        // Check for robotic combat patterns
        if (data.hasRoboticCombatPattern()) {
            return true;
        }
        
        return false;
    }
    
    private boolean checkBedwarsBotPattern(PlayerEntity player, BotAnalysisData data) {
        // Check for typical Bedwars bot behavior
        
        // 1. Extremely fast block placement/breaking
        if (data.hasRapidBlockInteraction()) {
            return true;
        }
        
        // 2. Perfect bridge building patterns
        if (data.hasPerfectBridgePattern()) {
            return true;
        }
        
        // 3. Instant bed breaking with perfect timing
        if (data.hasInstantBedBreaking()) {
            return true;
        }
        
        // 4. Robotic resource collection
        if (data.hasRoboticResourceCollection()) {
            return true;
        }
        
        // 5. Perfect generator camping
        if (data.hasPerfectGeneratorCamping()) {
            return true;
        }
        
        return false;
    }
    
    private boolean checkNamePattern(PlayerEntity player) {
        String name = player.getName().getString().toLowerCase();
        
        // Common bot name patterns
        String[] botPatterns = {
            "^[a-z]{1,3}[0-9]{3,6}$", // Letters followed by numbers
            "^bot[a-z0-9]*$", // Starting with "bot"
            "^[a-z]+_?bot$", // Ending with "bot"
            "^player[0-9]+$", // Player + numbers
            "^user[0-9]+$", // User + numbers
            "^test[a-z0-9]*$", // Starting with "test"
            "^[a-z]{1,2}[0-9]{5,8}$", // Very short letters with many numbers
            "^auto[a-z0-9]*$", // Starting with "auto"
            "^hack[a-z0-9]*$", // Starting with "hack"
            "^cheat[a-z0-9]*$" // Starting with "cheat"
        };
        
        for (String pattern : botPatterns) {
            if (name.matches(pattern)) {
                return true;
            }
        }
        
        // Check for random character sequences
        if (isRandomCharacterSequence(name)) {
            return true;
        }
        
        return false;
    }
    
    private boolean checkInhumanPrecision(BotAnalysisData data) {
        // Check for movements with too much precision
        if (data.getPositionHistory().size() >= 5) {
            for (Vec3d pos : data.getPositionHistory()) {
                // Check if coordinates have suspicious precision
                if (hasInhumanCoordinatePrecision(pos)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean checkRepeatedActions(BotAnalysisData data) {
        return data.getRepeatedActionCount() >= repeatedActionThreshold;
    }
    
    private void alertBotDetected(PlayerEntity player, String reason) {
        String message = "§c[BotDetector] §f" + player.getName().getString() + " §cdetected as bot: §f" + reason;
        
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        }
    }
    
    private void cleanupOldData() {
        // Remove data for players who are no longer in the world
        playerData.entrySet().removeIf(entry -> {
            UUID playerId = entry.getKey();
            return mc.world.getPlayers().stream()
                .noneMatch(player -> player.getUuid().equals(playerId));
        });
        
        confirmedBots.entrySet().removeIf(entry -> {
            UUID playerId = entry.getKey();
            return mc.world.getPlayers().stream()
                .noneMatch(player -> player.getUuid().equals(playerId));
        });
    }
    
    // Utility methods
    private double calculateStraightLineAccuracy(java.util.List<Vec3d> positions) {
        if (positions.size() < 3) return 0.0;
        
        Vec3d start = positions.get(0);
        Vec3d end = positions.get(positions.size() - 1);
        Vec3d direction = end.subtract(start).normalize();
        
        double totalDeviation = 0.0;
        for (int i = 1; i < positions.size() - 1; i++) {
            Vec3d current = positions.get(i);
            Vec3d expectedPos = start.add(direction.multiply(start.distanceTo(current)));
            totalDeviation += current.distanceTo(expectedPos);
        }
        
        double avgDeviation = totalDeviation / (positions.size() - 2);
        return Math.max(0.0, 1.0 - (avgDeviation / 0.1)); // Normalize to 0-1
    }
    
    private boolean hasConstantSpeed(java.util.List<Vec3d> velocities) {
        if (velocities.size() < constantSpeedFrames) return false;
        
        double firstSpeed = velocities.get(0).length();
        for (Vec3d velocity : velocities) {
            if (Math.abs(velocity.length() - firstSpeed) > 0.001) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasRoboticRotation(java.util.List<Float[]> rotations) {
        // Check for perfectly consistent rotation increments
        if (rotations.size() < 10) return false;
        
        float avgYawChange = 0;
        float avgPitchChange = 0;
        int changes = 0;
        
        for (int i = 1; i < rotations.size(); i++) {
            float yawDiff = rotations.get(i)[0] - rotations.get(i-1)[0];
            float pitchDiff = rotations.get(i)[1] - rotations.get(i-1)[1];
            
            avgYawChange += Math.abs(yawDiff);
            avgPitchChange += Math.abs(pitchDiff);
            changes++;
        }
        
        avgYawChange /= changes;
        avgPitchChange /= changes;
        
        // Check if rotation changes are too consistent
        return avgYawChange > 0 && avgYawChange < 0.5 && avgPitchChange < 0.5;
    }
    
    private boolean isRandomCharacterSequence(String name) {
        // Simple heuristic: if name has no vowels or too many consonants in a row
        String vowels = "aeiou";
        int consonantStreak = 0;
        int maxConsonantStreak = 0;
        int vowelCount = 0;
        
        for (char c : name.toCharArray()) {
            if (vowels.indexOf(c) != -1) {
                vowelCount++;
                consonantStreak = 0;
            } else if (Character.isLetter(c)) {
                consonantStreak++;
                maxConsonantStreak = Math.max(maxConsonantStreak, consonantStreak);
            }
        }
        
        return maxConsonantStreak > 4 || (name.length() > 4 && vowelCount == 0);
    }
    
    private boolean hasInhumanCoordinatePrecision(Vec3d pos) {
        // Check if coordinates have too many decimal places (suspicious precision)
        String xStr = String.valueOf(pos.x);
        String yStr = String.valueOf(pos.y);
        String zStr = String.valueOf(pos.z);
        
        return hasExcessiveDecimalPrecision(xStr) || 
               hasExcessiveDecimalPrecision(yStr) || 
               hasExcessiveDecimalPrecision(zStr);
    }
    
    private boolean hasExcessiveDecimalPrecision(String coord) {
        if (!coord.contains(".")) return false;
        
        String decimal = coord.substring(coord.indexOf(".") + 1);
        return decimal.length() > 10; // More than 10 decimal places is suspicious
    }
    
    // Public API methods
    public boolean isPlayerBot(PlayerEntity player) {
        return confirmedBots.getOrDefault(player.getUuid(), false);
    }
    
    public boolean isPlayerBot(UUID playerId) {
        return confirmedBots.getOrDefault(playerId, false);
    }
    
    public void markPlayerAsBot(PlayerEntity player, String reason) {
        confirmedBots.put(player.getUuid(), true);
        BotAnalysisData data = playerData.get(player.getUuid());
        if (data != null) {
            data.setDetectionReason(reason);
        }
    }
    
    public void clearBotStatus(PlayerEntity player) {
        confirmedBots.remove(player.getUuid());
    }
    
    // Getters and setters
    public boolean isDetectMovementBots() { return detectMovementBots; }
    public void setDetectMovementBots(boolean detectMovementBots) { this.detectMovementBots = detectMovementBots; }
    
    public boolean isDetectCombatBots() { return detectCombatBots; }
    public void setDetectCombatBots(boolean detectCombatBots) { this.detectCombatBots = detectCombatBots; }
    
    public boolean isDetectBedwarsBots() { return detectBedwarsBots; }
    public void setDetectBedwarsBots(boolean detectBedwarsBots) { this.detectBedwarsBots = detectBedwarsBots; }
    
    public boolean isDetectNamePatterns() { return detectNamePatterns; }
    public void setDetectNamePatterns(boolean detectNamePatterns) { this.detectNamePatterns = detectNamePatterns; }
    
    public boolean isAutoAlert() { return autoAlert; }
    public void setAutoAlert(boolean autoAlert) { this.autoAlert = autoAlert; }
}