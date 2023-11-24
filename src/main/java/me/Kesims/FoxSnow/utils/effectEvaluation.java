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
import me.Kesims.FoxSnow.files.messages;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.pluginData.hookState;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class effectEvaluation {

    // Evaluates if effect should be shown to the player
    public static boolean isEffectApplicable(Player p, effectType effectType) {
        Location center = p.getLocation();

        // WorldGuard hook has maximum priority, can bypass all other requirements
        if(hookState.worldGuard) {
            com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(aLoc);

            if(config.get().getBoolean("separate-snowman-permission")) { // If the permissions for effects are seperated
                if (effectType == me.Kesims.FoxSnow.utils.effectType.SNOW) {
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable")))
                        return false;
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable")))
                        return true;
                }
                if (effectType == me.Kesims.FoxSnow.utils.effectType.SNOWMAN) {
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "snowman-force-disable")))
                        return false;
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "snowman-force-enable")))
                        return true;
                }
            }
            else { // If the permissions for effects are not seperated, use foxsnow-force for everyting
                if(set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable"))) return false;
                if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable"))) return true;
            }
        }

        if(dataStorage.disableSnow.contains(p.getName())) return false;
        if(!config.get().getList("enabled-worlds").contains(p.getWorld().getName())) return false;

        if(config.get().getBoolean("require-permission")) {
            if(config.get().getBoolean("separate-snowman-permission")) {
                if (effectType == me.Kesims.FoxSnow.utils.effectType.SNOW && !p.hasPermission("foxsnow.show")) return false;
                else if (effectType == me.Kesims.FoxSnow.utils.effectType.SNOWMAN && !p.hasPermission("foxsnow.snowman")) return false;
            }
            else if(!p.hasPermission("foxsnow.show")) return false;
        }

        if(config.get().getBoolean("rain-disable-snow")  && p.getWorld().hasStorm()) return false;
        if(p.getWorld().getTime() < config.get().getInt("snowtime.start") || p.getWorld().getTime() > config.get().getInt("snowtime.end")) return false;
        if(!evaluateBiomeFilter(p.getWorld().getBiome(p.getLocation()).name())) return false;

        return true; // Passed all requirements, effect should be shown
    }

    // Used for /foxsnow why - this should help admins debug the plugin
    public static void debugIsEffectApplicable(Player p) {
        Location center = p.getLocation();

        // WorldGuard hook has maximum priority, can bypass all other requirements
        if(hookState.worldGuard) {
            com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(aLoc);

            if(set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable"))){
                chat.sendMessage(p, messages.getMessage("debug-messages.hook-force-disable"));
                return;
            }
            if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable"))) {
                chat.sendMessage(p, messages.getMessage("debug-messages.snow-is-working"));
                return;
            }
        }


        if(dataStorage.disableSnow.contains(p.getName())){
            chat.sendMessage(p, messages.getMessage("debug-messages.effect-toggled-off"));
            return;
        }
        if(!config.get().getList("enabled-worlds").contains(p.getWorld().getName())){
            chat.sendMessage(p, messages.getMessage("debug-messages.world-not-configured"));
            return;
        }
        if(config.get().getBoolean("require-permission") && !p.hasPermission("foxsnow.show")) {
            chat.sendMessage(p, messages.getMessage("debug-messages.missing-permission"));
            return;
        }
        if(config.get().getBoolean("rain-disable-snow")  && p.getWorld().hasStorm()){
            chat.sendMessage(p, messages.getMessage("debug-messages.rainy-weather"));
            return;
        }
        if(p.getWorld().getTime() < config.get().getInt("snowtime.start") || p.getWorld().getTime() > config.get().getInt("snowtime.end")) {
            chat.sendMessage(p, messages.getMessage("debug-messages.wrong-time"));
            return;
        }
        Location pLoc = p.getLocation();
        if(!config.get().getBoolean("snow-under-blocks") && pLoc.getY() < pLoc.getWorld().getHighestBlockAt(pLoc).getY()){
            chat.sendMessage(p, messages.getMessage("debug-messages.blocks-above"));
            return;
        };

        if(!evaluateBiomeFilter(p.getWorld().getBiome(p.getLocation()).name())) {
            chat.sendMessage(p, messages.getMessage("debug-messages.invalid-biome"));
            return;
        }


        chat.sendMessage(p, messages.getMessage("debug-messages.snow-is-working"));
    }


    public static boolean evaluateBiomeFilter(String biomeName) {
        if(!config.get().getBoolean("biome-filter.enabled")) return true;
        List<String> biomeList = config.get().getStringList("biome-filter.biomes");
        if(config.get().getString("biome-filter.type").equalsIgnoreCase("BLACKLIST")) {
            return !biomeList.contains(biomeName);
        }
        if(config.get().getString("biome-filter.type").equalsIgnoreCase("WHITELIST")) {
            return biomeList.contains(biomeName);
        }
        return true;
    }
}
