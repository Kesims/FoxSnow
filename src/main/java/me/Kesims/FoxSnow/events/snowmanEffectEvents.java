package me.Kesims.FoxSnow.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.pluginData.hookState;
import me.Kesims.FoxSnow.pluginData.snowmanBlocks;
import me.Kesims.FoxSnow.utils.misc;
import me.Kesims.FoxSnow.utils.snowmanEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class snowmanEffectEvents implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        if(snowmanBlocks.blockList.contains(e.getBlock()) && e.getBlock().getType() == Material.SNOW)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        Bukkit.getScheduler().runTaskAsynchronously(misc.plugin, new Runnable() {
            @Override
            public void run() {
                boolean snowmanEff = config.get().getInt("snowman-effect.range") > 0;

                Player p = e.getPlayer();
                Location center = p.getLocation();
                boolean forceShow = false;

                if(hookState.worldGuard)
                {
                    com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
                    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionQuery query = container.createQuery();
                    ApplicableRegionSet set = query.getApplicableRegions(aLoc);

                    if(set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable"))) return;
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable"))) forceShow = true;
                }

                //Exit conditions - the effect won't be shown to player
                if(!forceShow)
                {
                    if(dataStorage.disableSnow.contains(p.getName())) return;
                    if(!config.get().getList("enabled-worlds").contains(p.getWorld().getName())) return;
                    if(config.get().getBoolean("require-permission") && !p.hasPermission("foxsnow.show")) return;
                    if(config.get().getBoolean("rain-disable-snow")  && !p.getWorld().isClearWeather()) return;
                    if(p.getWorld().getTime() < config.get().getInt("snowtime.start") || p.getWorld().getTime() > config.get().getInt("snowtime.end")) return;
                }
                //Snowman effect
                if(snowmanEff)
                {
                    snowmanEffect.handleEffect(p);
                }
            }
        });
    }
}
