package me.Kesims.FoxSnow.pluginData;

import me.Kesims.FoxSnow.files.disabledPlayers;
import me.Kesims.FoxSnow.utils.report;

import java.util.ArrayList;
import java.util.List;

public class dataStorage
{
    public static List<String> disableSnow = new ArrayList<>();

    public static boolean areParticlesOk = true;




    // SAVES AND LOADS ----------------------------------------------------------------
    public static void loadDisabledfromStorage()
    {

        if(disabledPlayers.get().contains("disabled"))
        {
            disableSnow = disabledPlayers.get().getStringList("disabled");

        }
        report.debug("Loaded plugin data!");
    }

    public static void saveDisabledToStorage()
    {
        disabledPlayers.get().set("disabled", disableSnow);
    }
}
