package net.milkycraft.em;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.milkycraft.em.config.Option;
import net.milkycraft.em.config.WorldConfiguration;

public abstract class Utility {

	private EntityManager manager;

	public Utility(EntityManager manager) {
		this.manager = manager;
	}

	public void register(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, manager);
	}

	public WorldConfiguration get(String world) {
		try {
			return manager.getWorld(world);
		} catch (NullPointerException ex) {
			manager.warn("No config found for " + world
					+ ", generating one now");
			manager.load();
			return manager.getWorld(world);
		}
	}

	public EntityManager getHandle() {
		return this.manager;
	}

	public void alert(WorldConfiguration conf, String adminMsg) {
		if (conf.get(Option.ADMIN_ALERTS)) {
			adminAlert(adminMsg, conf.getWorld());
		}
		if (conf.get(Option.LOGGING)) {
			manager.info(adminMsg);
		}
	}

	public static void alert(WorldConfiguration conf, Player player, String message) {
		if (conf.get(Option.PLAYER_ALERTS)) {
			player.sendMessage(translateAlternateColorCodes('&', message));
		}
	}

	private static void adminAlert(String message, String world) {
		String msg =  "&2[&4Alert&2] [&6" + world + "&2] &c"+ message;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("entitymanager.admin.alert")) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
			}
		}
	}
}
