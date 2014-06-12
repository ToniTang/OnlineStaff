package us.rpvp.onlinestaff;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if(event.getPlayer().hasPermission("onlinestaff.staff") || event.getPlayer().isOp()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					sendPayload(event.getPlayer());
				}
			}.runTaskLaterAsynchronously(this, 5L);
		}
	}

	protected void sendPayload(Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF(player.getUniqueId().toString());
			player.sendPluginMessage(this, pluginChannel, b.toByteArray());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}