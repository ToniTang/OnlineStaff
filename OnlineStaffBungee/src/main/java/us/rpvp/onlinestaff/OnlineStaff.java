package us.rpvp.onlinestaff;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class OnlineStaff extends Plugin implements Listener {

	private Connection con;

	public static File configFile;
	public static Configuration config;
	public static ConfigurationProvider configProvider;

	private static OnlineStaff instance;

	public void onEnable() {
		instance = this;

		getProxy().getPluginManager().registerCommand(this, new ReloadConfig());
		getProxy().getPluginManager().registerListener(this, this);
		setupConfig();

		if(config.getBoolean("configured")) {
			String hostname = config.getString("mysql.hostname");
			String username = config.getString("mysql.username");
			String password = config.getString("mysql.password");
			String database = config.getString("mysql.database");
			Integer port = config.getInt("mysql.port");
			try {
				startConnection(hostname, username, password, database, port);
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			getLogger().severe("ERROR: You need to configure OnlineStaff first!");
			getLogger().severe("ERROR: Try that now...");
		}
	}

	public void onDisable() {
		closeConnection();
		instance = null;
	}

	public static OnlineStaff getInstance() {
		return instance;
	}

	@EventHandler
	public void onPlayerJoin(final PostLoginEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff")) {
			getProxy().getScheduler().runAsync(this, new Runnable() {
				@Override
				public void run() {
					try {
						Statement statement;
						statement = con.createStatement();
						String query = "INSERT INTO `OnlineStaff` (uuid, name, last_online, is_online, current_server) VALUES ('" + uuidToDbString(event.getPlayer().getUniqueId()) + "', '" + event.getPlayer().getName() + "', NOW(), 1, '" + event.getPlayer().getServer().getInfo().getName().toUpperCase() + "') ON DUPLICATE KEY UPDATE last_online = NOW(), is_online = '1'";
						System.out.print(query);
						statement.executeUpdate(query);
					} catch(SQLException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@EventHandler
	public void onPlayerQuit(final PlayerDisconnectEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff")) {
			getProxy().getScheduler().runAsync(this, new Runnable() {
				@Override
				public void run() {
					try {
						Statement statement;
						statement = con.createStatement();
						String query = "UPDATE `OnlineStaff` SET name = '" + event.getPlayer().getName() + "', `last_online` = NOW(), `is_online`  = '0', `current_server` = null WHERE uuid = '" + uuidToDbString(event.getPlayer().getUniqueId()) + "'";
						System.out.print(query);
						statement.executeUpdate(query);
					} catch(SQLException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void startConnection(String hostname, String username, String password, String database, Integer port) throws SQLException {
		Statement statement;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);

		statement = con.createStatement();
		String query = "CREATE TABLE IF NOT EXISTS `OnlineStaff` ("
			+ "  `uuid` varchar(32) NOT NULL,"
			+ "  `name` varchar(16) NOT NULL,"
			+ "  `last_online` datetime NOT NULL,"
			+ "  `is_online` tinyint(1) NOT NULL,"
			+ "  `current_server` varchar(24) NOT NULL,"
			+ "  UNIQUE KEY `uuid` (`uuid`)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		statement.executeUpdate(query);
		statement.close();
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

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void setupConfig() {
		File configFolder = new File(getDataFolder(), "");
		if(!configFolder.exists()) {
			configFolder.mkdir();
		}
		configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			try {
				String contents =
					"## Change this to true after you have edited your database details below!\n" +
						"configured: false\n\n" +
						"## MySQL Connection Details\n" +
						"mysql:\n" +
						"  hostname: localhost\n" +
						"  username: root\n" +
						"  password: \n" +
						"  database: mc_onlinestaff\n" +
						"  port: 3306";
				FileWriter fileWriter = new FileWriter(configFile);
				BufferedWriter output = new BufferedWriter(fileWriter);
				output.write(contents);
				output.close();
				fileWriter.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		try {
			config = configProvider.load(configFile);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadConfig() {
		try {
			configProvider.load(configFile);
		} catch(IOException e) {
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