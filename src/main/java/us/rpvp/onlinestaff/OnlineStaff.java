package us.rpvp.onlinestaff;

import java.lang.String;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    public Connection con;

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
        closeConnection();
    }

    public void startConnection(String hostname, String username, String password, String database) {
        Statement str;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":3306/" + database, username, password);

            str = con.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS `OnlineStaff` ("
                    + "  `player` varchar(16) NOT NULL,"
                    + "  `last_online` varchar(16) NOT NULL,"
                    + "  `is_online` tinyint(1) NOT NULL,"
                    + "  UNIQUE KEY `player` (`player`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
            str.executeUpdate(query);
            str.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.con.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
        if(sender.hasPermission("onlinestaff.reload")) {
            if(cmd.getLabel().equalsIgnoreCase("osreload")) {
                sender.sendMessage(ChatColor.AQUA + "[OnlineStaff]" + ChatColor.GOLD + " Configuration reloaded.");

                closeConnection();

                this.reloadConfig();

                String hostname = getConfig().getString("mysql.hostname");
                String username = getConfig().getString("mysql.username");
                String password = getConfig().getString("mysql.password");
                String database = getConfig().getString("mysql.database");

                startConnection(hostname, username, password, database);
            }
        }
        else {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Statement str = this.con.createStatement();
        String query = String.format("INSERT INTO `OnlineStaff` (player, last_online, is_online) VALUES ('%s', 'Currently Online', 1) ON DUPLICATE KEY UPDATE last_online = 'Currently Online', is_online = '1'", event.getPlayer().getName());
        str.executeUpdate(query);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
        Statement str = this.con.createStatement();
        String query = String.format("UPDATE `OnlineStaff` SET `last_online` = 'Just logged', `is_online`  = '0' WHERE player = '%s'", event.getPlayer().getName());
        str.executeUpdate(query);
    }
}
