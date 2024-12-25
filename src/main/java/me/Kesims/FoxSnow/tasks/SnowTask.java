package me.Kesims.FoxSnow.tasks;

import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.utils.EffectEvaluation;
import me.Kesims.FoxSnow.utils.EffectType;
import me.Kesims.FoxSnow.utils.Report;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SnowTask extends BukkitRunnable
{
    @Override
    public void run() {
        if(!DataStorage.areParticlesOk) return;

        // Cache configuration values
        int max = Config.get().getInt("max-particle-distance");
        int particleCount = Config.get().getInt("particle-count");
        boolean snowUnderBlocks = Config.get().getBoolean("snow-under-blocks");


        List<Particle> particles = new ArrayList<>();
        for(String ptc : (List<String>) Config.get().get("particles")) {
            try {
                particles.add(Particle.valueOf(ptc));
            }
            catch (Exception e) {}
        }

        List<Material> roofIgnoredMaterials = new ArrayList<>();
        for (String mat : (List<String>) Config.get().get("roof-ignored-blocks")) {
            try {
                roofIgnoredMaterials.add(Material.valueOf(mat));
            }
            catch (Exception e){}
        }

        Bukkit.getOnlinePlayers().parallelStream().forEach(p -> {
            if(!EffectEvaluation.isEffectApplicable(p, EffectType.SNOW)) return;

            Location center = p.getLocation();

            //Particle snow effect
            Random gen = new Random();
            List<Particle> playerParticles = new ArrayList<>(particles);
            Iterator<Particle> particleIterator = playerParticles.iterator();
            for(int i = 0; i < particleCount; i++) {
                Location pLoc = center.clone().add(new Vector(gen.nextInt((2*max) + 1) - max, gen.nextInt((2*max) + 1)-max, gen.nextInt((2*max) + 1) - max));
                try {
                    Block highestBlock = pLoc.getWorld().getHighestBlockAt(pLoc);
                    boolean skipParticle = false;
                    if(!snowUnderBlocks && pLoc.getY() < highestBlock.getY()) {
                        Location currentBlockLoc = highestBlock.getLocation().clone();
                        while (currentBlockLoc.getBlockY() > pLoc.getBlockY()+2) {
                            Material currentMaterial = currentBlockLoc.getBlock().getType();
                            if(!currentMaterial.isAir() && !roofIgnoredMaterials.contains(currentMaterial)) {
                                skipParticle = true;
                                break;
                            }
                            currentBlockLoc = currentBlockLoc.add(0,-1,0);
                        }
                    }
                    if(skipParticle) continue; // Do not create particle if there is a block above it or when it is ignored as roof
                }
                catch (Exception exc) {
                    Report.debug("Something went wrong, please, contact the developer.");
                }

                //SPAWN THE PARTICLE
                Particle particle = particleIterator.next();
                if(!particleIterator.hasNext()) particleIterator = particles.iterator(); // Reset iterator if end of list is reached
                spawnParticle(p, particle, pLoc);
            }
        });
    }

    private Color getConfigColor(String path) {
        return Color.fromRGB(Config.get().getInt(path));
    }

    private Material getConfigMaterialType() {
        return Material.getMaterial(Objects.requireNonNull(Config.get().getString("particle-material-color")));
    }

    private void spawnParticle(Player p, Particle particle, Location pLoc) {
        if(particle.getDataType().equals(Void.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05);
        }
        else if(particle.getDataType().equals(Particle.DustOptions.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new Particle.DustOptions(getConfigColor("particle-base-color"), 1));
        }
        else if(particle.getDataType().equals(Particle.DustTransition.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new Particle.DustTransition(getConfigColor("particle-base-color"), getConfigColor("particle-transition-color"), 1));
        }
        else if(particle.getDataType().equals(BlockData.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, getConfigMaterialType().createBlockData());
        }
        else if(particle.getDataType().equals(ItemStack.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new ItemStack(Material.SNOWBALL));
        }
    }
}
