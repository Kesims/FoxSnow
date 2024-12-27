package me.Kesims.FoxSnow.pluginData;

import me.Kesims.FoxSnow.utils.Report;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SnowmanBlocks {
    public static List<Block> blockList = new ArrayList<>();

    public static void emergencyCleanup() {
        Report.info("Cleaning left effects from the server...");
        for(Block b : blockList) {
            if(b.getType() == Material.SNOW)
                b.setType(Material.AIR);
        }
        Report.info("Effects cleaned up!");
    }
}
