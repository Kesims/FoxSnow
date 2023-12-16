package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;
import org.bukkit.Material;

import java.util.List;

public class checkBlockIntegrity {
    public static boolean check() {
        boolean result = true;
        for(String mat : (List<String>) config.get().get("roof-ignored-blocks")) {
            try {
                Material m = Material.valueOf(mat);
            }
            catch (Exception e) {
                report.warn("Material " + mat + " is not correctly set up in the roof-ignored-blocks! Is the name correct?");
                report.debug(e.getMessage());
                result = false;
            }
        }
        if(!result) report.error("\n&f-----------&c\n&cSome block materials in the roof-ignored-blocks were disabled, until you fix the issue!&f Please, check your configuration. " +
                "If you need any help, contact us on our Discord server! :) After you fix the configuration, use &6/foxsnow reload &f to load new data.\n-----------\n");
        return result;
    }

}
