package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.Config;

import java.util.logging.Logger;

public class Report
{
    private static Logger l = Misc.plugin.getLogger();

    public static void info(String message) {
        l.info(Chat.colored(message));
    }

    public static void success(String message) {
        l.info(Chat.colored("&a" + message));
    }

    public static void error(String message) {
        l.severe(Chat.colored(message));
    }

    public static void warn(String message) {
        l.warning(Chat.colored(message));
    }

    public static void debug(String message) {
        if(Config.get().getBoolean("show-debug-messages")) l.info(Chat.colored("&5" + message));
    }
}
