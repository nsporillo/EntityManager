package net.milkycraft.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.milkycraft.EntityManager;
import net.milkycraft.objects.Item;
import net.milkycraft.objects.Option;
import net.milkycraft.objects.Spawnable;
import net.milkycraft.objects.Type;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

public class WorldConfiguration extends ConfigLoader {

	private final String world;
	private final double version = 1.6;
	private boolean[] b = new boolean[21];
	private long[] l = new long[2];
	protected Set<Item> usageBlock = new HashSet<Item>();
	protected Set<Item> dispBlock = new HashSet<Item>();
	protected Set<Short> disEggs = new HashSet<Short>();
	protected Set<Spawnable> disMobs = new HashSet<Spawnable>();
	protected Set<String> disReasons = new HashSet<String>();

	public WorldConfiguration(EntityManager plugin, String world) {
		super(plugin, world + ".yml");
		super.saveIfNotExist();
		this.world = world;
		super.load();
	}

	@Override
	protected void loadKeys() {
		if (performUpdate(c.getDouble("Settings.Config_Revision", 0.1))) {
			l[0] = c.getLong("TimeManager.Target_Time", 12000L);
			l[1] = c.getLong("TimeManager.Set_Every", 100L);
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
			b[20] = c.getBoolean("Disable.Interaction.Portal_Creation", false);
			ConfigUtility.loadBlockedItems(this);
			ConfigUtility.loadBlockedDispenserItems(this);
			ConfigUtility.loadBlockedSpawnEggs(this);
			ConfigUtility.loadBlockedEntities(this);
			ConfigUtility.loadBlockedSpawnReasons(this);
		} else {
			super.saveConfig();
			super.rereadFromDisk();
			super.load();
		}
	}

	public boolean performUpdate(double rev) {
		Logger lg = plugin.getLogger();
		if (this.version == rev) {
			return true;
		}
		if (rev == 0.1) {
			super.set("Settings.Config_Revision", 1.0);
			super.set("Disable.Usage.Potions", false);
			super.set("Disable.Usage.Splash_Potions", false);
			super.set("Disable.Interaction.Trading", false);
			lg.info("Successfully updated " + fileName + " to 1.0");
			return false;
		} else if (rev == 1.0) {
			super.set("Settings.Config_Revision", 1.1);
			List<String> list = new ArrayList<String>();
			list.add(SpawnReason.LIGHTNING.toString().toLowerCase());
			super.set("SpawnManager.Disallowed_Reasons", list);
			lg.info("Successfully updated " + fileName + " to 1.1");
			return false;
		} else if (rev == 1.1) {
			super.set("Settings.Config_Revision", 1.2);
			super.set("DeathManager.Keep_Exp", false);
			lg.info("Successfully updated " + fileName + " to 1.2");
			return false;
		} else if (rev == 1.2) {
			super.set("Settings.Config_Revision", 1.3);
			super.set("DeathManager.Keep_Exp", null);
			super.set("DeathManager.Player.Keep_Exp", false);
			super.set("DeathManager.Entity.Drop_Exp", true);
			super.set("DeathManager.Entity.Drop_Items", true);
			lg.info("Successfully updated " + fileName + " to 1.3");
			return false;
		} else if (rev == 1.3) {
			super.set("Settings.Config_Revision", 1.4);
			super.set("Usage", null);
			List<String> list = c.getStringList("Usage.Blocked_Items");
			list.add("Potion:16394");
			super.set("Disable.Usage.Blocked_Items", list);
			lg.info("Successfully updated " + fileName + " to 1.4");
			return false;
		} else if (rev == 1.4) {
			super.set("Settings.Config_Revision", 1.5);
			super.set("DeathManager.Player.Keep_Items", false);
			super.set("SpawnManager.Remove_Armor", false);
			List<String> list = c.getStringList("SpawnManager.Disallowed_Mobs");
			list.add("Zombie:baby:villager");
			super.set("SpawnManager.Disallowed_Mobs", list);
			lg.info("Successfully updated " + fileName + " to 1.5");
			return false;
		} else if (rev == 1.5) {
			super.set("Settings.Config_Revision", 1.6);
			super.set("Disable.Interaction.Portal_Creation", false);
			lg.info("Successfully updated " + fileName + " to 1.6");
			return false;
		} else {
			lg.warning("Could not update config, mismatched config revision: Local: "
					+ version + " File: " + rev);
			return false;
		}
	}

	public void reload() {
		this.dispBlock.clear();
		this.disEggs.clear();
		this.disMobs.clear();
		this.disReasons.clear();
		this.usageBlock.clear();
		super.rereadFromDisk();
		super.load();
	}

	public String getWorld() {
		return this.world;
	}

	public Set<Short> getSet3() {
		return this.disEggs;
	}

	public Set<String> getSet5() {
		return this.disReasons;
	}

	public boolean has(short id) {
		for (Spawnable eme : this.disMobs) {
			if (eme.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean usage(int id) {
		for (Item i : this.usageBlock) {
			if (i.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean usagePotion(int pot) {
		for (Item i : this.usageBlock) {
			if (i.getId() == 373) {
				if (i.getDurability() == pot) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean dispense(int id) {
		for (Item i : this.dispBlock) {
			if (i.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean dispensePotion(int pot) {
		for (Item i : this.dispBlock) {
			if (i.getId() == 373) {
				if (i.getDurability() == pot) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean block(short id, Type type) {
		for (Spawnable eme : this.disMobs) {
			if (eme.getId() == id) {
				if (eme.getBreed() == type) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean block(short id, Type type, byte color) {
		for (Spawnable eme : this.disMobs) {
			if (eme.getId() == id) {
				if (eme.getBreed() == type) {
					if (eme.getColor() == color) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean block(short id, byte color) {
		for (Spawnable eme : this.disMobs) {
			if (eme.getId() == id) {
				if (eme.getColor() == color) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean get(Option op) {
		return b[op.getId()];
	}

	public long g(Option op) {
		return l[op.getId()];
	}
}
