package net.milkycraft.em.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import net.milkycraft.em.EntityManager;
import net.milkycraft.em.Utility;
import static net.milkycraft.em.config.Type.valueOf;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.Potion;

public class WorldConfiguration extends ConfigLoader {

	private final String world;
	private final String local = "1.5";
	private String confRev;
	private boolean[] b = new boolean[20];
	private long[] l = new long[2];
	private Set<String> usageBlock = new HashSet<String>();
	private Set<String> disDBlock = new HashSet<String>();
	private Map<String, Type> disEggs = new HashMap<String, Type>();
	private Map<String, Type> disMobs = new HashMap<String, Type>();
	private Set<String> disReasons = new HashSet<String>();
	private Set<Integer> disPots = new HashSet<Integer>();
	private Set<Integer> disDpots = new HashSet<Integer>();

	public WorldConfiguration(EntityManager plugin, String world) {
		super(plugin, world + ".yml");
		super.saveIfNotExist();
		this.world = world;
		super.load();
	}

	@Override
	protected void loadKeys() {
		confRev = c.getString("Settings.Config_Revision", "0.1");
		performUpdate(confRev);
		b[0] = c.getBoolean("Settings.Admin-Alerts", true);
		b[1] = c.getBoolean("Settings.Player-Alerts", true);
		b[2] = c.getBoolean("Settings.Logging", true);
		b[3] = c.getBoolean("Disable.Weather.Rain", false);
		b[4] = c.getBoolean("Disable.Weather.Thunder", false);
		b[5] = c.getBoolean("Disable.Weather.Lightning", false);
		b[6] = c.getBoolean("Disable.Interaction.PVP", false);
		b[7] = c.getBoolean("Disable.Interaction.Fishing", false);
		b[8] = c.getBoolean("Disable.Interaction.Shooting", false);
		b[9] = c.getBoolean("Disable.Interaction.Enchanting", false);
		b[10] = c.getBoolean("Disable.Interaction.Fireworks", false);
		b[11] = c.getBoolean("Disable.Interaction.Trading", false);
		b[12] = c.getBoolean("Disable.Other.Monster_Spawner_Exp", false);
		b[13] = c.getBoolean("Disable.Other.Monster_Spawner_Drops", false);
		b[14] = c.getBoolean("TimeManager.Enabled", false);
		b[15] = c.getBoolean("DeathManager.Player.Keep_Exp", false);
		b[16] = c.getBoolean("DeathManager.Entity.Drop_Exp", true);
		b[17] = c.getBoolean("DeathManager.Entity.Drop_Items", true);
		b[18] = c.getBoolean("DeathManager.Player.Keep_Items", false);
		b[19] = c.getBoolean("SpawnManager.Remove_Armor", false);
		l[0] = c.getLong("TimeManager.Target_Time", 12000L);
		l[1] = c.getLong("TimeManager.Set_Every", 100L);
		this.loadLists();
		this.log();
	}

	private void loadLists() {
		final EntityManager em = super.plugin;
		for (String s : c.getStringList("Disable.Usage.Blocked_Items")) {
			try {
				usageBlock.add(s.toUpperCase());
			} catch (Exception ex) {
				if (s.toLowerCase().startsWith("potion")) {
					String[] args = s.split(":");
					Integer id = Integer.parseInt(args[1]);
					Potion p = Utility.fromDamage(id);
					disPots.add(p.getNameId());
					if (!usageBlock.contains("POTION")) {
						usageBlock.add("POTION");
					}
				} else {
					em.getLogger().severe("Invalid value: " + s);
					em.getLogger().severe("Reference: http://goo.gl/f1Nmb");
				}
			}
		}
		for (String s : c.getStringList("Disable.Dispensing.Blocked_Items")) {
			try {
				disDBlock.add(s.toUpperCase());
			} catch (Exception ex) {
				if (s.toLowerCase().startsWith("potion")) {
					String[] args = s.split(":");
					Integer id = Integer.parseInt(args[1]);
					Potion p = Utility.fromDamage(id);
					disDpots.add(p.getNameId());
					if (!usageBlock.contains("POTION")) {
						usageBlock.add("POTION");
					}
				} else {
					em.getLogger().severe("Invalid value: " + s);
					em.getLogger().severe("Reference: http://goo.gl/f1Nmb");
				}
			}
		}
		for (String s : c.getStringList("EggManager.Disabled_Eggs")) {
			try {
				disEggs.put(s.toUpperCase(), Type.ALL);
			} catch (Exception ex) {
				em.getLogger().severe("Invalid value: " + s);
				em.getLogger().severe("Reference: http://goo.gl/E7mVB");
			}
		}
		for (String s : c.getStringList("SpawnManager.Disallowed_Mobs")) {
			try {
				if (s.indexOf(":") <= 0) {
					disMobs.put(s.toUpperCase(), Type.ALL);
				} else {
					String[] args = s.split(":");
					String one = args[1].toUpperCase();
					if (args.length == 2) {
						disMobs.put(args[0].toUpperCase(),valueOf(one));
					} else if (args.length == 3) {
						if (one.equalsIgnoreCase("baby")
								|| one.equalsIgnoreCase("villager")) {
							if (args[2].equalsIgnoreCase("baby")
									|| args[2].equalsIgnoreCase("villager")) {
								disMobs.put(args[0].toUpperCase(), Type.BOTH);
							}
						}
					}
				}
			} catch (Exception ex) {
				em.getLogger().severe("Invalid value: " + s);
				em.getLogger().severe("Reference: http://goo.gl/E7mVB");
			}
		}
		for (String s : c.getStringList("SpawnManager.Disallowed_Reasons")) {
			try {
				disReasons.add(s.toUpperCase());
			} catch (Exception ex) {
				em.getLogger().severe("Invalid value: " + s);
				em.getLogger().severe("Reference: http://goo.gl/a4XRB");
			}
		}
	}
	
