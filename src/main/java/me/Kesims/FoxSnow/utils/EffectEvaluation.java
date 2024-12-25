package me.Kesims.FoxSnow.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.files.Messages;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.pluginData.HookState;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class EffectEvaluation {

    // Evaluates if effect should be shown to the player
    public static boolean isEffectApplicable(Player p, EffectType effectType) {
        Location center = p.getLocation();

        // WorldGuard hook has maximum priority, can bypass all other requirements
        if(HookState.worldGuard) {
            com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(aLoc);

            if(Config.get().getBoolean("separate-snowman-permission")) { // If the permissions for effects are seperated
                if (effectType == EffectType.SNOW) {
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable")))
                        return false;
                    if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable")))
                        return true;
                }
                if (effectType == EffectType.SNOWMAN) {
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

        if(DataStorage.disableSnow.contains(p.getName())) return false;
        if(!Config.get().getList("enabled-worlds").contains(p.getWorld().getName())) return false;

        if(Config.get().getBoolean("require-permission")) {
            if(Config.get().getBoolean("separate-snowman-permission")) {
                if (effectType == EffectType.SNOW && !p.hasPermission("foxsnow.show")) return false;
                else if (effectType == EffectType.SNOWMAN && !p.hasPermission("foxsnow.snowman")) return false;
            }
            else if(!p.hasPermission("foxsnow.show")) return false;
        }

        if(Config.get().getBoolean("rain-disable-snow")  && p.getWorld().hasStorm()) return false;
        if(p.getWorld().getTime() < Config.get().getInt("snowtime.start") || p.getWorld().getTime() > Config.get().getInt("snowtime.end")) return false;
        if(!evaluateBiomeFilter(p.getWorld().getBiome(p.getLocation()).name())) return false;

        return true; // Passed all requirements, effect should be shown
    }

    // Used for /foxsnow why - this should help admins debug the plugin
    public static void debugIsEffectApplicable(Player p) {
        Location center = p.getLocation();

        // WorldGuard hook has maximum priority, can bypass all other requirements
        if(HookState.worldGuard) {
            com.sk89q.worldedit.util.Location aLoc = BukkitAdapter.adapt(center);
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(aLoc);

            if(set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-disable"))){
                Chat.sendMessage(p, Messages.getMessage("debug-messages.hook-force-disable"));
                return;
            }
            if (set.testState(null, (StateFlag) Flags.fuzzyMatchFlag(registry, "foxsnow-force-enable"))) {
                Chat.sendMessage(p, Messages.getMessage("debug-messages.snow-is-working"));
                return;
            }
        }


        if(DataStorage.disableSnow.contains(p.getName())){
            Chat.sendMessage(p, Messages.getMessage("debug-messages.effect-toggled-off"));
            return;
        }
        if(!Config.get().getList("enabled-worlds").contains(p.getWorld().getName())){
            Chat.sendMessage(p, Messages.getMessage("debug-messages.world-not-configured"));
            return;
        }
        if(Config.get().getBoolean("require-permission") && !p.hasPermission("foxsnow.show")) {
            Chat.sendMessage(p, Messages.getMessage("debug-messages.missing-permission"));
            return;
        }
        if(Config.get().getBoolean("rain-disable-snow")  && p.getWorld().hasStorm()){
            Chat.sendMessage(p, Messages.getMessage("debug-messages.rainy-weather"));
            return;
        }
        if(p.getWorld().getTime() < Config.get().getInt("snowtime.start") || p.getWorld().getTime() > Config.get().getInt("snowtime.end")) {
            Chat.sendMessage(p, Messages.getMessage("debug-messages.wrong-time"));
            return;
        }
        Location pLoc = p.getLocation();
        if(!Config.get().getBoolean("snow-under-blocks") && pLoc.getY() < pLoc.getWorld().getHighestBlockAt(pLoc).getY()){
            Chat.sendMessage(p, Messages.getMessage("debug-messages.blocks-above"));
            return;
        };

        if(!evaluateBiomeFilter(p.getWorld().getBiome(p.getLocation()).name())) {
            Chat.sendMessage(p, Messages.getMessage("debug-messages.invalid-biome"));
            return;
        }


        Chat.sendMessage(p, Messages.getMessage("debug-messages.snow-is-working"));
    }


    public static boolean evaluateBiomeFilter(String biomeName) {
        if(!Config.get().getBoolean("biome-filter.enabled")) return true;
        List<String> biomeList = Config.get().getStringList("biome-filter.biomes");
        if(Config.get().getString("biome-filter.type").equalsIgnoreCase("BLACKLIST")) {
            return !biomeList.contains(biomeName);
        }
        if(Config.get().getString("biome-filter.type").equalsIgnoreCase("WHITELIST")) {
            return biomeList.contains(biomeName);
        }
        return true;
    }
}
