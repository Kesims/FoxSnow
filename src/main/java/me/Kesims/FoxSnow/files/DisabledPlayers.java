package me.Kesims.FoxSnow.files;

import me.Kesims.FoxSnow.utils.Misc;
import me.Kesims.FoxSnow.utils.Report;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DisabledPlayers
{
    private static File file;
    private static FileConfiguration disabledPlayers;


    public static void setup() {
        file = new File(Misc.plugin.getDataFolder()+ "/pluginData", "disabledPlayers.yml");

        if (!file.exists()) { //Check if the file exists, else create new one
            try {
                file.createNewFile();
            }
            catch(Exception e) {
                Report.error("Couldn't create disabledPlayers.yml!");
            }
        }
        disabledPlayers = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get()
    {
        return disabledPlayers;
    }

    public static void save()
    {
        try {
            disabledPlayers.save(file);
        }
        catch (Exception e) {
            Report.error("Couldn't save disabledPlayers.yml");
        }
    }

    public static void reload() {
        disabledPlayers = YamlConfiguration.loadConfiguration(file);
    }

}
