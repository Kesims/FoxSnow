package me.Kesims.FoxSnow.utils.RoofBlock;

import me.Kesims.FoxSnow.files.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.*;

import static me.Kesims.FoxSnow.utils.Misc.plugin;

/**
 * RoofBlockService manages and provides information about roof blocks in the worlds, with ability to cache data to improve performance.
 */
public class RoofBlockService {

    private record CacheEntry(int value, long timestamp) { }

    private static final Map<RoofBlockKey, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static List<Material> roofIgnoredMaterials = new ArrayList<>();

    private static boolean isCacheEnabled = false;
    private static int cacheTime = 10000;

    public static void loadRoofCacheConfigurationValues() {
        isCacheEnabled = Config.get().getBoolean("roof-cache.enabled");
        cacheTime = Config.get().getInt("roof-cache.cache-time") * 1000;
    }


    public static void startRoofCacheClearing() {
        new BukkitRunnable() {
            @Override
            public void run() {
                RoofBlockService.invalidateOldEntries();
            }
        }.runTaskTimerAsynchronously(plugin, 200L, 100L); // 200L represents 10 seconds in ticks (20 ticks per second)
    }

    public static void loadRoofIgnoredMaterials() {
        roofIgnoredMaterials = new ArrayList<>();
        for (String mat : (List<String>) Config.get().get("roof-ignored-blocks")) {
            try {
                roofIgnoredMaterials.add(Material.valueOf(mat));
            } catch (Exception ignored) {}
        }
    }

    private static void cacheRoofY(String worldName, int x, int z, int highestY) {
        RoofBlockKey key = new RoofBlockKey(worldName, x, z);
        cache.put(key, new CacheEntry(highestY, System.currentTimeMillis()));
    }

    public static boolean isUnderRoof(Location location) {
        RoofBlockKey roofBlockKey = new RoofBlockKey(Objects.requireNonNull(location.getWorld()).getName(), location.getBlockX(), location.getBlockZ());
        CacheEntry cacheEntry = cache.get(roofBlockKey);
        Integer highestBlockY = cacheEntry != null ? cacheEntry.value : null;
        if (highestBlockY == null) {
            // need to find out the value and cache it
            Block highestBlock = location.getWorld().getHighestBlockAt(location);
            highestBlockY = highestBlock.getY();

            // However, if we know that the location is above current highest block, there cant be any roof...
            // ... so speed up and don't even fill the cache I guess.
            if (location.getY() > highestBlock.getY()) return false;

            Location currentBlockLoc = highestBlock.getLocation().clone();
            while (currentBlockLoc.getBlockY() > 1) {
                Material currentMaterial = currentBlockLoc.getBlock().getType();
                if (!currentMaterial.isAir() && !roofIgnoredMaterials.contains(currentMaterial)) {
                    highestBlockY = currentBlockLoc.getBlockY();
                    if(isCacheEnabled) cacheRoofY(currentBlockLoc.getWorld().getName(), currentBlockLoc.getBlockX(), currentBlockLoc.getBlockZ(), highestBlockY);
                    break;
                }
                currentBlockLoc = currentBlockLoc.add(0, -1, 0);
            }
        }

        return highestBlockY > location.getY();
    }

    private static void invalidateOldEntries() {
        // Invalidate the cache!
        long currentTime = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> currentTime - entry.getValue().timestamp > cacheTime);
    }
}