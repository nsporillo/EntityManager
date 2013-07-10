package net.milkycraft.em;

import static net.milkycraft.em.config.Option.FISHING;
import static net.milkycraft.em.config.Option.LIGHTNING;
import static net.milkycraft.em.config.Option.RAIN;
import static net.milkycraft.em.config.Option.THUNDER;
import static org.bukkit.Material.EXP_BOTTLE;
import static org.bukkit.Material.POTION;
import static org.bukkit.Material.SNOW_BALL;
import static org.bukkit.entity.EntityType.FISHING_HOOK;
import static org.bukkit.entity.EntityType.SNOWBALL;
import static org.bukkit.entity.EntityType.SPLASH_POTION;
import static org.bukkit.entity.EntityType.THROWN_EXP_BOTTLE;
import net.milkycraft.em.config.ConfigHelper;
import net.milkycraft.em.config.WorldConfiguration;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public class AuxiliaryListener extends Utility implements Listener {

	public AuxiliaryListener(EntityManager manager) {
		super(manager);
		super.register(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLightning(LightningStrikeEvent e) {
		e.setCancelled(get(e.getWorld().getName()).get(LIGHTNING));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onThunderChange(ThunderChangeEvent e) {
		boolean cancel = e.toThunderState() ? get(e.getWorld().getName()).get(
				THUNDER) : false;
		e.setCancelled(cancel);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeatherChange(WeatherChangeEvent e) {
		boolean cancel = e.toWeatherState() ? super.get(e.getWorld().getName())
				.get(RAIN) : false;
		e.setCancelled(cancel);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		WorldConfiguration conf = get(e.getEntity().getWorld().getName());
		Player player = (Player) e.getEntity().getShooter();
		Entity en = e.getEntity();
		if (en.getType() == EntityType.EGG) {
			if (conf.getBlockedUsage().contains(Material.EGG)) {
				if (!player.hasPermission("entitymanager.interact.egg")) {
					e.setCancelled(true);
					alert(conf, "Player " + player.getName()
							+ " tried to throw a egg");
					alert(conf, player,
							"&cYou don't have permission to throw eggs.");
				}
			}
		} else if (en.getType() == SNOWBALL) {
			if (conf.getBlockedUsage().contains(SNOW_BALL)) {
				if (!player.hasPermission("entitymanager.interact.snow_ball")) {
					e.setCancelled(true);
					alert(conf, "Player " + player.getName()
							+ " tried to throw a snowball");
					alert(conf, player,
							"&cYou don't have permission to throw snowballs.");
				}
			}
		} else if (en.getType() == THROWN_EXP_BOTTLE) {
			if (conf.getBlockedUsage().contains(EXP_BOTTLE)) {
				if (!player.hasPermission("entitymanager.interact.exp_bottle")) {
					e.setCancelled(true);
					alert(conf, "Player " + player.getName()
							+ " tried to use an exp bottle.");
					alert(conf, player,
							"&cYou don't have permission to throw exp bottles.");
				}
			}
		} else if (en.getType() == EntityType.ENDER_PEARL) {
			if (conf.getBlockedUsage().contains(Material.ENDER_PEARL)) {
				if (!player.hasPermission("entitymanager.interact.ender_pearl")) {
					e.setCancelled(true);
					alert(conf, "Player " + player.getName()
							+ " tried to use an ender pearl");
					alert(conf, player,
							"&cYou don't have permission to throw ender pearls.");

				}
			}
		} else if (en.getType() == SPLASH_POTION) {
			if (conf.getBlockedUsage().contains(POTION)) {
				ItemStack is = player.getItemInHand();
				Potion b = ConfigHelper.fromDamage(is.getDurability());
				if (conf.getPotions().contains(b)) {
					if (!player
							.hasPermission("entitymanager.interact.splash_potion")) {
						e.setCancelled(true);
						alert(conf, "Player " + player.getName()
								+ " tried to throw a splash potion");
						alert(conf, player,
								"&cYou don't have permission to throw potions.");
					}
				}
			}
		} else if (en.getType() == FISHING_HOOK) {
			if (conf.get(FISHING)
					&& !player.hasPermission("entitymanager.interact.fishing")) {
				e.setCancelled(true);
				alert(conf, "Player " + player.getName() + " tried to fish");
				alert(conf, player, "&cYou don't have permission to fish.");
			}
		}
	}

}
