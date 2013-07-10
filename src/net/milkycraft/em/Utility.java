package net.milkycraft.em;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.*;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;

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

	public void c(PlayerInteractEvent e) {
		e.setCancelled(true);
		e.setUseItemInHand(Result.DENY);
	}
	
	public boolean a(WorldConfiguration conf, Player p, String name) {
		if (conf.get(Option.PVP)
				&& !p.hasPermission("entitymanager.interact.pvp")) {
			alert(conf, "Player " + p.getName() + " tried to attack " + name);
			alert(conf, p, "&cYou don't have permission to pvp.");
			return true;
		}
		return false;
	}

	public void alert(WorldConfiguration conf, String adminMsg) {
		if (conf.get(Option.ADMIN_ALERTS)) {
			adminAlert(adminMsg, conf.getWorld());
		}
		if (conf.get(Option.LOGGING)) {
			manager.getLogger().info(adminMsg);
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
	
	public EntityManager getHandle() {
		return this.manager;
	}
	
	public WorldConfiguration get(String world) {
		try {
			return manager.getWorld(world);
		} catch (NullPointerException ex) {
			manager.getLogger().warning("No config found for " + world
					+ ", generating one now");
			return manager.getWorld(world);
		}
	}
}
