package me.Kesims.FoxSnow;


import me.Kesims.FoxSnow.commands.foxSnow;
import me.Kesims.FoxSnow.events.snowmanEffectEvents;
import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.files.disabledPlayers;
import me.Kesims.FoxSnow.files.messages;
import me.Kesims.FoxSnow.hooks.worldGuardHook;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.pluginData.snowmanBlocks;
import me.Kesims.FoxSnow.tabCompleters.foxSnowTabCompleter;
import me.Kesims.FoxSnow.tasks.autoSave;
import me.Kesims.FoxSnow.tasks.snowTask;
import me.Kesims.FoxSnow.utils.checkBlockIntegrity;
import me.Kesims.FoxSnow.utils.checkParticleIntegrity;
import me.Kesims.FoxSnow.utils.placeholder;
import me.Kesims.FoxSnow.utils.report;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;


public class main extends JavaPlugin {
    private final ArrayList<BukkitRunnable> taskPool = new ArrayList<>();

    @Override
    public void onEnable() {
        setupCommands();
        setupTabCompleters();
        setupData();
        checkParticleIntegrity.check();
        checkBlockIntegrity.check();
        setupTasks();
        setupPlaceholders();
        setupEvents();
        report.info("Plugin loaded!");
    }

    @Override
    public void onLoad() {
        setupFiles();
        setupHooks();
    }

    @Override
    public void onDisable() {
        dataStorage.saveDisabledToStorage();
        disabledPlayers.save();
        if(snowmanBlocks.blockList.size() > 0) snowmanBlocks.emergencyCleanup();
        for(BukkitRunnable b : taskPool)
            b.cancel();
        report.info("Plugin disabled!");
    }

    public void setupFiles() {
        if(!getDataFolder().exists()) getDataFolder().mkdirs();
        config.setup();
        messages.setup();
        File dataDir = new File(getDataFolder(), "pluginData");
        if(!dataDir.exists()) dataDir.mkdir();
        disabledPlayers.setup();
    }
    public void setupData() {
        dataStorage.loadDisabledfromStorage();
    }
    public void setupCommands() {
        getCommand("foxsnow").setExecutor(new foxSnow());
    }
    public void setupTabCompleters() {
        getCommand("foxsnow").setTabCompleter(new foxSnowTabCompleter());
    }
    public void setupTasks() {
        snowTask s = new snowTask();
        s.runTaskTimerAsynchronously(this, 100, 11L); //every 0.55 seconds
        taskPool.add(s);

        if(config.get().getBoolean("auto-save-data")) {
            autoSave a = new autoSave();
            a.runTaskTimerAsynchronously(this, 6000, 6000L); //every 5 minutes
            taskPool.add(a);
        }
    }
    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new snowmanEffectEvents(), this);
    }
    private void setupPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new placeholder().register();
        }
    }
    private void setupHooks() {
        //WorldGuard hook
        if(config.get().getBoolean("hooks.worldguard")) {
            if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null || Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
                worldGuardHook.initialize();
            }
            else report.warn("WorldGuard hook is enabled, plugin was not found though! It is recommended to disable the hook in config.yml in order to improve FoxSnow performance!");
        }

    }
}
