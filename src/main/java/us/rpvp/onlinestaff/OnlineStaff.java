package us.rpvp.onlinestaff;

import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
        String hostname = getConfig().getString("mysql.hostname");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
        String database = getConfig().getString("mysql.database");
        startConnection(hostname, username, password, database);
    }

    public void onDisable() {
        getLogger().info("===========================================");
        getLogger().info(String.format("[v%s] OnlineStaff has been disabled.", getDescription().getVersion()));
        getLogger().info("===========================================");
    }

    public void startConnection(String hostname, String username, String password, String database) {
        Connection con = null;
        Statement str;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":3306/" + database, username, password);
            System.out.println("[OnlineStaff] Successfully connected to database");
            System.out.println(con);
            str = con.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS `OnlineStaff` ("
                    + "  `player` varchar(16) NOT NULL,"
                    + "  `last_online` int(16) NOT NULL,"
                    + "  `is_online` tinyint(1) NOT NULL"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
            str.executeQuery(query);
            str.close();
        }
        catch (Exception e) {
            System.out.println("[OnlineStaff] Failed to connect to database");
        }
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
