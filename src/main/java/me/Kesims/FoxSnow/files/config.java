package me.Kesims.FoxSnow.files;

import me.Kesims.FoxSnow.utils.misc;
import me.Kesims.FoxSnow.utils.report;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class config
{
    private static File file;
    private static FileConfiguration config;


    public static void setup() {
        file = new File(misc.plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                misc.plugin.saveResource("config.yml", false);
            }
            catch(Exception e) {
                report.error("Couldn't create config.yml file!");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        autoUpdate();
    }

    public static void autoUpdate() {
        try {
            if(config == null) setup();

            InputStreamReader d = new InputStreamReader(misc.plugin.getResource("config.yml"), StandardCharsets.UTF_8);
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(d);
            boolean wasUpdated = false;
            for(String key : defaults.getKeys(false)) {
                if(!config.contains(key)) {
                    config.addDefault(key, defaults.get(key));
                    report.warn("config.yml updated, new option added: &f" + key + ": " + defaults.get(key));
                    wasUpdated = true;
                }
            }
            config.options().copyDefaults(true);
            config.options().header("\n" +
                    "Plugin made by Kesims, contact me on Discord for any help: Kesims#0001\n" +
                    "Thanks for using my plugin!\n" +
                    "\n" +
                    "##############################\n" +
                    " FOX SNOW CONFIGURATION FILE #\n" +
                    "##############################\n");
            config.options().copyHeader(true);
            if (wasUpdated) save();
        }
        catch (Exception e) {
            report.error(e.getMessage());
        }
    }

    public static FileConfiguration get()
    {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        }
        catch (Exception e) {
            report.error("Couldn't save config.yml");
        }
    }

    public static void reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
