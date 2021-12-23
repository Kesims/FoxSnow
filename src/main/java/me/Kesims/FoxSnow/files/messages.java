package me.Kesims.FoxSnow.files;

import me.Kesims.FoxSnow.utils.misc;
import me.Kesims.FoxSnow.utils.report;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class messages
{
    private static File file;
    private static FileConfiguration messages;


    public static void setup()
    {
        file = new File(misc.plugin.getDataFolder(), "messages.yml");

        if (!file.exists())
        {
            try
            {
                misc.plugin.saveResource("messages.yml", false);
            }
            catch(Exception e)
            {
                report.error("Couldn't create messages.yml file!");
            }
        }
        messages = YamlConfiguration.loadConfiguration(file);
        autoUpdate();
    }

    public static void autoUpdate()
    {
        try
        {
            if(messages == null) setup();
            InputStreamReader d = new InputStreamReader(misc.plugin.getResource("messages.yml"), StandardCharsets.UTF_8);
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(d);
            boolean wasUpdated = false;
            for(String key : defaults.getKeys(false))
            {
                if(!messages.contains(key))
                {
                    messages.addDefault(key, defaults.get(key));
                    report.warn("messages.yml updated, new message added: " + defaults.get(key));
                    wasUpdated = true;
                }
            }
            messages.options().copyDefaults(true);
            messages.options().header("#########################\n" +
                    " MESSAGES CONFIGUTATION #\n" +
                    "#########################\n");
            messages.options().copyHeader(true);
            if(wasUpdated) save();
        }
        catch (Exception e)
        {
            report.error(e.getMessage());
        }
    }

    public static FileConfiguration get()
    {
        return messages;
    }

    public static void save()
    {
        try
        {
            messages.save(file);
        }
        catch (Exception e)
        {
            report.error("Couldn't save messages.yml");
        }
    }

    public static void reload()
    {
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public static String getMessage(String path)
    {
        String r = get().getString(path);
        if(r == null)
        {
            report.error("Unable to find message in your configuration, please, check that your language version has all the required messages configured!");
            return messages.getString("message-not-found");
        }
        return r;
    }
}
