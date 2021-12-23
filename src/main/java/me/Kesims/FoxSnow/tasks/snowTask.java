package me.Kesims.FoxSnow.tasks;

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
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class snowTask extends BukkitRunnable
{
    @Override
    public void run()
    {
        if(!dataStorage.areParticlesOk) return;

        List<Particle> particles = new ArrayList<>();
        for(String ptc : (List<String>) config.get().get("particles"))
        {
            try
            {
                particles.add(Particle.valueOf(ptc));
            }
            catch (Exception e) {}
        }

        for(Player p : Bukkit.getOnlinePlayers())
        {
            Location center = p.getLocation();
            boolean forceShow = false;

            if(hookState.worldGuard)
            {
                com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
                FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(aLoc);

                if(set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable"))) continue;
                if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable"))) forceShow = true;
            }

            if(!forceShow)
            {
                if(dataStorage.disableSnow.contains(p.getName())) continue;
                if(!config.get().getList("enabled-worlds").contains(p.getWorld().getName())) continue;
                if(config.get().getBoolean("require-permission") && !p.hasPermission("foxsnow.show")) continue;
                if(config.get().getBoolean("rain-disable-snow")  && !p.getWorld().isClearWeather()) continue;
                if(p.getWorld().getTime() < config.get().getInt("snowtime.start") || p.getWorld().getTime() > config.get().getInt("snowtime.end")) continue;
            }

            Random gen = new Random();
            int max = config.get().getInt("max-particle-distance");
            for(int i = 0; i < config.get().getInt("particle-count"); i++)
            {
                Location pLoc = center.clone().add(new Vector(gen.nextInt((2*max) + 1) - max, gen.nextInt((2*max) + 1)-max, gen.nextInt((2*max) + 1) - max));
                if(!config.get().getBoolean("snow-under-blocks") && pLoc.getY() < pLoc.getWorld().getHighestBlockAt(pLoc).getY()) continue; // Do not create particle if there is a block above it

                //SPAWN THE PARTICLE
                Particle particle = particles.get(gen.nextInt(particles.size()));
                if(particle.getDataType().equals(Void.class))
                {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05);
                }
                else if(particle.getDataType().equals(Particle.DustOptions.class))
                {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new Particle.DustOptions(Color.WHITE, 1));
                }
                else if(particle.getDataType().equals(Particle.DustTransition.class))
                {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new Particle.DustTransition(Color.WHITE, Color.SILVER, 1));
                }
                else if(particle.getDataType().equals(BlockData.class))
                {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, Material.SNOW_BLOCK.createBlockData());
                }
                else if(particle.getDataType().equals(ItemStack.class))
                {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new ItemStack(Material.SNOWBALL));
                }
            }
        }
    }
}
