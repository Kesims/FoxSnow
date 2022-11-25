package me.Kesims.FoxSnow.commands;

import me.Kesims.FoxSnow.files.config;
import me.Kesims.FoxSnow.files.messages;
import me.Kesims.FoxSnow.pluginData.dataStorage;
import me.Kesims.FoxSnow.utils.chat;
import me.Kesims.FoxSnow.utils.checkParticleIntegrity;
import me.Kesims.FoxSnow.utils.effectEvaluation;
import me.Kesims.FoxSnow.utils.report;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class foxSnow implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
    {
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("reload"))
            {
                if(sender.hasPermission("foxsnow.admin"))
                {
                    config.reload();
                    dataStorage.loadDisabledfromStorage();
                    messages.reload();
                    chat.reloadPrefix();
                    chat.sendMessage(sender, "&aPlugin successfully reloaded!");
                    checkParticleIntegrity.check();
                }
                else
                {
                    chat.sendMessage(sender, messages.getMessage("no-perms"));
                }
            }
            if(args[0].equalsIgnoreCase("toggle"))
            {
                if(sender instanceof Player p)
                {
                    if(p.hasPermission("foxsnow.toggle") || !config.get().getBoolean("require-permission"))
                    {
                        if(dataStorage.disableSnow.contains(p.getName()))
                        {
                            dataStorage.disableSnow.remove(p.getName());
                            chat.sendMessage(p, messages.getMessage("effect-enabled"));
                        }
                        else
                        {
                            dataStorage.disableSnow.add(p.getName());
                            chat.sendMessage(p, messages.getMessage("effect-disabled"));
                        }
                    }
                    else
                    {
                        chat.sendMessage(p, messages.getMessage("no-perms"));
                    }
                }
                else
                {
                    report.info("This command can be only used in-game!");
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
                        chat.sendMessage(p, str);
                        effectEvaluation.debugIsEffectApplicable(p);
                    }
                    else {
                        chat.sendMessage(sender, messages.getMessage("no-perms"));
                    }
                }
                else {
                    report.info("This command can be only used in-game!");
                }
            }
        }
        return false;
    }
}
