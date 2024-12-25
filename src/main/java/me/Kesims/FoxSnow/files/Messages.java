package me.Kesims.FoxSnow.files;

import me.Kesims.FoxSnow.utils.Misc;
import me.Kesims.FoxSnow.utils.Report;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Messages {
    private static File file;
    private static FileConfiguration messages;


    public static void setup() {
        file = new File(Misc.plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            try {
                Misc.plugin.saveResource("messages.yml", false);
            }
            catch(Exception e) {
                Report.error("Couldn't create messages.yml file!");
            }
        }
        messages = YamlConfiguration.loadConfiguration(file);
        autoUpdate();
    }

    public static void autoUpdate() {
        try {
            if(messages == null) setup();
            InputStreamReader d = new InputStreamReader(Misc.plugin.getResource("messages.yml"), StandardCharsets.UTF_8);
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(d);
            boolean wasUpdated = false;
            for(String key : defaults.getKeys(true)) {
                if(!messages.contains(key)) {
                    messages.addDefault(key, defaults.get(key));
                    Report.warn("messages.yml updated, new message added: " + defaults.get(key));
                    wasUpdated = true;
                }
            }
            messages.options().copyDefaults(true);
            messages.options().header(
                    "##########################\n" +
                          "# MESSAGES CONFIGUTATION #\n" +
                          "##########################\n");
            messages.options().copyHeader(true);
            if(wasUpdated) save();
        }
        catch (Exception e) {
            Report.error(e.getMessage());
        }
    }

    public static FileConfiguration get()
    {
        return messages;
    }

    public static void save()
    {
        try {
            messages.save(file);
        }
        catch (Exception e) {
            Report.error("Couldn't save messages.yml");
        }
    }

    public static void reload()
    {
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public static String getMessage(String path) {
        String r = get().getString(path);
        if(r == null) {
            Report.error("Unable to find message in your configuration, please, check that your language version has all the required messages configured!");
            return messages.getString("message-not-found");
        }
        return r;
    }
}
