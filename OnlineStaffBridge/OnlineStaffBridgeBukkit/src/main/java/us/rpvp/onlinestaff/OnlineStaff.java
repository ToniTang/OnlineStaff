package us.rpvp.onlinestaff;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OnlineStaff extends JavaPlugin implements Listener {

	public void onEnable() {
		getServer().getMessenger().registerOutgoingPluginChannel(this, "OnlineStaff");
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff")) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				out.writeUTF(event.getPlayer().getUniqueId().toString());
				event.getPlayer().sendPluginMessage(this, "OnlineStaff", b.toByteArray());
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}