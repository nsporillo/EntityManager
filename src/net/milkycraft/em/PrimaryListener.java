package net.milkycraft.em;

import java.util.HashSet;
import java.util.Set;

import net.milkycraft.em.config.ConfigHelper;
import static net.milkycraft.em.config.Option.*;
import net.milkycraft.em.config.WorldConfiguration;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public class PrimaryListener extends Utility implements Listener {

	private Set<Entity> mobs = new HashSet<Entity>();
	private String[] p = { "entitymanager.interact.trade",
			"entitymanager.interact.shoot", "entitymanager.interact.enchant",
			"entitymanager.interact.anvil", "entitymanager.death.keepexp" };

	public PrimaryListener(EntityManager manager) {
		super(manager);
		super.register(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteraction(PlayerInteractEvent e) {
		if (!e.hasItem())
			return;
		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			WorldConfiguration conf = get(e.getPlayer().getWorld().getName());
			Player pl = e.getPlayer();
			String perm = e.getItem().getType().toString().toLowerCase();
			if (e.getItem().getType().equals(Material.FIREWORK)) {
				if (conf.get(FIREWORKS)
						&& !pl.hasPermission("entitymanager.interact." + perm)) {
					this.c(e);
					alert(conf, "Player " + pl.getName()
							+ " tried to use a firework.");
					alert(conf, pl,
							"&cYou don't have permission to use fireworks.");

				}
			} else if (e.getItem().getType().equals(Material.MONSTER_EGG)) {
				EntityType type = EntityType
						.fromId(e.getItem().getDurability());
				if (conf.getBlockedEggs().contains(type)) {
					if (!pl.hasPermission("entitymanager.spawn."
							+ type.toString().toLowerCase())) {
						String mob = type.toString().toLowerCase() + "s.";
						this.c(e);
						alert(conf, "Player " + pl.getName()
								+ " tried to spawn a " + mob.replace("s", ""));
						alert(conf, pl, "&cYou don't have permission to spawn "
								+ mob);
					}
				}
			} else if (conf.getBlockedUsage().contains(e.getItem().getType())) {
				if (e.getItem().getType() == Material.POTION) {
					ItemStack is = e.getItem();
					Potion b = ConfigHelper.fromDamage(is.getDurability());
					if (conf.getPotions().contains(b)) {
						this.c(e);
						alert(conf, "Player " + pl.getName()
								+ " tried to use an " + b.getType().name()
								+ " potion" + ".");
						alert(conf, pl,
								"&cYou don't have permission to use that &6"
										+ b.getType().name() + " potion"
										+ "&c.");
					}
					return;
				}
				if (!pl.hasPermission("entitymanager.interact." + perm)) {
					String item = perm.replace("_", " ");
					this.c(e);
					alert(conf, "Player " + pl.getName() + " tried to use an "
							+ item + ".");
					alert(conf, pl,
							"&cYou don't have permission to use that &6" + item
									+ "&c.");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPvp(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player attacked = ((Player) e.getEntity());
			WorldConfiguration conf = get(attacked.getWorld().getName());
			if (e.getDamager() instanceof Player) {
				Player ag = ((Player) e.getDamager());
				e.setCancelled(a(conf, ag, attacked.getName()));
			} else if (e.getDamager() instanceof Projectile
					&& !(e.getDamager() instanceof EnderPearl)) {
				Entity a = ((Projectile) e.getDamager()).getShooter();
				if (a instanceof Player) {
					Player p = (Player) a;
					e.setCancelled(a(conf, p, attacked.getName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTrade(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof org.bukkit.entity.Villager) {
			Player pl = e.getPlayer();
			WorldConfiguration conf = get(pl.getWorld().getName());
			if (conf.get(THUNDER) && !pl.hasPermission(p[0])) {
				e.setCancelled(true);
				alert(conf, "Player " + pl.getName() + " tried to trade ");
				alert(conf, pl, "&cYou don't have permission to trade.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onArrowShoot(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = ((Player) e.getEntity());
			WorldConfiguration conf = get(player.getWorld().getName());
			if (conf.get(SHOOTING) && !player.hasPermission(p[1])) {
				e.setCancelled(true);
				alert(conf, "Player " + player.getName()
						+ " attempted to shoot a bow.");
				alert(conf, player,
						"&cYou don't have permission to shoot bows.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchant(EnchantItemEvent e) {
		Player player = e.getEnchanter();
		WorldConfiguration conf = get(player.getWorld().getName());
		if (conf.get(ENCHANTING) && !player.hasPermission(p[2])) {
			e.setCancelled(true);
			alert(conf, "Player " + player.getName() + " tried to enchant a "
					+ e.getItem().getType().toString().toLowerCase());
			alert(conf, player, "&cYou don't have permission to enchant.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAnvilEnchant(InventoryClickEvent e) {
		if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
			if (e.getSlot() == 9) {
				if (e.getWhoClicked() instanceof Player) {
					Player p = (Player) e.getWhoClicked();
					WorldConfiguration conf = get(p.getWorld().getName());
					if (conf.get(ENCHANTING) && !p.hasPermission(this.p[3])) {
						e.setCancelled(true);
						alert(conf, "Player "
								+ p.getName()
								+ " tried to enchant a "
								+ e.getCurrentItem().getType().toString()
										.toLowerCase() + " in an anvil.");
						alert(conf, p,
								"&cYou don't have permission to use anvils.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent e) {
		Entity ent = e.getEntity();
		WorldConfiguration conf = get(ent.getWorld().getName());
		if (conf.getBlockedMobs().contains(e.getEntityType())
				|| conf.getBlockedReasons().contains(e.getSpawnReason())) {
			e.setCancelled(true);
			return;
		} else if (e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
			mobs.add(e.getEntity());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Creature) {
			WorldConfiguration conf = get(e.getEntity().getWorld().getName());
			if (mobs.contains(e.getEntity())) {
				if (conf.get(NOEXP)) {
					e.setDroppedExp(0);
				} else if (conf.get(NODROPS)) {
					e.getDrops().clear();
				}
				mobs.remove(e.getEntity());
			}
			if (!conf.get(EDEATHEXP)) {
				e.setDroppedExp(0);
			}
			if (!conf.get(EDEATHDROPS)) {
				e.getDrops().clear();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		WorldConfiguration conf = get(e.getEntity().getWorld().getName());
		if (e.getEntity().hasPermission(this.p[4]) || conf.get(PDEATHEXP)) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent e) {
		WorldConfiguration conf = get(e.getBlock().getWorld().getName());
		if (conf.getBlockedDispense().contains(e.getItem().getType())) {
			if (e.getBlock().getType() == Material.POTION) {
				ItemStack is = e.getItem();
				Potion b = ConfigHelper.fromDamage(is.getDurability());
				if (conf.getDPotions().contains(b)) {
					e.setCancelled(true);
				}
				return;
			}
			e.setCancelled(true);
			return;
		}
	}
}
