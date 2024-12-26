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

    private static int range = 0;
    private static int duration = 0;
    private static boolean destroyable = false;
    private static boolean snowUnderBlocks = false;

    public static void loadSnowmanEffectConfigurationValues() {
        range = Config.get().getInt("snowman-effect.range");
        duration = Config.get().getInt("snowman-effect.duration");
        destroyable = Config.get().getBoolean("snowman-effect.destroyable");
        snowUnderBlocks = Config.get().getBoolean("snowman-effect.under-roof");
    }



    public static List<Block> getApplicableBlocks(Location playerLoc) { //the playerLoc should be Block location ideally
        List<Block> applicableBlocks = new ArrayList<>();
        double rangeSquared = Math.pow(range, 2);
        for(int x = -range; x <= range; x++) {
            for(int z = -range; z <= range; z++) {
                if(Math.pow(z, 2) + Math.pow(x, 2) > rangeSquared) continue; //It should be a circle, so skip values beyond the circle range
                Location tempLoc = playerLoc.clone().add(x, 0, z);
                for(int y = -2; y <= 2; y++) {
                    Block b = tempLoc.clone().add(0, y, 0).getBlock();
                    if(b.getType() == Material.SNOW) break;
                    if(b.getType() == Material.AIR) {
                        Material m = b.getLocation().clone().add(0, -1, 0).getBlock().getType();
                        if(m.isSolid() && m.isOccluding() && m != Material.PACKED_ICE) {
                            if(!snowUnderBlocks && isUnderRoof(b.getLocation())) break;
                            applicableBlocks.add(b);
                            break;
                        }
                    }
                }
            }
        }
        return applicableBlocks;
    }

    public static void handleEffect(Player player) { //This should run async -- task created in event handler
        if(DataStorage.disableSnow.contains(player.getName())) return; // Don't do anything if the effect is disabled

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
