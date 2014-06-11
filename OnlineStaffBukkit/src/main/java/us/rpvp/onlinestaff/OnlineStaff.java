package us.rpvp.onlinestaff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class OnlineStaff extends JavaPlugin implements CommandExecutor, Listener {

	private Connection con;

	public void onEnable() {
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
			} catch(SQLException e) {
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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("osreload")) {
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
				} catch(SQLException e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.AQUA + "[OnlineStaff]" + ChatColor.GOLD + " Configuration reloaded.");
			}
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Statement statement;
						statement = con.createStatement();
						String query = "INSERT INTO `OnlineStaff` (uuid, name, last_online, is_online) VALUES ('" + uuidToDbString(event.getPlayer().getUniqueId()) + "', '" + event.getPlayer().getName() + "', NOW(), 1) ON DUPLICATE KEY UPDATE last_online = NOW(), is_online = '1'";
						statement.executeUpdate(query);
					} catch(SQLException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(this);
		}
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Statement statement;
						statement = con.createStatement();
						String query = "UPDATE `OnlineStaff` SET name = '" + event.getPlayer().getName() + "', `last_online` = NOW(), `is_online`  = '0' WHERE uuid = '" + uuidToDbString(event.getPlayer().getUniqueId()) + "'";
						statement.executeUpdate(query);
					} catch(SQLException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(this);
		}
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
			+ "  `uuid` varchar(32) NOT NULL,"
			+ "  `name` varchar(16) NOT NULL,"
			+ "  `last_online` datetime NOT NULL,"
			+ "  `is_online` tinyint(1) NOT NULL,"
			+ "  UNIQUE KEY `uuid` (`uuid`)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		str.executeUpdate(query);
		str.close();
	}

	public void closeConnection() {
		try {
			if(con != null) {
				con.close();
				con = null;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Taken from Prism
	 * Credits to them: https://github.com/prism/Prism/blob/master/src/main/java/me/botsko/prism/players/PlayerIdentification.java
	 */
	protected String uuidToDbString(UUID id) {
		return id.toString().replace("-", "");
	}
}