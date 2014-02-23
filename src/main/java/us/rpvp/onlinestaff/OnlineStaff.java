package us.rpvp.onlinestaff;

import java.sql.*;
import java.lang.String;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class OnlineStaff extends JavaPlugin implements Listener {

    Connection con;

    public void onEnable() {
        getLogger().info("[" + getDescription().getVersion() + "] OnlineStaff has been enabled");
        getServer().getPluginManager().registerEvents(this, this);

        saveDefaultConfig();

		if(getConfig().getBoolean("configured")) {
			String hostname = getConfig().getString("mysql.hostname");
			String username = getConfig().getString("mysql.username");
			String password = getConfig().getString("mysql.password");
			String database = getConfig().getString("mysql.database");
			Integer port = getConfig().getInt("mysql.port");
			try {
				startConnection(hostname, username, password, database, port);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			getServer().getPluginManager().disablePlugin(this);
			getLogger().severe("ERROR: You need to configure OnlineStaff first!");
			getLogger().severe("ERROR: Try that now...");
		}
    }

    public void onDisable() {
        closeConnection();
    }

    public void startConnection(String hostname, String username, String password, String database, Integer port) throws SQLException {
        Statement str;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);

		str = con.createStatement();
		String query = "CREATE TABLE IF NOT EXISTS `OnlineStaff` ("
				+ "  `player` varchar(16) NOT NULL,"
				+ "  `last_online` datetime NOT NULL,"
				+ "  `is_online` tinyint(1) NOT NULL,"
				+ "  UNIQUE KEY `player` (`player`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		str.executeUpdate(query);
		str.close();
    }

    public void closeConnection() {
        try {
			if(con != null) {
           		con.close();
			}
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
        if(cmd.getLabel().equalsIgnoreCase("osreload")) {
            if(sender.hasPermission("onlinestaff.reload")) {
				closeConnection();

				reloadConfig();

				String hostname = getConfig().getString("mysql.hostname");
				String username = getConfig().getString("mysql.username");
				String password = getConfig().getString("mysql.password");
				String database = getConfig().getString("mysql.database");
				Integer port = getConfig().getInt("mysql.port");
				try {
					startConnection(hostname, username, password, database, port);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.AQUA + "[OnlineStaff]" + ChatColor.GOLD + " Configuration reloaded.");
			}
			return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) throws SQLException {
        new BukkitRunnable() {
			public void run() {
				if(event.getPlayer().hasPermission("onlinestaff.staff")) {
					Statement str = null;
					try {
						str = con.createStatement();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					String query = "INSERT INTO `OnlineStaff` (player, last_online, is_online) VALUES ('" + event.getPlayer().getName() + "', NOW(), 1) ON DUPLICATE KEY UPDATE last_online = NOW(), is_online = '1'";
					assert str != null;
					try {
						str.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
        }.runTaskAsynchronously(this);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) throws SQLException {
		new BukkitRunnable() {
			public void run() {
				if(event.getPlayer().hasPermission("onlinestaff.staff")) {
					Statement str = null;
					try {
						str = con.createStatement();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					String query = "UPDATE `OnlineStaff` SET `last_online` = NOW(), `is_online`  = '0' WHERE player = '" + event.getPlayer().getName() + "'";
					assert str != null;
					try {
						str.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(this);
    }
}
