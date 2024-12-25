package me.Kesims.FoxSnow;


import me.Kesims.FoxSnow.commands.FoxSnow;
import me.Kesims.FoxSnow.events.SnowmanEffectEvents;
import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.files.DisabledPlayers;
import me.Kesims.FoxSnow.files.Messages;
import me.Kesims.FoxSnow.hooks.WorldGuardHook;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.pluginData.SnowmanBlocks;
import me.Kesims.FoxSnow.tabCompleters.FoxSnowTabCompleter;
import me.Kesims.FoxSnow.tasks.AutoSave;
import me.Kesims.FoxSnow.tasks.SnowTask;
import me.Kesims.FoxSnow.utils.CheckBlockIntegrity;
import me.Kesims.FoxSnow.utils.CheckParticleIntegrity;
import me.Kesims.FoxSnow.utils.Placeholder;
import me.Kesims.FoxSnow.utils.Report;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;


public class Main extends JavaPlugin {
    private final ArrayList<BukkitRunnable> taskPool = new ArrayList<>();

    @Override
    public void onEnable() {
        setupCommands();
        setupTabCompleters();
        setupData();
        CheckParticleIntegrity.check();
        CheckBlockIntegrity.check();
        setupTasks();
        setupPlaceholders();
        setupEvents();
        Report.info("Plugin loaded!");
    }

    @Override
    public void onLoad() {
        setupFiles();
        setupHooks();
    }

    @Override
    public void onDisable() {
        DataStorage.saveDisabledToStorage();
        DisabledPlayers.save();
        if(SnowmanBlocks.blockList.size() > 0) SnowmanBlocks.emergencyCleanup();
        for(BukkitRunnable b : taskPool)
            b.cancel();
        Report.info("Plugin disabled!");
    }

    public void setupFiles() {
        if(!getDataFolder().exists()) getDataFolder().mkdirs();
        Config.setup();
        Messages.setup();
        File dataDir = new File(getDataFolder(), "pluginData");
        if(!dataDir.exists()) dataDir.mkdir();
        DisabledPlayers.setup();
    }
    public void setupData() {
        DataStorage.loadDisabledfromStorage();
    }
    public void setupCommands() {
        getCommand("foxsnow").setExecutor(new FoxSnow());
    }
    public void setupTabCompleters() {
        getCommand("foxsnow").setTabCompleter(new FoxSnowTabCompleter());
    }
    public void setupTasks() {
        SnowTask s = new SnowTask();
        s.runTaskTimerAsynchronously(this, 100, 11L); //every 0.55 seconds
        taskPool.add(s);

        if(Config.get().getBoolean("auto-save-data")) {
            AutoSave a = new AutoSave();
            a.runTaskTimerAsynchronously(this, 6000, 6000L); //every 5 minutes
            taskPool.add(a);
        }
    }
    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new SnowmanEffectEvents(), this);
    }
    private void setupPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder().register();
        }
    }
    private void setupHooks() {
        //WorldGuard hook
        if(Config.get().getBoolean("hooks.worldguard")) {
            if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null || Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
                WorldGuardHook.initialize();
            }
            else Report.warn("WorldGuard hook is enabled, plugin was not found though! It is recommended to disable the hook in config.yml in order to improve FoxSnow performance!");
        }

    }
}
