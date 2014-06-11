package us.rpvp.onlinestaff;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;

public class ReloadConfig extends Command {

	public ReloadConfig() {
		super("osreload", "onlinestaff.reload");
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		if(sender.hasPermission("onlinestaff.reload")) {
			OnlineStaff.getInstance().closeConnection();
			OnlineStaff.getInstance().reloadConfig();

			String hostname = OnlineStaff.config.getString("mysql.hostname");
			String username = OnlineStaff.config.getString("mysql.username");
			String password = OnlineStaff.config.getString("mysql.password");
			String database = OnlineStaff.config.getString("mysql.database");
			Integer port = OnlineStaff.config.getInt("mysql.port");
			try {
				OnlineStaff.getInstance().startConnection(hostname, username, password, database, port);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			sender.sendMessage(ChatColor.AQUA + "[OnlineStaff]" + ChatColor.GOLD + " Configuration reloaded.");
		}
	}
}