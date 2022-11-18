package me.Kesims.FoxSnow.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.pluginData.hookState;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class effectEvaluation {

    // Evaluates if effect should be shown to the player
    public static boolean isEffectApplicable(Player p) {
        Location center = p.getLocation();

        // WorldGuard hook has maximum priority, can bypass all other requirements
        if(hookState.worldGuard) {
            com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(aLoc);

            if(set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable"))) return false;
            if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable"))) return true;
        }


        if(dataStorage.disableSnow.contains(p.getName())) return false;
        if(!config.get().getList("enabled-worlds").contains(p.getWorld().getName())) return false;
        if(config.get().getBoolean("require-permission") && !p.hasPermission("foxsnow.show")) return false;
        if(config.get().getBoolean("rain-disable-snow")  && p.getWorld().hasStorm()) return false;
        if(p.getWorld().getTime() < config.get().getInt("snowtime.start") || p.getWorld().getTime() > config.get().getInt("snowtime.end")) return false;


        return true; // Passed all requirements, effect should be shown
    }
}
