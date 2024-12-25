package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat
{
    static String prefix = Config.get().getString("plugin-prefix");

    public static String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(Player p, String message) {
        p.sendMessage(Chat.colored(prefix + message));
    }

    public static void sendMessage(CommandSender s, String message) {
        s.sendMessage(Chat.colored(prefix + message));
    }

    public static void reloadPrefix() {
        prefix = Config.get().getString("plugin-prefix");
    }
}
