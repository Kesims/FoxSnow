package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.messages;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class placeholder extends PlaceholderExpansion
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
            return dataStorage.disableSnow.contains(player.getName()) ? messages.getMessage("disabled-placeholder"):messages.getMessage("enabled-placeholder");
        }
        return null; // Placeholder is unknown by the Expansion
    }
}
