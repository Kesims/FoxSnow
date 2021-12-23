package me.Kesims.FoxSnow.utils;

import me.Kesims.FoxSnow.files.config;

import java.util.logging.Logger;

public class report
{
    private static Logger l = misc.plugin.getLogger();

    public static void info(String message)
    {
        l.info(chat.barevne(message));
    }

    public static void success(String message)
    {
        l.info(chat.barevne("&a" + message));
    }

    public static void error(String message)
    {
        l.severe(chat.barevne(message));
    }

    public static void warn(String message)
    {
        l.warning(chat.barevne(message));
    }

    public static void debug(String message)
    {
        if(config.get().getBoolean("show-debug-messages")) l.info(chat.barevne("&5" + message));
    }
}
