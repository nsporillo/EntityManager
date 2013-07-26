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
import net.milkycraft.em.config.WorldConfiguration;

import org.bukkit.Material;
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
		e.setCancelled(a(e.getWorld()).get(LIGHTNING));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onThunderChange(ThunderChangeEvent e) {
		boolean b = e.toThunderState();
		e.setCancelled(b ? a(e.getWorld()).get(THUNDER) : false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeatherChange(WeatherChangeEvent e) {
		boolean b = e.toWeatherState();
		e.setCancelled(b ? a(e.getWorld()).get(RAIN) : false);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		WorldConfiguration conf = a(e.getEntity().getWorld());
		Player p = (Player) e.getEntity().getShooter();
		EntityType type = e.getEntity().getType();
		if (type == EntityType.EGG) {
			if (conf.get(1).contains(Material.EGG.toString())) {
				if (!b(p, "entitymanager.interact.egg")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName() + " tried to throw a egg");
					al(conf, p, "&cYou don't have permission to throw eggs.");
				}
			}
		} else if (type == SNOWBALL) {
			if (conf.get(1).contains(SNOW_BALL.toString())) {
				if (!b(p, "entitymanager.interact.snow_ball")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName()
							+ " tried to throw a snowball");
					al(conf, p,
							"&cYou don't have permission to throw snowballs.");
				}
			}
		} else if (type == THROWN_EXP_BOTTLE) {
			if (conf.get(1).contains(EXP_BOTTLE.toString())) {
				if (!b(p, "entitymanager.interact.exp_bottle")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName()
							+ " tried to use an exp bottle.");
					al(conf, p,
							"&cYou don't have permission to throw exp bottles.");
				}
			}
		} else if (type == EntityType.ENDER_PEARL) {
			if (conf.get(1).contains(Material.ENDER_PEARL.toString())) {
				if (!b(p, "entitymanager.interact.ender_pearl")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName()
							+ " tried to use an ender pearl");
					al(conf, p,
							"&cYou don't have permission to throw ender pearls.");
				}
			}
		} else if (type == SPLASH_POTION) {
			if (conf.get(1).contains(POTION.toString())) {
				ItemStack is = p.getItemInHand();
				Potion b = fromDamage(is.getDurability());
				if (conf.get(6).contains(b.getNameId())) {
					if (!b(p, "entitymanager.interact.splash_potion")) {
						e.setCancelled(true);
						al(conf, "Player " + p.getName()
								+ " tried to throw a splash potion");
						al(conf, p,
								"&cYou don't have permission to throw potions.");
					}
				}
			}
		} else if (type == FISHING_HOOK) {
			if (conf.get(FISHING) && !b(p, "entitymanager.interact.fishing")) {
				e.setCancelled(true);
				al(conf, "Player " + p.getName() + " tried to fish");
				al(conf, p, "&cYou don't have permission to fish.");
			}
		}
	}
}
