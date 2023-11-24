package me.Kesims.FoxSnow.tasks;

import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.utils.effectEvaluation;
import me.Kesims.FoxSnow.utils.effectType;
import me.Kesims.FoxSnow.utils.report;
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
    public void run() {
        if(!dataStorage.areParticlesOk) return;

        List<Particle> particles = new ArrayList<>();
        for(String ptc : (List<String>) config.get().get("particles")) {
            try {
                particles.add(Particle.valueOf(ptc));
            }
            catch (Exception e) {}
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!effectEvaluation.isEffectApplicable(p, effectType.SNOW)) continue;

            Location center = p.getLocation();

            //Particle snow effect
            Random gen = new Random();
            int max = config.get().getInt("max-particle-distance");
            for(int i = 0; i < config.get().getInt("particle-count"); i++) {
                Location pLoc = center.clone().add(new Vector(gen.nextInt((2*max) + 1) - max, gen.nextInt((2*max) + 1)-max, gen.nextInt((2*max) + 1) - max));
                try {
                    if(!config.get().getBoolean("snow-under-blocks") && pLoc.getY() < pLoc.getWorld().getHighestBlockAt(pLoc).getY()) continue; // Do not create particle if there is a block above it
                }
                catch (Exception exc) {
                    report.debug("Something went wrong, please, contact the developer.");
                }

                //SPAWN THE PARTICLE
                Particle particle = particles.get(gen.nextInt(particles.size()));
                if(particle.getDataType().equals(Void.class)) {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05);
                }
                else if(particle.getDataType().equals(Particle.DustOptions.class)) {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new Particle.DustOptions(Color.WHITE, 1));
                }
                else if(particle.getDataType().equals(Particle.DustTransition.class)) {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new Particle.DustTransition(Color.WHITE, Color.SILVER, 1));
                }
                else if(particle.getDataType().equals(BlockData.class)) {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, Material.SNOW_BLOCK.createBlockData());
                }
                else if(particle.getDataType().equals(ItemStack.class)) {
                    p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new ItemStack(Material.SNOWBALL));
                }
            }
        }
    }
}
