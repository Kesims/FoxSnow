package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;
import org.bukkit.Particle;

import java.util.List;

public class checkParticleIntegrity {
    public static boolean check() {
        boolean result = true;
        for(String ptc : (List<String>) config.get().get("particles")) {
            try {
                Particle p = Particle.valueOf(ptc);
            }
            catch (Exception e) {
                report.warn("Particle " + ptc + " is not correctly set up! Is the name correct?");
                report.debug(e.getMessage());
                result = false;
            }
        }
        if(!result) report.error("\n&f-----------&c\n&cSome snow particles were disabled, until you fix the issue!&f Please, check your configuration. " +
                "You can use particles from this list: &6https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html&f " +
                "Please note, that this list may be a little different depending on your Minecraft version. If you need any help," +
                "contact us on our Discord server! :) After you fix the configuration, use &6/foxsnow reload &f to load new data.\n-----------\n");
        return result;
    }

}
