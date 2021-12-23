package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class chat
{
    static String prefix = config.get().getString("plugin-prefix");

    public static String barevne(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(Player p, String message)
    {
        p.sendMessage(chat.barevne(prefix + message));
    }

    public static void sendMessage(CommandSender s, String message)
    {
        s.sendMessage(chat.barevne(prefix + message));
    }
}
