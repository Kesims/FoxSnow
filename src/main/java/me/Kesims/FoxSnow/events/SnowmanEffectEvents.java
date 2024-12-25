package me.Kesims.FoxSnow.events;

import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.pluginData.SnowmanBlocks;
import me.Kesims.FoxSnow.utils.EffectEvaluation;
import me.Kesims.FoxSnow.utils.EffectType;
import me.Kesims.FoxSnow.utils.Misc;
import me.Kesims.FoxSnow.utils.SnowmanEffect;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SnowmanEffectEvents implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(SnowmanBlocks.blockList.contains(e.getBlock()) && e.getBlock().getType() == Material.SNOW)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Misc.plugin, new Runnable() {
            @Override
            public void run() {
                Player p = e.getPlayer();

                boolean snowmanEff = Config.get().getInt("snowman-effect.range") > 0;
                if(snowmanEff && EffectEvaluation.isEffectApplicable(p, EffectType.SNOWMAN)) {
                    SnowmanEffect.handleEffect(p);
                }
            }
        });
    }
}