	private void log(){
		if(b[2]) {
			plugin.getLogger().info("== The following mobs are blocked from spawning ==");
			for(Entry<String, Type> entry : disMobs.entrySet()) {
				plugin.getLogger().info("Mob: " + entry.getKey() + " Type: " + entry.getValue());
			}
			plugin.getLogger().info("== The following mob spawn reasons are blocked ==");
			for(String str : disReasons) {
				plugin.getLogger().info("SpawnReason: " + str);
			}
		}
	}

	public void performUpdate(String revision) {
		Logger lg = plugin.getLogger();
		if (this.local.equals(revision)) {
			return;
		}
		if (revision.equals("0.1")) {
			super.set("Settings.Config_Revision", this.local);
			super.set("Disable.Usage.Potions", false);
			super.set("Disable.Usage.Splash_Potions", false);
			super.set("Disable.Interaction.Trading", false);
			lg.info("Successfully updated " + fileName + " to 1.0");
		} else if (revision.equals("1.0")) {
			super.set("Settings.Config_Revision", this.local);
			List<String> list = new ArrayList<String>();
			list.add(SpawnReason.LIGHTNING.toString().toLowerCase());
			super.set("SpawnManager.Disallowed_Reasons", list);
			lg.info("Successfully updated " + fileName + " to 1.1");
		} else if (revision.equals("1.1")) {
			super.set("Settings.Config_Revision", this.local);
			super.set("DeathManager.Keep_Exp", false);
			lg.info("Successfully updated " + fileName + " to 1.2");
		} else if (revision.equals("1.2")) {
			super.set("Settings.Config_Revision", this.local);
			super.set("DeathManager.Keep_Exp", null);
			super.set("DeathManager.Player.Keep_Exp", false);
			super.set("DeathManager.Entity.Drop_Exp", true);
			super.set("DeathManager.Entity.Drop_Items", true);
			lg.info("Successfully updated " + fileName + " to 1.3");
		} else if (revision.equals("1.3")) {
			super.set("Settings.Config_Revision", this.local);
			super.set("Usage", null);
			List<String> list = c.getStringList("Usage.Blocked_Items");
			list.add("Potion:16394");
			super.set("Disable.Usage.Blocked_Items", list);
			lg.info("Successfully updated " + fileName + " to 1.4");
		} else if (revision.equals("1.4")) {
			super.set("Settings.Config_Revision", this.local);
			super.set("DeathManager.Player.Keep_Items", false);
			super.set("SpawnManager.Remove_Armor", false);
			List<String> list = c.getStringList("SpawnManager.Disallowed_Mobs");
			list.add("Zombie:baby:villager");
			super.set("SpawnManager.Disallowed_Mobs", list);
			lg.info("Successfully updated " + fileName + " to 1.5");
			return;
		} else if (revision.equals("1.5")) {
			return;
		} else {
			lg.warning("Could not update config, mismatched config revision: Local: "
					+ local + " File: " + revision);
		}
		this.reload();
	}

	public void reload() {
		this.disDBlock.clear();
		this.disEggs.clear();
		this.disMobs.clear();
		this.disReasons.clear();
		this.usageBlock.clear();
		this.disDpots.clear();
		this.disPots.clear();
		super.rereadFromDisk();
		super.load();
	}

	public String getWorld() {
		return this.world;
	}

	public Set<?> get(int i) {
		if (i == 1) {
			return this.disDBlock;
		} else if (i == 2) {
			return this.usageBlock;
		} else if (i == 5) {
			return this.disReasons;
		} else if (i == 6) {
			return this.disPots;
		} else if (i == 7) {
			return this.disDpots;
		}
		return null;
	}

	public Map<String, Type> getMap(int i) {
		if (i == 1) {
			return this.disEggs;
		}
		return this.disMobs;
	}

	public boolean get(Option op) {
		return b[op.getId()];
	}

	public long g(Option op) {
		return l[op.getId()];
	}
}
