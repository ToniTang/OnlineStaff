package us.rpvp.onlinestaff;

import java.lang.String;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class OnlineStaff extends JavaPlugin {

    public void onEnable() {
        getLogger().info("===========================================");
        getLogger().info(String.format("[v%s] OnlineStaff has been enabled.", getDescription().getVersion()));
        getLogger().info("===========================================");
    }

    public void onDisable() {
        getLogger().info("===========================================");
        getLogger().info(String.format("[v%s] OnlineStaff has been disabled.", getDescription().getVersion()));
        getLogger().info("===========================================");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
        if(sender.hasPermission("onlinestaff.reload")) {
            if(cmd.getLabel().equalsIgnoreCase("osreload")) {
                sender.sendMessage(ChatColor.AQUA + "[OnlineStaff]" + ChatColor.GOLD + " Configuration reloaded.");
                this.reloadConfig();
            }
        }
        else {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
        }
        return false;
    }
}
