package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.Messages;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class Placeholder extends PlaceholderExpansion
{
    @Override
    public String getAuthor()
    {
        return "Kesims";
    }

    @Override
    public String getIdentifier()
    {
        return  "foxsnow";
    }

    @Override
    public String getVersion()
    {
        return "1.6";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("enabled")) {
            return DataStorage.disableSnow.contains(player.getName()) ? Messages.getMessage("disabled-placeholder"): Messages.getMessage("enabled-placeholder");
        }
        return null; // Placeholder is unknown by the Expansion
    }
}
