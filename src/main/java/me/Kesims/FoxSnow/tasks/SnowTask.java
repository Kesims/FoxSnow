package me.Kesims.FoxSnow.tasks;

import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.utils.EffectEvaluation;
import me.Kesims.FoxSnow.utils.EffectType;
import me.Kesims.FoxSnow.utils.PerformanceMonitor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.Kesims.FoxSnow.utils.Misc.plugin;
import static me.Kesims.FoxSnow.utils.Misc.random;
import static me.Kesims.FoxSnow.utils.RoofBlock.RoofBlockService.isUnderRoof;

public class SnowTask extends BukkitRunnable {
    // Configuration values
    private static int activeMax = 0;
    private static int configMax = 0;
    private static int configParticleCount = 0;
    private static int activeParticleCount = 0;
    private static boolean snowUnderBlocks = false;
    private static final List<Particle> particles = new ArrayList<>();

    public static void loadSnowTaskConfigurationValues() {
        configMax = Config.get().getInt("max-particle-distance");
        configParticleCount = Config.get().getInt("particle-count");
        snowUnderBlocks = Config.get().getBoolean("snow-under-blocks");

        particles.clear();
        for (String ptc : (List<String>) Config.get().get("particles")) {
            try {
                particles.add(Particle.valueOf(ptc));
            } catch (Exception ignored) {}
        }

        activeMax = configMax;
        activeParticleCount = configParticleCount;
    }

    @Override
    public void run() {
        if (!DataStorage.areParticlesOk) return;

        // Dynamic adjustment of particle distance range
        activeMax = (int) (configMax * PerformanceMonitor.getAdjustmentFactor());
        activeParticleCount = (int) (configParticleCount * Math.pow(PerformanceMonitor.getAdjustmentFactor(), 3));

        for (Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> handlePlayerSnowEffect(player));
        }
    }

    private void handlePlayerSnowEffect(Player player) {
        if (!EffectEvaluation.isEffectApplicable(player, EffectType.SNOW)) return;

        Location center = player.getLocation();
        List<Particle> playerParticles = new ArrayList<>(particles);
        Iterator<Particle> particleIterator = playerParticles.iterator();

        for (int i = 0; i < configParticleCount; i++) {
            Location particleLoc = center.clone().add(
                    random.nextInt((2 * activeMax) + 1) - activeMax,
                    random.nextInt((2 * activeMax) + 1) - activeMax,
                    random.nextInt((2 * activeMax) + 1) - activeMax
            );

            // Skip the particle if it's under a roof and snowUnderBlocks is false
            if (!snowUnderBlocks && isUnderRoof(particleLoc.clone().add(0, -2, 0))) continue;

            // Spawn the particle
            Particle particle = particleIterator.next();
            if (!particleIterator.hasNext()) particleIterator = particles.iterator(); // Reset iterator if end of list is reached
            spawnParticle(player, particle, particleLoc);
        }
    }

    private Color getConfigColor(String path) {
        return Color.fromRGB(Config.get().getInt(path));
    }

    private Material getConfigMaterialType() {
        return Material.getMaterial(Objects.requireNonNull(Config.get().getString("particle-material-color")));
    }

    private void spawnParticle(Player p, Particle particle, Location pLoc) {
        if (particle.getDataType().equals(Void.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05);
        } else if (particle.getDataType().equals(Particle.DustOptions.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05,
                    new Particle.DustOptions(getConfigColor("particle-base-color"), 1));
        } else if (particle.getDataType().equals(Particle.DustTransition.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05,
                    new Particle.DustTransition(getConfigColor("particle-base-color"),
                            getConfigColor("particle-transition-color"), 1));
        } else if (particle.getDataType().equals(BlockData.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, getConfigMaterialType().createBlockData());
        } else if (particle.getDataType().equals(ItemStack.class)) {
            p.spawnParticle(particle, pLoc, 0, 0, -1.8, 0, 0.05, new ItemStack(Material.SNOWBALL));
        }
    }
}
