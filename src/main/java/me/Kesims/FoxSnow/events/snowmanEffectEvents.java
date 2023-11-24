package me.Kesims.FoxSnow.events;

import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.pluginData.snowmanBlocks;
import me.Kesims.FoxSnow.utils.effectEvaluation;
import me.Kesims.FoxSnow.utils.effectType;
import me.Kesims.FoxSnow.utils.misc;
import me.Kesims.FoxSnow.utils.snowmanEffect;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class snowmanEffectEvents implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(snowmanBlocks.blockList.contains(e.getBlock()) && e.getBlock().getType() == Material.SNOW)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(misc.plugin, new Runnable() {
            @Override
            public void run() {
                Player p = e.getPlayer();

                boolean snowmanEff = config.get().getInt("snowman-effect.range") > 0;
                if(snowmanEff && effectEvaluation.isEffectApplicable(p, effectType.SNOWMAN)) {
                    snowmanEffect.handleEffect(p);
                }
            }
        });
    }
}
