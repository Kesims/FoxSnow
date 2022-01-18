package me.Kesims.FoxSnow.pluginData;
;

import me.Kesims.FoxSnow.utils.report;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class snowmanBlocks
{
    public static List<Block> blockList = new ArrayList<>();

    public static void emergencyCleanup()
    {
        report.info("Cleaning left effects from the server...");
        for(Block b : blockList)
        {
            if(b.getType() == Material.SNOW)
                b.setType(Material.AIR);
        }
        report.info("Effects cleaned up!");
    }
}
