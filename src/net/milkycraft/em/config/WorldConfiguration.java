package net.milkycraft.em.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.milkycraft.em.EntityManager;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.Potion;

public class WorldConfiguration extends ConfigLoader {

	private final String world;
	private String confRev;
	private final String localRev = "1.4";
	private boolean[] bools = new boolean[20];
	private long[] longs = new long[2];
	private Set<Material> usageBlock = new HashSet<Material>();
	private Set<Material> disBlock = new HashSet<Material>();
	private Set<EntityType> disEggs = new HashSet<EntityType>();
	private Set<EntityType> disMobs = new HashSet<EntityType>();
	private Set<SpawnReason> disReasons = new HashSet<SpawnReason>();
	private Set<Potion> dispots = new HashSet<Potion>();
	private Set<Potion> disDpots = new HashSet<Potion>();

	public WorldConfiguration(EntityManager plugin, String world) {
		super(plugin, world + ".yml");
		super.saveIfNotExist();
		this.world = world;
		super.load();
	}

	@Override
	protected void loadKeys() {
		confRev = config.getString("Settings.Config_Revision", "0.1");
		performUpdate(confRev);
		bools[0] = config.getBoolean("Settings.Admin-Alerts", true);
		bools[1] = config.getBoolean("Settings.Player-Alerts", true);
		bools[2] = config.getBoolean("Settings.Logging", true);
		bools[3] = config.getBoolean("Disable.Weather.Rain", false);
		bools[4] = config.getBoolean("Disable.Weather.Thunder", false);
		bools[5] = config.getBoolean("Disable.Weather.Lightning", false);
		bools[6] = config.getBoolean("Disable.Interaction.PVP", false);
		bools[7] = config.getBoolean("Disable.Interaction.Fishing", false);
		bools[8] = config.getBoolean("Disable.Interaction.Shooting", false);
		bools[9] = config.getBoolean("Disable.Interaction.Enchanting", false);
		bools[10] = config.getBoolean("Disable.Interaction.Fireworks", false);
		bools[11] = config.getBoolean("Disable.Interaction.Trading", false);
		bools[12] = config.getBoolean("Disable.Other.Monster_Spawner_Exp",
				false);
		bools[13] = config.getBoolean("Disable.Other.Monster_Spawner_Drops",
				false);
		bools[14] = config.getBoolean("TimeManager.Enabled", false);
		bools[15] = config.getBoolean("DeathManager.Player.Keep_Exp", false);
		bools[16] = config.getBoolean("DeathManager.Entity.Drop_Exp", true);
		bools[17] = config.getBoolean("DeathManager.Entity.Drop_Items", true);
		longs[0] = config.getLong("TimeManager.Target_Time", 12000L);
		longs[1] = config.getLong("TimeManager.Set_Every", 100L);
		loadLists();
	}

	private void loadLists() {
		final EntityManager em = super.plugin;
		for (String s : config.getStringList("Disable.Usage.Blocked_Items")) {
			try {
				usageBlock.add(Material.valueOf(s.toUpperCase()));
			} catch (Exception ex) {
				if (s.toLowerCase().startsWith("potion")) {
					String[] args = s.split(":");
					Integer id = Integer.parseInt(args[1]);
					Potion p = ConfigHelper.fromDamage(id);
					this.dispots.add(p);
					usageBlock.add(Material.POTION);
				} else {
					em.severe("Found invalid material in usage items: " + s);
					em.severe("Reference: http://goo.gl/f1Nmb");
				}
			}
		}
		for (String s : config
				.getStringList("Disable.Dispensing.Blocked_Items")) {
			try {
				disBlock.add(Material.valueOf(s.toUpperCase()));
			} catch (Exception ex) {
				if (s.toLowerCase().startsWith("potion")) {
					String[] args = s.split(":");
					Integer id = Integer.parseInt(args[1]);
					Potion p = ConfigHelper.fromDamage(id);
					this.disDpots.add(p);
					disBlock.add(Material.POTION);
				} else {
					em.severe("Found invalid material in dispense items: " + s);
					em.severe("Reference: http://goo.gl/f1Nmb");
				}
			}
		}
		for (String s : config.getStringList("EggManager.Disabled_Eggs")) {
			try {
				disEggs.add(EntityType.valueOf(s.toUpperCase()));
			} catch (Exception ex) {
				em.severe("Found invalid entitytype in blocked eggs");
				em.severe("Reference: http://goo.gl/E7mVB");
			}
		}
		for (String s : config.getStringList("SpawnManager.Disallowed_Mobs")) {
			try {
				disMobs.add(EntityType.valueOf(s.toUpperCase()));
			} catch (Exception ex) {
				em.severe("Found invalid entitytype in blocked mobs");
				em.severe("Reference: http://goo.gl/E7mVB");
			}
		}
		for (String s : config.getStringList("SpawnManager.Disallowed_Reasons")) {
			try {
				disReasons.add(SpawnReason.valueOf(s.toUpperCase()));
			} catch (Exception ex) {
				em.severe("Found invalid spawnreason in blocked spawn reasons");
				em.severe("Reference: http://goo.gl/a4XRB");
			}
		}
	}

