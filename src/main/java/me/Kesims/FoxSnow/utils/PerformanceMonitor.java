package me.Kesims.FoxSnow.utils;
import org.bukkit.Bukkit;

import static me.Kesims.FoxSnow.utils.Misc.plugin;

public class PerformanceMonitor {
    private static long lastWindowTime = System.currentTimeMillis();
    private static int tickCount = 0;
    private static double tps = 20.0;

    private static double tpsThreshold = 19.5; // TPS threshold for lag
    private static double reductionFactor = 0.0;
    private static double realAdjustmentFactor = 1.0;

    public static void loadPerformanceMonitorConfigurationValues() {
        tpsThreshold = plugin.getConfig().getDouble("dynamic-performance-adjustment.min-tps");
        reductionFactor = plugin.getConfig().getDouble("dynamic-performance-adjustment.adjustment-factor");
        if (reductionFactor > 1.0 || reductionFactor < 0.0) {
            plugin.getLogger().warning("Invalid reduction factor is configured in the FoxSnow" +
                    " dynamic-performance-adjustment configuration, value needs to be between 0 and 1, using default value.");
            reductionFactor = 0.3;
        }
    }

    public static void trackTPS() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            tickCount += 5; // Increment by 5 since we're running every 5 ticks

            long elapsedTime = currentTime - lastWindowTime; // Time elapsed in milliseconds

            if (elapsedTime >= 15000) { // If 15 seconds have passed
                tps = (tickCount / (elapsedTime / 1000.0)); // Calculate TPS over 15 seconds
                tickCount = 0; // Reset tick count for the new window
                lastWindowTime = currentTime; // Update the window start time
                if (tps < tpsThreshold) {
                    realAdjustmentFactor = 1.0 - reductionFactor; // Reduce the radius
                } else {
                    realAdjustmentFactor = 1.0; // Reset the radius
                }
            }
        }, 5L, 5L); // Runs every 5 ticks
    }

    /**
     * Get the current adjustment factor, radius can then be directly multiplied by this value.
     * @return The current adjustment factor.
     */
    public static double getAdjustmentFactor() {
        return realAdjustmentFactor;
    }
}
