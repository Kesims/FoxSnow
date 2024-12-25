package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.Config;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.List;

public class CheckParticleIntegrity {
    public static boolean check() {
        boolean validParticles = true;
        for(String ptc : (List<String>) Config.get().get("particles")) {
            try {
                Particle p = Particle.valueOf(ptc);
            }
            catch (Exception e) {
                Report.warn("Particle " + ptc + " is not correctly set up! Is the name correct?");
                Report.debug(e.getMessage());
                validParticles = false;
            }
        }
        if(!validParticles) {
            Report.error("\n&f-----------&c\n&cSome snow particles were disabled, until you fix the issue!&f Please, check your configuration. " +
                    "You can use particles from this list: &6https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html&f " +
                    "Please note, that this list may be a little different depending on your Minecraft version. If you need any help," +
                    "contact us on our Discord server! :) After you fix the configuration, use &6/foxsnow reload &f to load new data.\n-----------\n");
        }

        boolean validBaseColor = true;
        boolean validTransitionColor = true;
        boolean validMaterialColor = true;
        int baseColor = Config.get().getInt("particle-base-color");
        int transitionColor = Config.get().getInt("particle-transition-color");
        String materialColorName = Config.get().getString("particle-material-color");

        if(baseColor < 0 || baseColor > 0xffffff) {
            Report.error("Particle base color value is invalid. Please, check your config.yml and enter a valid RGB value. After" +
                    " changing the configuration, reload the plugin using /foxsnow reload. The default value is 0xFFFFFF");
            validBaseColor = false;
            Config.get().set("particle-base-color", 0xffffff);
        }

        if(transitionColor < 0 || transitionColor > 0xffffff) {
            Report.error("Particle transition color value is invalid. Please, check your config.yml and enter a valid RGB value. After" +
                    " changing the configuration, reload the plugin using /foxsnow reload. The default value is 0xC0C0C0");
            validTransitionColor = false;
            Config.get().set("particle-base-color", 0xc0c0c0);
        }

        try {
            Material material = Material.getMaterial(materialColorName);
        }
        catch (Exception e) {
            Report.error("Invalid particle material color in config.yml. Enter a valid value and reload the plugin using /foxsnow reload. Default value: SNOW_BLOCK.");
            validMaterialColor = false;
            Config.get().set("particle-material.color", "SNOW_BLOCK");
        }

        return validParticles && validBaseColor && validTransitionColor && validMaterialColor;
    }

}
