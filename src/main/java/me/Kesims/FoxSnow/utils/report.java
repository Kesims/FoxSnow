package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;

import java.util.logging.Logger;

public class report
{
    private static Logger l = misc.plugin.getLogger();

    public static void info(String message)
    {
        l.info(chat.colored(message));
    }

    public static void success(String message)
    {
        l.info(chat.colored("&a" + message));
    }

    public static void error(String message)
    {
        l.severe(chat.colored(message));
    }

    public static void warn(String message)
    {
        l.warning(chat.colored(message));
    }

    public static void debug(String message)
    {
        if(config.get().getBoolean("show-debug-messages")) l.info(chat.colored("&5" + message));
    }
}
