package net.milkycraft;

import static net.milkycraft.objects.Option.*;
import static net.milkycraft.objects.Type.ALL;
import static net.milkycraft.objects.Type.BABY;
import static net.milkycraft.objects.Type.BOTH;
import static org.bukkit.entity.EntityType.EGG;
import static org.bukkit.entity.EntityType.ENDER_PEARL;
import static org.bukkit.entity.EntityType.FISHING_HOOK;
import static org.bukkit.entity.EntityType.SNOWBALL;
import static org.bukkit.entity.EntityType.SPLASH_POTION;
import static org.bukkit.entity.EntityType.THROWN_EXP_BOTTLE;
import static org.bukkit.entity.EntityType.fromId;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.HashSet;
import java.util.Set;

import net.milkycraft.config.WorldConfiguration;
import net.milkycraft.objects.Type;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public class AuxiliaryListener extends Utility implements Listener {

	private Set<Entity> mobs = new HashSet<Entity>();

	public AuxiliaryListener(EntityManager manager) {
		super(manager);
		super.register(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent e) {
		LivingEntity en = e.getEntity();
		WorldConfiguration conf = a(en);
		if (conf.get(SDISABLE) || conf.getSet5().contains(e.getSpawnReason().toString())) {
			e.setCancelled(true);
			return;
		}
		EntityType type = e.getEntityType();
		if (conf.has(type)) {
			if (en instanceof Ageable) {
				if (en instanceof Sheep) {
					Sheep sheep = (Sheep) en;
					if (!sheep.isAdult()) {
						if (conf.block(type, BABY, sheep.getColor().getColor()))
							e.setCancelled(true);
						return;
					}
					if (conf.block(type, ALL, sheep.getColor().getColor()))
						e.setCancelled(true);
					return;
				}
				Ageable a = (Ageable) en;
				if (!a.isAdult())
					if (conf.block(type, BABY))
						e.setCancelled(true);
				return;
			} else if (en instanceof Zombie) {
				Zombie z = (Zombie) en;
				if (z.isBaby() && !z.isVillager()) {
					if (conf.block(type, BABY))
						e.setCancelled(true);
				} else if (z.isVillager() && !z.isBaby()) {
					if (conf.block(type, Type.VILLAGER))
						e.setCancelled(true);
				} else if (z.isBaby() || z.isVillager()) {
					if (conf.block(type, BOTH))
						e.setCancelled(true);
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		WorldConfiguration conf = a(e.getEntity().getWorld());
		Player p = (Player) e.getEntity().getShooter();
		EntityType type = e.getEntity().getType();
		if (type == EGG) {
			if (conf.usage(344)) {
				if (!b(p, "entitymanager.interact.egg")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName() + " tried to throw a egg");
					al(conf, p, "&cYou don't have permission to throw eggs.");
				}
			}
		} else if (type == SNOWBALL) {
			if (conf.usage(322)) {
				if (!b(p, "entitymanager.interact.snow_ball")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName() + " tried to throw a snowball");
					al(conf, p, "&cYou don't have permission to throw snowballs.");
				}
			}
		} else if (type == THROWN_EXP_BOTTLE) {
			if (conf.usage(384)) {
				if (!b(p, "entitymanager.interact.exp_bottle")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName() + " tried to use an exp bottle.");
					al(conf, p, "&cYou don't have permission to throw exp bottles.");
				}
			}
		} else if (type == ENDER_PEARL) {
			if (conf.usage(368)) {
				if (!b(p, "entitymanager.interact.ender_pearl")) {
					e.setCancelled(true);
					al(conf, "Player " + p.getName() + " tried to use an ender pearl");
					al(conf, p, "&cYou don't have permission to throw ender pearls.");
				}
			}
		} else if (type == SPLASH_POTION) {
			ItemStack is = p.getItemInHand();
			if (conf.usagePotion(is.getDurability())) {
				if (!b(p, "entitymanager.interact.potion_" + is.getDurability())) {
					e.setCancelled(true);
					Potion b = Potion.fromItemStack(is);
					al(conf, "Player " + p.getName() + " tried to throw a " + c(b) + " potion");
					al(conf, p, "&cYou don't have permission to throw " + c(b) + " potions.");
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Creature) {
			WorldConfiguration conf = a(e.getEntity());
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
	public void onTrade(PlayerInteractEntityEvent e) {
		Player pl = e.getPlayer();
		if (e.getRightClicked() instanceof org.bukkit.entity.Villager) {
			WorldConfiguration conf = a(e.getRightClicked());
			if (conf.get(TRADING) && !b(pl, p[0])) {
				e.setCancelled(true);
				al(conf, "Player " + pl.getName() + " tried to trade ");
				al(conf, pl, "&cYou don't have permission to trade.");
			}
		} else if (e.getRightClicked() instanceof Sheep) {
			WorldConfiguration conf = a(e.getRightClicked());
			EntityType type = e.getRightClicked().getType();
			Player p = e.getPlayer();
			ItemStack is = p.getItemInHand();
			if (conf.has(type) && is.getType() == Material.INK_SACK) {
				@SuppressWarnings("deprecation")
				DyeColor dc = DyeColor.getByDyeData((byte) is.getDurability());
				String c = dc.toString().toLowerCase();
				Sheep sheep = (Sheep) e.getRightClicked();
				if (!sheep.isAdult()) {
					if (conf.block(type, BABY, dc.getColor()) && !pl.isOp()) {
						e.setCancelled(true);
						al(conf, p, "&cThat color of sheep is blocked (" + c + ")");
						al(conf, "Player " + p.getName() + " tried to dye a sheep " + c);
						update(sheep);
						return;
					}
					return;
				}
				if (conf.block(type, ALL, dc.getColor()) && !pl.isOp()) {
					e.setCancelled(true);
					al(conf, p, "&cThat color of sheep is blocked (" + c + ")");
					al(conf, "Player " + p.getName() + " tried to dye a sheep " + c);
					update(sheep);
				}
				return;
			}
		}
	}

	private void a(Player pl, String str, PlayerInteractEvent e, WorldConfiguration c) {
		if (!b(pl, "entitymanager.spawn." + str)) {
			if (b(e.getClickedBlock())) {
				e.setUseItemInHand(Result.DENY);
				e.setCancelled(true);
				return;
			}
			e.setUseItemInHand(Result.DENY);
			e.setCancelled(true);
			al(c, "Player " + pl.getName() + " tried to spawn a " + str);
			al(c, pl, "&cYou don't have permission to spawn " + str + "s.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteraction(PlayerInteractEvent e) {
		if (!e.hasItem())
			return;
		if (e.getAction() == RIGHT_CLICK_AIR || e.getAction() == RIGHT_CLICK_BLOCK) {
			WorldConfiguration c = get(e.getPlayer().getWorld().getName());
			Player pl = e.getPlayer();
			String str = e.getItem().getType().toString().toLowerCase();
			if (e.getItem().getType().equals(Material.FIREWORK)) {
				if (c.get(FIREWORKS) && !b(pl, "entitymanager.interact." + str)) {
					if (b(e.getClickedBlock())) {
						e.setUseItemInHand(Result.DENY);
						return;
					}
					e.setUseItemInHand(Result.DENY);
					e.setCancelled(true);
					al(c, "Player " + pl.getName() + " tried to use a firework.");
					al(c, pl, "&cYou don't have permission to use fireworks.");
				}
			} else if (e.getItem().getType().equals(Material.MONSTER_EGG)) {
				if (c.get(EDISABLE))
					a(pl, str, e, c);
				else {
					EntityType type = fromId(e.getItem().getDurability());
					if (c.getSet3().contains(type.getTypeId()))
						a(pl, str, e, c);
				}
			} else if (e.getItem().getType() == Material.POTION) {
				ItemStack is = e.getItem();
				if (b(pl, super.p[7] + is.getDurability()))
					return;
				if (c.usagePotion(is.getDurability())) {
					if (b(e.getClickedBlock())) {
						e.setUseItemInHand(Result.DENY);
						return;
					}
					e.setUseItemInHand(Result.DENY);
					e.setCancelled(true);
					Potion b = Potion.fromItemStack(e.getItem());
					al(c, "Player " + pl.getName() + " tried to use an " + c(b) + " potion" + ".");
					al(c, pl, "&cYou don't have permission to use that &6" + c(b) + " potion"
							+ "&c.");
				}
				return;
			} else {
				if (c.usage(e.getItem().getTypeId())) {
					if (!b(pl, "entitymanager.interact." + str)) {
						String item = str.replace("_", " ");
						if (b(e.getClickedBlock())) {
							e.setUseItemInHand(Result.DENY);
							return;
						}
						e.setUseItemInHand(Result.DENY);
						e.setCancelled(true);
						al(c, "Player " + pl.getName() + " tried to use an " + item + ".");
						al(c, pl, "&cYou don't have permission to use that &6" + item + "&c.");
					}
				}
			}
		}
	}
}
