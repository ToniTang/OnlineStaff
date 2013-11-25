package us.rpvp.onlinestaff;

import java.lang.String;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnlineStaff extends JavaPlugin implements Listener {

    public void onEnable() {
        getLogger().info("===========================================");
        getLogger().info(String.format("[v%s] OnlineStaff has been enabled.", getDescription().getVersion()));
        getLogger().info("===========================================");
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        /********************************************
        Method / Plan here is that when the user joins
        and triggers the PlayerJoinEvent, a SQL statement
        will be formatted, escaped, and then queried into
        the database updating the player's row.
            Case 1:
            - User is joining for the first time
                - Create a manager or check method that
                  will run and see if the player's row
                  exists based off of the player's name
                - If it doesn't exist, append the player's
                  name into a already predefined statement
                  and query it into the database.
                - Field (isOnline) = 1 ((true))
            Case 2:
            - User row exists and is returning to server
                - Use predefined query statement and append
                  player name to it and update the field
                  isOnline and set to (1) true
        ********************************************/
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        /********************************************
         Method / Plan here is that when the user quits
         and triggers the PlayerQuitEvent, a SQL statement
         will be formatted, escaped, and then queried into
         the database updating the player's row.
            Case 1:
            - Use predefined query statement and append
              player name to it and update the field
              isOnline and set it to (0) false
         ********************************************/
    }
}
