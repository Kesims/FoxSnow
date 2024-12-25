package me.Kesims.FoxSnow.commands;

import me.Kesims.FoxSnow.files.Config;
import me.Kesims.FoxSnow.files.Messages;
import me.Kesims.FoxSnow.pluginData.DataStorage;
import me.Kesims.FoxSnow.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FoxSnow implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                if(sender.hasPermission("foxsnow.admin")) {
                    Config.reload();
                    DataStorage.loadDisabledfromStorage();
                    Messages.reload();
                    Chat.reloadPrefix();
                    Chat.sendMessage(sender, "&aPlugin successfully reloaded!");
                    CheckParticleIntegrity.check();
                    CheckBlockIntegrity.check();
                }
                else {
                    Chat.sendMessage(sender, Messages.getMessage("no-perms"));
                }
            }
            if(args[0].equalsIgnoreCase("toggle")) {
                if(sender instanceof Player p) {
                    if(p.hasPermission("foxsnow.toggle") || !Config.get().getBoolean("require-permission")) {
                        if(DataStorage.disableSnow.contains(p.getName())) {
                            DataStorage.disableSnow.remove(p.getName());
                            Chat.sendMessage(p, Messages.getMessage("effect-enabled"));
                        }
                        else {
                            DataStorage.disableSnow.add(p.getName());
                            Chat.sendMessage(p, Messages.getMessage("effect-disabled"));
                        }
                    }
                    else {
                        Chat.sendMessage(p, Messages.getMessage("no-perms"));
                    }
                }
                else {
                    Report.info("This command can be only used in-game!");
                }
            }
            if(args[0].equalsIgnoreCase("why")) {
                if(sender instanceof Player p) {
                    if(sender.hasPermission("foxsnow.admin"))  {
                        String str = "\n &l--- DEBUG INFO ---&7" +
                                     "\n* World name: " + p.getWorld().getName() +
                                     "\n* World time: " + p.getWorld().getTime() +
                                     "\n* Biome: " + p.getWorld().getBiome(p.getLocation()).name() +
                                     "\n* World storm: " + p.getWorld().hasStorm();
                        Chat.sendMessage(p, str);
                        EffectEvaluation.debugIsEffectApplicable(p);
                    }
                    else {
                        Chat.sendMessage(sender, Messages.getMessage("no-perms"));
                    }
                }
                else {
                    Report.info("This command can be only used in-game!");
                }
            }
        }
        return false;
    }
}
