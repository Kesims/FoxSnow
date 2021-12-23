package me.Kesims.FoxSnow.tasks;

import me.Kesims.FoxSnow.files.disabledPlayers;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.utils.report;
import org.bukkit.scheduler.BukkitRunnable;

public class autoSave extends BukkitRunnable {
    @Override
    public void run() {
        dataStorage.saveDisabledToStorage();
        disabledPlayers.save();
        report.debug("Auto-saved plugin data!");
    }
}
