package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.pluginData.SnowmanBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.Kesims.FoxSnow.utils.Misc.random;
import static me.Kesims.FoxSnow.utils.RoofBlock.RoofBlockService.isUnderRoof;

public class SnowmanEffect {

    private static int configRange = 0;
    private static int activeRange = 0;
    private static int duration = 0;
    private static boolean destroyable = false;
    private static boolean snowUnderBlocks = false;

    public static void loadSnowmanEffectConfigurationValues() {
        configRange = Config.get().getInt("snowman-effect.range");
        duration = Config.get().getInt("snowman-effect.duration");
        destroyable = Config.get().getBoolean("snowman-effect.destroyable");
        snowUnderBlocks = Config.get().getBoolean("snowman-effect.under-roof");

        activeRange = configRange;
    }

    public static List<Block> getApplicableBlocks(Location playerLoc) {
        List<Block> applicableBlocks = new ArrayList<>();
        double rangeSquared = Math.pow(activeRange, 2);
        Location tempLoc = playerLoc.clone(); // Reuse this location object

        for (int x = -activeRange; x <= activeRange; x++) {
            for (int z = -activeRange; z <= activeRange; z++) {
                if (x * x + z * z > rangeSquared) continue;

                tempLoc.setX(playerLoc.getX() + x);
                tempLoc.setZ(playerLoc.getZ() + z);

                for (int y = -2; y <= 2; y++) {
                    tempLoc.setY(playerLoc.getY() + y);
                    Block b = tempLoc.getBlock();
                    if (b.getType() == Material.SNOW) break; // Skip entire column
                    if (b.getType() == Material.AIR) {
                        Block below = b.getRelative(0, -1, 0);
                        Material belowType = below.getType();
                        if (belowType.isSolid() && belowType.isOccluding() && belowType != Material.PACKED_ICE) {
                            if (!snowUnderBlocks && isUnderRoof(b.getLocation())) break;
                            applicableBlocks.add(b);
                            break; // Move to the next (x, z) column
                        }
                    }
                }
            }
        }
        return applicableBlocks;
    }

    public static void handleEffect(Player player) { //This should run async -- task created in event handler
        if(DataStorage.disableSnow.contains(player.getName())) return; // Don't do anything if the effect is disabled

        if(!Config.get().getBoolean("dynamic-performance-adjustment.snowfall-only")) activeRange = (int) (configRange * PerformanceMonitor.getAdjustmentFactor()); // Adjust the range based on the performance

        List<Block> blocks = getApplicableBlocks(player.getLocation().getBlock().getLocation());

        if(!destroyable) {
            SnowmanBlocks.blockList.addAll(blocks);
        }

        //Place the snow on the ground around the player
        Bukkit.getScheduler().runTask(Misc.plugin, new Runnable() {
            @Override
            public void run() {
                for(Block b : blocks) {
                    b.setType(Material.SNOW);
                    Snow s = (Snow) b.getBlockData();
                    s.setLayers(3 - random.nextInt(3));
                    b.setBlockData(s);
                }
            }
        });

        //Remove the snow from the fround after defined period
        if(duration > 0) {
            Bukkit.getScheduler().runTaskLater(Misc.plugin, new Runnable() {
                @Override
                public void run() {
                    for(Block b : blocks) {
                        if(b.getType() == Material.SNOW)
                            b.setType(Material.AIR);
                    }
                    SnowmanBlocks.blockList.removeAll(blocks);
                }
            }, duration);
        }
    }
}
