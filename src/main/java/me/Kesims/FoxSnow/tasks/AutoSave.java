package me.Kesims.FoxSnow.tasks;

import me.Kesims.FoxSnow.files.DisabledPlayers;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.utils.Report;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSave extends BukkitRunnable {
    @Override
    public void run() {
        DataStorage.saveDisabledToStorage();
        DisabledPlayers.save();
        Report.debug("Auto-saved plugin data!");
    }
}
