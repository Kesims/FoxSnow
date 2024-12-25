package me.Kesims.FoxSnow.pluginData;

import me.Kesims.FoxSnow.files.DisabledPlayers;
import me.Kesims.FoxSnow.utils.Report;

import java.util.ArrayList;
import java.util.List;

public class DataStorage
{
    public static List<String> disableSnow = new ArrayList<>();

    public static boolean areParticlesOk = true;




    // SAVES AND LOADS ----------------------------------------------------------------
    public static void loadDisabledfromStorage() {

        if(DisabledPlayers.get().contains("disabled")) {
            disableSnow = DisabledPlayers.get().getStringList("disabled");

        }
        Report.debug("Loaded plugin data!");
    }

    public static void saveDisabledToStorage() {
        DisabledPlayers.get().set("disabled", disableSnow);
    }
}
