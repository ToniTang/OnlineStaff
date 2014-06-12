package us.rpvp.onlinestaff;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OnlineStaff extends JavaPlugin implements Listener {

	public String pluginChannel = "OnlineStaff";

	public void onEnable() {
		getServer().getMessenger().registerOutgoingPluginChannel(this, pluginChannel);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff") || event.getPlayer().isOp()) {
			sendPayload("join", event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff") || event.getPlayer().isOp()) {
			sendPayload("quit", event.getPlayer());
		}
	}

	protected void sendPayload(String event, Player player) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			switch(event.toLowerCase()) {
				case "join":
					out.writeUTF("join");
					break;
				case "quit":
					out.writeUTF("quit");
			}
			out.writeUTF(player.getUniqueId().toString());
			player.sendPluginMessage(this, pluginChannel, b.toByteArray());
			System.out.print(b.toString());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}