package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.pluginData.snowmanBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class snowmanEffect
{
    private static Random rnd = new Random();

    public static List<Block> getApplicableBlocks(Location playerLoc) //the playerLoc should be Block location ideally
    {
        List<Block> applicableBlocks = new ArrayList<>();
        int range = config.get().getInt("snowman-effect.range");
        double rangeSquared = Math.pow(range, 2);
        for(int x = -range; x <= range; x++)
        {
            for(int z = -range; z <= range; z++)
            {
                if(Math.pow(z, 2) + Math.pow(x, 2) > rangeSquared) continue; //It should be a circle, so skip values beyond the circle range
                Location tempLoc = playerLoc.clone().add(x, 0, z);
                for(int y = -2; y <= 2; y++)
                {
                    Block b = tempLoc.clone().add(0, y, 0).getBlock();
                    if(b.getType() == Material.SNOW) break;
                    if(b.getType() == Material.AIR)
                    {
                        Material m = b.getLocation().clone().add(0, -1, 0).getBlock().getType();
                        if(m.isSolid() && m.isOccluding())
                        {
                            applicableBlocks.add(b);
                            break;
                        }
                    }
                }
            }
        }
        return applicableBlocks;
    }

    public static void handleEffect(Player player) //This should run async -- task created in event handler
    {
        if(dataStorage.disableSnow.contains(player.getName())) return; // Don't do anything if the effect is disabled

        List<Block> blocks = getApplicableBlocks(player.getLocation().getBlock().getLocation());

        if(!config.get().getBoolean("snowman-effect.destroyable"))
        {
            snowmanBlocks.blockList.addAll(blocks);
        }

        //Place the snow on the ground around the player
        Bukkit.getScheduler().runTask(misc.plugin, new Runnable() {
            @Override
            public void run() {
                for(Block b : blocks)
                {
                    b.setType(Material.SNOW);
                    Snow s = (Snow) b.getBlockData();
                    s.setLayers(3 - rnd.nextInt(3));
                    b.setBlockData(s);
                }
            }
        });

        //Remove the snow from the fround after defined period
        if(config.get().getInt("snowman-effect.duration") > 0)
        {
            Bukkit.getScheduler().runTaskLater(misc.plugin, new Runnable() {
                @Override
                public void run()
                {
                    for(Block b : blocks)
                    {
                        if(b.getType() == Material.SNOW)
                            b.setType(Material.AIR);
                    }
                    snowmanBlocks.blockList.removeAll(blocks);
                }
            }, config.get().getInt("snowman-effect.duration"));
        }
    }
}
