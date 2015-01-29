package net.milkycraft;

import static net.milkycraft.types.Option.*;
import static net.milkycraft.types.Permission.*;
import static net.milkycraft.types.Type.*;
import static org.bukkit.entity.EntityType.EGG;
import static org.bukkit.entity.EntityType.ENDER_PEARL;
import static org.bukkit.entity.EntityType.FISHING_HOOK;
import static org.bukkit.entity.EntityType.SNOWBALL;
import static org.bukkit.entity.EntityType.SPLASH_POTION;
import static org.bukkit.entity.EntityType.THROWN_EXP_BOTTLE;
import static org.bukkit.entity.EntityType.fromId;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.milkycraft.config.WorldConfiguration;
import net.milkycraft.types.Type;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.projectiles.ProjectileSource;

public class EventListener extends Utility implements Listener {

	private Map<String, List<ItemStack>> drops = new HashMap<String, List<ItemStack>>();
	private Set<Entity> mobs = new HashSet<Entity>();

	public EventListener(EntityManager manager) {
		super(manager);
		super.register(this);
	}

	private void handleSpawnTry(Player pl, String str, PlayerInteractEvent e, WorldConfiguration c) {
		if (!hasPermission(pl, sNTITY + str)) {
			Block cb = e.getClickedBlock();
			if (shouldBlock(cb)) {
				e.setUseItemInHand(Result.DENY);
				e.setCancelled(true);
				al(c, pl, "&cSwitch item in hand to use " + cb.getType().name().toLowerCase());
				return;
			}
			e.setUseItemInHand(Result.DENY);
			e.setCancelled(true);
			al(c, "Player " + pl.getName() + " tried to spawn a " + str);
			al(c, pl, "&cYou don't have permission to spawn " + str + "s.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAnvilEnchant(InventoryClickEvent e) {
		if (e.getInventory().getType() == InventoryType.ANVIL) {
			if (e.getSlotType() == SlotType.RESULT) {
				if (e.getWhoClicked() instanceof Player) {
					Player pl = (Player) e.getWhoClicked();
					WorldConfiguration conf = getConfig(pl.getWorld());
					if (conf.get(ENCHANTING) && !hasPermission(pl, iANVIL)) {
						e.setCancelled(true);
						al(conf, "Player " + pl.getName() + " tried to enchant a "
								+ e.getCurrentItem().getType().toString().toLowerCase()
								+ " in an anvil.");
						al(conf, pl, "&cYou don't have permission to use anvils.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onArrowShoot(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player pl = ((Player) e.getEntity());
			WorldConfiguration conf = get(pl.getWorld().getName());
			if (conf.get(SHOOTING) && !hasPermission(pl, iSHOOT)) {
				e.setCancelled(true);
				al(conf, "Player " + pl.getName() + " tried to shoot a bow.");
				al(conf, pl, "&cYou don't have permission to shoot bows.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockDispense(BlockDispenseEvent e) {
		WorldConfiguration conf = getConfig(e.getBlock().getWorld());
		ItemStack is = e.getItem();
		if (is.getType() == Material.POTION)
			e.setCancelled(conf.dispensePotion(is.getDurability()));
		else
			e.setCancelled(conf.dispense(is.getTypeId()));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Creature) {
			WorldConfiguration conf = getConfigByEntity(e.getEntity());
			if (mobs.contains(e.getEntity())) {
				if (conf.get(NOEXP))
					e.setDroppedExp(0);
				else if (conf.get(NODROPS))
					e.getDrops().clear();
				mobs.remove(e.getEntity());
			}
			if (!conf.get(EDEATHEXP))
				e.setDroppedExp(0);
			if (!conf.get(EDEATHDROPS))
				e.getDrops().clear();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		LivingEntity en = e.getEntity();
		WorldConfiguration conf = getConfigByEntity(en);
		if (conf.get(SDISABLE) || conf.getSet5().contains(e.getSpawnReason().toString())) {
			e.setCancelled(true);
			return;
		}
		EntityType type = e.getEntityType();
		if (conf.has(type)) {
			if (en instanceof Ageable) {
				if (en instanceof Sheep) {
					Sheep sheep = (Sheep) en;
					Color color = sheep.getColor().getColor();
					if (sheep.isAdult()) {
						e.setCancelled(conf.block(type, ALL, color));
						return;
					}
					e.setCancelled(conf.block(type, BABY, color));
					return;
				}
				if (!((Ageable) en).isAdult())
					e.setCancelled(conf.block(type, BABY));
				return;
			} else if (en instanceof Zombie) {
				Zombie z = (Zombie) en;
				if (z.isBaby() && !z.isVillager()) {
					e.setCancelled(conf.block(type, BABY));
				} else if (z.isVillager() && !z.isBaby()) {
					e.setCancelled(conf.block(type, Type.VILLAGER));
				} else if (z.isBaby() || z.isVillager()) {
					e.setCancelled(conf.block(type, BOTH));
				}
				return;
			}
			e.setCancelled(true);
		} else if (e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
			mobs.add(e.getEntity());
		}
		if (conf.get(NOMOBARMOR)) {
			en.getEquipment().setArmorContents(null);
			en.getEquipment().getItemInHand().setType(Material.AIR);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		Player pl = e.getPlayer();
		if (e.getRightClicked() instanceof org.bukkit.entity.Villager) {
			WorldConfiguration conf = getConfigByEntity(e.getRightClicked());
			if (conf.get(TRADING) && !hasPermission(pl, iTRADE)) {
				e.setCancelled(true);
				al(conf, "Player " + pl.getName() + " tried to trade ");
				al(conf, pl, "&cYou don't have permission to trade.");
			}
		} else if (e.getRightClicked() instanceof Sheep) {
			WorldConfiguration conf = getConfigByEntity(e.getRightClicked());
			EntityType type = e.getRightClicked().getType();
			Player p = e.getPlayer();
			ItemStack is = p.getItemInHand();
			if (conf.has(type) && is.getType() == Material.INK_SACK) {
				DyeColor dc = DyeColor.getByDyeData((byte) is.getDurability());
				String c = dc.toString().toLowerCase();
				Sheep sheep = (Sheep) e.getRightClicked();
				if (sheep.isAdult())
					e.setCancelled(conf.block(type, ALL, dc.getColor()) && !pl.isOp());
				else
					e.setCancelled(conf.block(type, BABY, dc.getColor()) && !pl.isOp());
				if (e.isCancelled()) {
					al(conf, p, "&cThat color of sheep is blocked (" + c + ")");
					al(conf, p, "&cDye apply was client side - relog :)");
					al(conf, "Player " + p.getName() + " tried to dye a sheep " + c);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteraction(PlayerInteractEvent e) {
		if (!e.hasItem())
			return;
		if (e.getAction() == RIGHT_CLICK_AIR || e.getAction() == RIGHT_CLICK_BLOCK) {
			WorldConfiguration conf = getConfig(e.getPlayer().getWorld());
			Player pl = e.getPlayer();
			String str = e.getItem().getType().toString().toLowerCase();
			if (shouldBlock(e.getClickedBlock())) {
				e.setUseItemInHand(Result.DENY);
				return;
			}
			if (e.getItem().getType().equals(Material.FIREWORK)) {
				if (conf.get(FIREWORKS) && !hasPermission(pl, iUITEM + str)) {
					e.setUseItemInHand(Result.DENY);
					e.setCancelled(true);
					al(conf, "Player " + pl.getName() + " tried to use a firework.");
					al(conf, pl, "&cYou don't have permission to use fireworks.");
				}
			} else if (e.getItem().getType().equals(Material.MONSTER_EGG)) {
				if (conf.get(EDISABLE))
					handleSpawnTry(pl, str, e, conf);
				else {
					EntityType type = fromId(e.getItem().getDurability());
					if (conf.getSet3().contains(type.getTypeId()))
						handleSpawnTry(pl, str, e, conf);
				}
			} else if (e.getItem().getType() == Material.POTION) {
				short dura = e.getItem().getDurability();
				if (hasPermission(pl, iPTION) || hasPermission(pl, iPTION + "." + dura))
					return;
				if (conf.usagePotion(dura)) {
					e.setUseItemInHand(Result.DENY);
					e.setCancelled(true);
					warn(conf, pl, Potion.fromItemStack(e.getItem()));
				}
				return;
			} else {
				if (conf.usage(e.getItem().getTypeId())) {
					if (!hasPermission(pl, iUITEM + str)) {
						String item = str.replace("_", " ");
						e.setUseItemInHand(Result.DENY);
						e.setCancelled(true);
						al(conf, "Player " + pl.getName() + " tried to use an " + item + ".");
						al(conf, pl, "&cYou don't have permission to use that &6" + item + "&c.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemEnchant(EnchantItemEvent e) {
		Player pl = e.getEnchanter();
		WorldConfiguration conf = get(pl.getWorld().getName());
		if (conf.get(ENCHANTING) && !hasPermission(pl, iCHANT)) {
			e.setCancelled(true);
			al(conf, "Player " + pl.getName() + " tried to enchant a "
					+ e.getItem().getType().toString().toLowerCase());
			al(conf, pl, "&cYou don't have permission to enchant.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLightning(LightningStrikeEvent e) {
		e.setCancelled(getConfig(e.getWorld()).get(LIGHTNING));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCombat(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player attacked = ((Player) e.getEntity());
			WorldConfiguration conf = get(attacked.getWorld().getName());
			if (e.getDamager() instanceof Player) {
				Player ag = ((Player) e.getDamager());
				e.setCancelled(alert(conf, ag, attacked.getName()));
			} else if (e.getDamager() instanceof Projectile
					&& !(e.getDamager() instanceof EnderPearl)) {
				ProjectileSource a = ((Projectile) e.getDamager()).getShooter();
				if (a instanceof Player) {
					Player p = (Player) a;
					e.setCancelled(alert(conf, p, attacked.getName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player pl = e.getEntity();
		WorldConfiguration conf = getConfig(pl.getWorld());
		if (hasPermission(pl, dKITEM) || conf.get(PDEATHITEMS)) {
			drops.put(pl.getName(), e.getDrops());
			e.getDrops().clear();
		}
		if (hasPermission(pl, dKEEXP) || conf.get(PDEATHEXP)) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		if (drops.containsKey(p.getName())) {
			Bukkit.getScheduler().runTaskLater(getHandle(), new Runnable() {
				@Override
				public void run() {
					WorldConfiguration wc = getConfig(p.getWorld());
					al(wc, "Player " + p.getName() + " respawned with their items");
					al(wc, p, "&6Your items were returned after death!");
					for (ItemStack is : drops.get(p.getName()))
						if (is != null)
							p.getInventory().addItem(is);
					drops.remove(p.getName());
				}
			}, 1L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPortalMake(EntityCreatePortalEvent e) {
		if (e.getEntity() instanceof Player) {
			Player pl = ((Player) e.getEntity());
			WorldConfiguration conf = get(pl.getWorld().getName());
			if (conf.get(PORTAL_CREATE) && !hasPermission(pl, cPRTAL)) {
				e.setCancelled(true);
				al(conf, "Player " + pl.getName() + " tried to create portals");
				al(conf, pl, "&cYou don't have permission to create a portal.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent e) {
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			ItemStack is = e.getPotion().getItem();
			short dura = is.getDurability();

			WorldConfiguration conf = getConfig(p.getWorld());
			if (conf.usagePotion(is.getDurability())) {
				if (hasPermission(p, iPTION) || hasPermission(p, iPTION + "." + dura)) {
					return;
				}
				e.setCancelled(true);
				String potion = getName(Potion.fromItemStack(is)) + " potion";
				al(conf, "Player " + p.getName() + " tried to use an " + potion + ".");
				al(conf, p, "&cYou don't have permission to use that &6" + potion + "&c.");
				return;
			}

			int mult = conf.getMultiplier(is.getDurability());
			if (mult > 1) {
				for (LivingEntity le : e.getAffectedEntities()) {
					System.out.println("Old intensity: " + e.getIntensity(le));
					e.setIntensity(le, mult);
				}
				System.out.println("Potion multiplier: " + mult + " on " + "373:"
						+ is.getDurability());
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		WorldConfiguration conf = getConfigByEntity(e.getEntity());
		Player p = (Player) e.getEntity().getShooter();
		EntityType type = e.getEntity().getType();
		if (type == EGG) {
			e.setCancelled(conf.usage(344) && !hasPermission(p, iTHEGG));
		} else if (type == SNOWBALL) {
			e.setCancelled(conf.usage(322) && !hasPermission(p, iTSNOW));
		} else if (type == FISHING_HOOK) {
			e.setCancelled(conf.get(FISHN) && !hasPermission(p, iFISHN));
		} else if (type == THROWN_EXP_BOTTLE) {
			e.setCancelled(conf.usage(384) && !hasPermission(p, iTEXPB));
		} else if (type == ENDER_PEARL) {
			e.setCancelled(conf.usage(368) && !hasPermission(p, iPEARL));
		} else if (type == SPLASH_POTION) {
			Potion pot = Potion.fromItemStack(p.getItemInHand());
			if (conf.usagePotion(pot.getNameId())) {
				if (!hasPermission(p, iPTION + "_" + pot.getNameId())) {
					e.setCancelled(true);
					warn(conf, p, pot);
					return;
				}
			}
		}
		if (e.isCancelled()) {
			alert(conf, p, type);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onThunderChange(ThunderChangeEvent e) {
		boolean b = e.toThunderState();
		e.setCancelled(b ? getConfig(e.getWorld()).get(THUNDER) : false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeatherChange(WeatherChangeEvent e) {
		boolean b = e.toWeatherState();
		e.setCancelled(b ? getConfig(e.getWorld()).get(RAIN) : false);
	}
}