	public void performUpdate(String revision) {
		if (this.localRev.equals(revision)) {
			return;
		}
		if (revision.equals("0.1")) {
			super.set("Settings.Config_Revision", this.localRev);
			super.set("Disable.Usage.Potions", false);
			super.set("Disable.Usage.Splash_Potions", false);
			super.set("Disable.Interaction.Trading", false);
			plugin.info("Successfully updated " + fileName + " to 1.0");
		} else if (revision.equals("1.0")) {
			super.set("Settings.Config_Revision", this.localRev);
			List<String> list = new ArrayList<String>();
			list.add(SpawnReason.LIGHTNING.toString().toLowerCase());
			super.set("SpawnManager.Disallowed_Reasons", list);
			plugin.info("Successfully updated " + fileName + " to 1.1");
		} else if (revision.equals("1.1")) {
			super.set("Settings.Config_Revision", this.localRev);
			super.set("DeathManager.Keep_Exp", false);
			plugin.info("Successfully updated " + fileName + " to 1.2");
		} else if (revision.equals("1.2")) {
			super.set("Settings.Config_Revision", this.localRev);
			super.set("DeathManager.Keep_Exp", null);
			super.set("DeathManager.Player.Keep_Exp", false);
			super.set("DeathManager.Entity.Drop_Exp", true);
			super.set("DeathManager.Entity.Drop_Items", true);
			plugin.info("Successfully updated " + fileName + " to 1.3");
		} else if (revision.equals("1.3")) {
			super.set("Settings.Config_Revision", this.localRev);
			super.set("Usage.Potions", null);
			super.set("Usage.Splash_Potions", null);
			List<String> list = config.getStringList("Usage.Blocked_Items");
			list.add("'Potion:16384'");
			super.set("Usage.Blocked_Items", list);
			plugin.info("Successfully updated " + fileName + " to 1.4");
		} else {
			super.plugin
					.warn("Could not update config, mismatched config revision: Local: "
							+ this.localRev + " File: " + revision);
		}
		this.reload();
	}

	public String getWorld() {
		return this.world;
	}

	public Set<Material> getBlockedDispense() {
		return this.disBlock;
	}

	public Set<Material> getBlockedUsage() {
		return this.usageBlock;
	}

	public Set<EntityType> getBlockedEggs() {
		return this.disEggs;
	}

	public Set<EntityType> getBlockedMobs() {
		return this.disMobs;
	}

	public Set<SpawnReason> getBlockedReasons() {
		return this.disReasons;
	}
	
	public Set<Potion> getPotions() {
		return this.dispots;
	}
	
	public Set<Potion> getDPotions() {
		return this.disDpots;
	}
 
	public void reload() {
		this.disBlock.clear();
		this.disEggs.clear();
		this.disMobs.clear();
		this.disReasons.clear();
		this.usageBlock.clear();
		super.rereadFromDisk();
		super.load();
	}

	public boolean get(Option op) {
		return bools[op.getId()];
	}

	public long g(Option op) {
		return longs[op.getId()];
	}
}
