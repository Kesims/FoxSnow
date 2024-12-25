package me.Kesims.FoxSnow.tabCompleters;

import me.Kesims.FoxSnow.files.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FoxSnowTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> result = new ArrayList<>();

        if(args.length == 1) {
            if(sender.hasPermission("foxsnow.admin")) {
                result.add("reload");
                result.add("why");
            }
            if(sender.hasPermission("foxsnow.toggle")  || !Config.get().getBoolean("require-permission")) {
                result.add("toggle");
            }
        }
        return result;
    }
}
