package me.Kesims.FoxSnow.hooks;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.pluginData.HookState;
import me.Kesims.FoxSnow.utils.Report;


public class WorldGuardHook
{
    public static void initialize() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            StateFlag flag = new StateFlag("foxsnow-force-enable", false);
            registry.register(flag);
            var FOXSNOW_FORCE_ENABLE = flag; // only set our field if there was no error

            StateFlag flag2 = new StateFlag("foxsnow-force-disable", false);
            registry.register(flag2);
            var FOXSNOW_FORCE_DISABLE = flag2; // only set our field if there was no error

            if(Config.get().getBoolean("separate-snowman-permission")) {
                StateFlag flag3 = new StateFlag("snowman-force-enable", false);
                registry.register(flag3);
                var SNOWMAN_FORCE_ENABLE = flag3; // only set our field if there was no error

                StateFlag flag4 = new StateFlag("snowman-force-disable", false);
                registry.register(flag4);
                var SNOWMAN_FORCE_DISABLE = flag4; // only set our field if there was no error
            }

            HookState.worldGuard = true;
            Report.info("WorldGuard hook enabled!");
        }
        catch (Exception e) {
            Report.error("Failed to hook into WorldGuard! Is working? Please, check your server configuration or disable the hook, if this is a mistake and you don't want to hook into WorldGuard!");
        }

    }
}
