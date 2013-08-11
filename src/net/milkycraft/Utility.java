package net.milkycraft;

import static org.bukkit.ChatColor.translateAlternateColorCodes;
import net.milkycraft.config.WorldConfiguration;
import net.milkycraft.objects.Option;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Listener;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public abstract class Utility {

	private EntityManager manager;
	private int[] ids = { 23, 54, 61, 62, 69, 77, 84, 116, 117, 130, 138, 143, 145 };
	public String[] p = { "entitymanager.interact.trade", "entitymanager.interact.shoot",
			"entitymanager.interact.enchant", "entitymanager.interact.anvil",
			"entitymanager.death.keepexp", "entitymanager.death.keepitems",
			"entitymanager.create.portal" };

	public Utility(EntityManager manager) {
		this.manager = manager;
	}

	public void register(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, manager);
	}

	public boolean a(WorldConfiguration conf, Player p, String name) {
		if (conf.get(Option.PVP) && !p.hasPermission("entitymanager.interact.pvp")) {
			al(conf, "Player " + p.getName() + " tried to attack " + name);
			al(conf, p, "&cYou don't have permission to pvp.");
			return true;
		}
		return false;
	}

	public WorldConfiguration a(World w) {
		return this.get(w.getName());
	}

	public WorldConfiguration a(Entity e) {
		return this.get(e.getWorld().getName());
	}

	public void update(Sheep e) {
		double health = e.getHealth();
		Location loc = e.getLocation();
		DyeColor dc = e.getColor();
		String name = e.getCustomName();
		e.remove();
		Sheep sheep = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
		sheep.setHealth(health);
		sheep.setColor(dc);
		sheep.setCustomName(name);
	}

	public boolean b(Block b) {
		if (b == null) {
			return false;
		}
		for (int i : ids) {
			if (b.getTypeId() == i) {
				return true;
			}
		}
		return false;
	}

	public boolean b(Player p, String perm) {
		return p.hasPermission(perm);
	}

	public static String c(Potion p) {
		return p.getType().name().toLowerCase() + (p.getLevel() == 1 ? "" : " II")
				+ (p.isSplash() ? " splash" : "");
	}

	public void al(WorldConfiguration conf, String adminMsg) {
		if (conf.get(Option.ADMIN_ALERTS)) {
			adA(adminMsg, conf.getWorld());
		}
		if (conf.get(Option.LOGGING)) {
			manager.getLogger().info(adminMsg);
		}
	}

	public static void al(WorldConfiguration conf, Player player, String message) {
		if (conf.get(Option.PLAYER_ALERTS)) {
			player.sendMessage(translateAlternateColorCodes('&', message));
		}
	}

	private static void adA(String message, String world) {
		String msg = "&2[&4Alert&2] [&6" + world + "&2] &c" + message;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("entitymanager.admin.alert")) {
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
			manager.getLogger().warning(
					"No config found for " + world + ", generating one now");
			return manager.load(world);
		}
	}

	public static Potion fromDamage(int damage) {
		PotionType type = PotionType.getByDamageValue(damage & 0xF);
		Potion potion;
		if (type == null || (type == PotionType.WATER && damage != 0)) {
			potion = new Potion(damage & 0x3F);
		} else {
			int level = (damage & 0x20) >> 5;
			level++;
			potion = new Potion(type, level);
		}
		if ((damage & 0x4000) > 0) {
			potion = potion.splash();
		}
		if ((damage & 0x40) > 0) {
			potion = potion.extend();
		}
		return potion;
	}
}
