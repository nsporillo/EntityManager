package net.milkycraft.em;

import static net.milkycraft.em.config.Option.*;
import static org.bukkit.entity.EntityType.fromId;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.milkycraft.em.config.Type;
import net.milkycraft.em.config.WorldConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public class PrimaryListener extends Utility implements Listener {

	private Set<Entity> mobs = new HashSet<Entity>();
	private Map<String, List<ItemStack>> drops = new HashMap<String, List<ItemStack>>();
	private String[] p = { "entitymanager.interact.trade",
			"entitymanager.interact.shoot", "entitymanager.interact.enchant",
			"entitymanager.interact.anvil", "entitymanager.death.keepexp",
			"entitymanager.death.keepitems" };

	public PrimaryListener(EntityManager manager) {
		super(manager);
		super.register(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteraction(PlayerInteractEvent e) {
		if (!e.hasItem())
			return;
		if (e.getAction() == RIGHT_CLICK_AIR
				|| e.getAction() == RIGHT_CLICK_BLOCK) {
			WorldConfiguration c = get(e.getPlayer().getWorld().getName());
			Player pl = e.getPlayer();
			String str = e.getItem().getType().toString().toLowerCase();
			if (e.getItem().getType().equals(Material.FIREWORK)) {
				if (c.get(FIREWORKS) && !b(pl, "entitymanager.interact." + str)) {
					if (b(e.getClickedBlock())) {
						e.setUseItemInHand(Result.DENY);
						return;
					} else {
						e.setUseItemInHand(Result.DENY);
						e.setCancelled(true);
						al(c, "Player " + pl.getName()
								+ " tried to use a firework.");
						al(c, pl,
								"&cYou don't have permission to use fireworks.");
					}
				}
			} else if (e.getItem().getType().equals(Material.MONSTER_EGG)) {
				EntityType type = fromId(e.getItem().getDurability());
				if (c.getMap(1).containsKey(type.toString())) {
					if (!b(pl, "entitymanager.spawn." + str)) {
						if (b(e.getClickedBlock())) {
							e.setUseItemInHand(Result.DENY);
							return;
						} else {
							e.setUseItemInHand(Result.DENY);
							e.setCancelled(true);
							al(c, "Player " + pl.getName()
									+ " tried to spawn a " + str);
							al(c, pl, "&cYou don't have permission to spawn "
									+ str + "s.");
						}
					}
				}
			} else if (c.get(1).contains(e.getItem().getType().toString())) {
				if (e.getItem().getType() == Material.POTION) {
					Potion b = fromDamage(e.getItem().getDurability());
					if (b(pl, "entitymanager.interact.potion." + b.getNameId())) {
						return;
					}
					if (c.get(6).contains(b.getNameId())) {
						if (b(e.getClickedBlock())) {
							e.setUseItemInHand(Result.DENY);
							return;
						} else {
							e.setUseItemInHand(Result.DENY);
							e.setCancelled(true);
							al(c, "Player " + pl.getName()
									+ " tried to use an " + c(b) + " potion"
									+ ".");
							al(c, pl,
									"&cYou don't have permission to use that &6"
											+ c(b) + " potion" + "&c.");
						}
					}
					return;
				}
				if (!b(pl, "entitymanager.interact." + str)) {
					String item = str.replace("_", " ");
					if (b(e.getClickedBlock())) {
						e.setUseItemInHand(Result.DENY);
						return;
					} else {
						e.setUseItemInHand(Result.DENY);
						e.setCancelled(true);
						al(c, "Player " + pl.getName() + " tried to use an "
								+ item + ".");
						al(c, pl, "&cYou don't have permission to use that &6"
								+ item + "&c.");
					}
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
			if (conf.get(TRADING) && !b(pl, p[0])) {
				e.setCancelled(true);
				al(conf, "Player " + pl.getName() + " tried to trade ");
				al(conf, pl, "&cYou don't have permission to trade.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onArrowShoot(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player pl = ((Player) e.getEntity());
			WorldConfiguration conf = get(pl.getWorld().getName());
			if (conf.get(SHOOTING) && !b(pl, p[1])) {
				e.setCancelled(true);
				al(conf, "Player " + pl.getName() + " tried to shoot a bow.");
				al(conf, pl, "&cYou don't have permission to shoot bows.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchant(EnchantItemEvent e) {
		Player pl = e.getEnchanter();
		WorldConfiguration conf = get(pl.getWorld().getName());
		if (conf.get(ENCHANTING) && !b(pl, p[2])) {
			e.setCancelled(true);
			al(conf, "Player " + pl.getName() + " tried to enchant a "
					+ e.getItem().getType().toString().toLowerCase());
			al(conf, pl, "&cYou don't have permission to enchant.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAnvilEnchant(InventoryClickEvent e) {
		if (e.getInventory().getType() == InventoryType.ANVIL) {
			if (e.getSlotType() == SlotType.RESULT) {
				if (e.getWhoClicked() instanceof Player) {
					Player pl = (Player) e.getWhoClicked();
					WorldConfiguration conf = a(pl.getWorld());
					if (conf.get(ENCHANTING) && !b(pl, p[3])) {
						e.setCancelled(true);
						al(conf, "Player "
								+ pl.getName()
								+ " tried to enchant a "
								+ e.getCurrentItem().getType().toString()
										.toLowerCase() + " in an anvil.");
						al(conf, pl,
								"&cYou don't have permission to use anvils.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent e) {
		WorldConfiguration conf = a(e.getEntity());
		String et = e.getEntityType().toString();
		if (conf.getMap(2).containsKey(et)) {
			if (e.getEntity() instanceof Ageable) {
				Ageable a = (Ageable) e.getEntity();
				if (!a.isAdult()) {
					if (conf.getMap(2).get(et) == Type.BABY) {
						e.setCancelled(true);
					} else {
						return;
					}
				} else {
					return;
				}
			} else if (e.getEntity() instanceof Zombie) {
				Zombie z = (Zombie) e.getEntity();
				if (z.isBaby() || z.isVillager()) {
					if (conf.getMap(2).get(et) == Type.BOTH) {
						e.setCancelled(true);
					}
				} else if (z.isBaby() && !z.isVillager()) {
					if (conf.getMap(2).get(et) == Type.BABY) {
						e.setCancelled(true);
					}
				} else if (z.isVillager() && !z.isBaby()) {
					System.out.println("Villager!");
					if (conf.getMap(2).get(et) == Type.VILLAGER) {
						e.setCancelled(true);
					}
				}
			} else if (conf.getMap(2).get(et) == Type.ALL) {
				e.setCancelled(true);
			}
		} else if (conf.get(5).contains(e.getSpawnReason().toString())) {
			e.setCancelled(true);
		} else if (e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
			mobs.add(e.getEntity());
		}
		if (conf.get(NOMOBARMOR)) {
			e.getEntity().getEquipment().setArmorContents(null);
			e.getEntity().getEquipment().getItemInHand().setTypeId(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Creature) {
			WorldConfiguration conf = a(e.getEntity());
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
	public void onPotionSplash(PotionSplashEvent e) {
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			Potion b = fromDamage(e.getPotion().getItem().getDurability());
			if (b(p, "entitymanager.interact.potion." + b.getNameId())) {
				return;
			}
			WorldConfiguration conf = a(p.getWorld());
			if (conf.get(6).contains(b.getNameId())) {
				e.setCancelled(true);
				al(conf, "Player " + p.getName() + " tried to use an " + c(b)
						+ " potion" + ".");
				al(conf, p, "&cYou don't have permission to use that &6" + c(b)
						+ " potion" + "&c.");
			}
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player pl = e.getEntity();
		WorldConfiguration conf = a(pl.getWorld());
		if (b(pl, p[5]) || conf.get(PDEATHITEMS)) {
			drops.put(pl.getName(), e.getDrops());
		}
		if (b(pl, p[4]) || conf.get(PDEATHEXP)) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		if (drops.containsKey(p.getName())) {
			Bukkit.getScheduler().runTaskLater(getHandle(), new Runnable() {

				@Override
				public void run() {
					WorldConfiguration wc = a(p.getWorld());
					al(wc, "Player " + p.getName()
							+ " respawned with their items");
					al(wc, p, "&6Your items were returned after death!");
					for (ItemStack is : drops.get(p.getName())) {
						if (is != null) {
							p.getInventory().addItem(is);
						}
					}
					drops.remove(p.getName());
				}

			}, 10L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent e) {
		WorldConfiguration conf = a(e.getBlock().getWorld());
		if (conf.get(2).contains(e.getItem().getType().toString())) {
			if (e.getItem().getType() == Material.POTION) {
				Potion b = fromDamage(e.getItem().getDurability());
				if (conf.get(7).contains(b.getNameId())) {
					e.setCancelled(true);
				}
				return;
			}
			e.setCancelled(true);
			return;
		}
	}
}
