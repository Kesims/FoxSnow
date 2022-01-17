package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.pluginData.snowmanBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class snowmanEffect
{
    public static List<Block> getApplicableBlocks(Location playerLoc) //the playerLoc should be Block location ideally
    {
        List<Block> applicableBlocks = new ArrayList<>();
        double radiusSquared = Math.pow(config.get().getInt("snowman-effect.range"), 2);
        for(int x = -config.get().getInt("snowman-effect.range"); x <= config.get().getInt("snowman-effect.range"); x++)
        {
            for(int z = -config.get().getInt("snowman-effect.range"); z <= config.get().getInt("snowman-effect.range"); z++)
            {
                if(Math.pow(z, 2) + Math.pow(x, 2) > radiusSquared) continue; //It should be a circle, so skip values beyond the circle range
                Location tempLoc = playerLoc.clone().add(x, 0, z);
                for(int y = -2; y <= 2; y++)
                {
                    Block b = tempLoc.clone().add(0, y, 0).getBlock();
                    if(b.getType() == Material.SNOW) break;
                    if(b.getType() == Material.AIR)
                    {
                        if(b.getLocation().clone().add(0, -1, 0).getBlock().getType().isSolid())
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

    public static void handleEffect(Player player) //This should be run async -- task created in event handler
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
                        b.setType(Material.AIR);
                    }
                }
            }, config.get().getInt("snowman-effect.duration"));
        }
    }
}
