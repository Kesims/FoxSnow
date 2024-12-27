package me.Kesims.FoxSnow.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class Misc
{
    public static Plugin plugin = Bukkit.getPluginManager().getPlugin("FoxSnow");

    public static final Random random = new Random();
}