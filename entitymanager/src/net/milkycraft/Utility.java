package net.milkycraft;

import static org.bukkit.ChatColor.translateAlternateColorCodes;
import net.milkycraft.config.WorldConfiguration;
import net.milkycraft.types.Option;
import net.milkycraft.types.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.Potion;

public class Utility {

	private EntityManager manager;

	// List of blocks to permit interaction with
	private int[] ids = { 23, 54, 61, 62, 69, 77, 84, 116, 117, 130, 138, 143, 145 };

	public Utility(EntityManager manager) {
		this.manager = manager;
	}

	public void register(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, manager);
	}

	public boolean alert(WorldConfiguration conf, Player p, String name) {
		if (conf.get(Option.PVP) && !p.hasPermission(Permission.iNTPVP)) {
			al(conf, "Player " + p.getName() + " tried to attack " + name);
			al(conf, p, "&cYou don't have permission to pvp.");
			return true;
		}
		return false;
	}

	public WorldConfiguration getConfigByEntity(Entity e) {
		return this.getConfig(e.getWorld());
	}

	public WorldConfiguration getConfig(World w) {
		return this.get(w.getName());
	}

	public WorldConfiguration get(String world) {
		try {
			return manager.getWorld(world);
		} catch (NullPointerException ex) {
			manager.getLogger().warning("No config found for " + world + ", generating one now");
			return manager.load(world);
		}
	}

	@SuppressWarnings("deprecation")
	public boolean shouldBlock(Block b) {
		if (b == null)
			return false;
		for (int i : ids)
			if (b.getTypeId() == i)
				return true;
		return false;
	}

	public boolean hasPermission(Player p, String perm) {
		return p.hasPermission(perm);
	}

	public static String getName(Potion p) {
		String d = (p.getLevel() == 1 ? "" : " II") + (p.isSplash() ? " splash" : "");
		return p.getType().name().toLowerCase() + d;
	}

	public void al(WorldConfiguration conf, String adminMsg) {
		if (conf.get(Option.ADMIN_ALERTS))
			adA(adminMsg, conf.getWorld());
		if (conf.get(Option.LOGGING))
			manager.getLogger().info(adminMsg);
	}

	public void al(WorldConfiguration conf, Player player, String message) {
		if (conf.get(Option.PLAYER_ALERTS))
			player.sendMessage(translateAlternateColorCodes('&', message));
	}

	public void alert(WorldConfiguration conf, Player p, EntityType type) {
		String item = type.name().toLowerCase().replace("_", "");
		item = item.replace("thrown_", "").replace("splash_", "");
		al(conf, "Player " + p.getName() + " tried to use a " + item);
		al(conf, p, "&cYou don't have permission to throw " + item.concat("s"));
	}

	public void warn(WorldConfiguration conf, Player p, Potion pot) {
		String item = getName(pot) + " potion";
		al(conf, "Player " + p.getName() + " tried to use a " + item);
		al(conf, p, "&cYou don't have permission to use " + item.concat("s"));
	}

	private static void adA(String message, String world) {
		String msg = "&2[&4Alert&2] [&6" + world + "&2] &c" + message;
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.hasPermission(Permission.aDMINA))
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public EntityManager getHandle() {
		return this.manager;
	}
}
