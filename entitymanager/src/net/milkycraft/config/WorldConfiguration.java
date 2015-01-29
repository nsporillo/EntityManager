package net.milkycraft.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.milkycraft.EntityManager;
import net.milkycraft.types.Item;
import net.milkycraft.types.Potion;
import net.milkycraft.types.Option;
import net.milkycraft.types.Spawnable;
import net.milkycraft.types.Type;

import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class WorldConfiguration extends ConfigLoader {

	private final String world;
	private final double REV = 1.7;
	private boolean[] b = new boolean[24];
	private long[] l = new long[2];

	protected Set<Item> usageBlock = new HashSet<Item>();
	protected Set<Item> dispBlock = new HashSet<Item>();
	protected Set<Short> disEggs = new HashSet<Short>();
	protected Set<Spawnable> disMobs = new HashSet<Spawnable>();
	protected Set<String> disReasons = new HashSet<String>();
	protected Set<Item> blockedBlocks = new HashSet<Item>();
	protected Set<Potion> ampedPots = new HashSet<Potion>();

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

			load(0, "Settings.Admin-Alerts", true);
			load(1, "Settings.Player-Alerts", true);
			load(2, "Settings.Logging", true);
			load(3, "Disable.Weather.Rain", false);
			load(4, "Disable.Weather.Thunder", false);
			load(5, "Disable.Weather.Lightning", false);
			load(6, "Disable.Interaction.PVP", false);
			load(7, "Disable.Interaction.Fishing", false);
			load(8, "Disable.Interaction.Shooting", false);
			load(9, "Disable.Interaction.Enchanting", false);
			load(10, "Disable.Interaction.Fireworks", false);
			load(11, "Disable.Interaction.Trading", false);
			load(12, "Disable.Other.Monster_Spawner_Exp", false);
			load(13, "Disable.Other.Monster_Spawner_Drops", false);
			load(14, "PotionManager.Enabled", false);
			load(15, "TimeManager.Enabled", false);
			load(16, "EggManager.Disable_All", false);
			load(17, "SpawnManager.Disallow_All", false);
			load(18, "SpawnManager.Remove_Armor", false);
			load(19, "DeathManager.Player.Keep_Exp", false);
			load(20, "DeathManager.Entity.Drop_Exp", false);
			load(21, "DeathManager.Entity.Drop_Items", true);
			load(22, "DeathManager.Player.Keep_Items", false);
			load(23, "Disable.Interaction.Portal_Creation", false);
			ConfigUtility.load(this);
		} else {
			super.saveConfig();
			super.rereadFromDisk();
			super.load();
		}
	}

	private void load(int i, String sec, boolean def) {
		b[i] = c.getBoolean(sec, def);
	}

	public Logger getLog() {
		return plugin.getLogger();
	}

	private boolean performUpdate(double rev) {
		if (this.REV == rev)
			return true;

		if (rev == 0.1) {
			super.set("Settings.Config_Revision", 1.0);
			super.set("Disable.Usage.Potions", false);
			super.set("Disable.Usage.Splash_Potions", false);
			super.set("Disable.Interaction.Trading", false);
			return false;
		} else if (rev == 1.0) {
			super.set("Settings.Config_Revision", 1.1);
			List<String> list = new ArrayList<String>();
			list.add(SpawnReason.LIGHTNING.toString().toLowerCase());
			super.set("SpawnManager.Disallowed_Reasons", list);
			return false;
		} else if (rev == 1.1) {
			super.set("Settings.Config_Revision", 1.2);
			super.set("DeathManager.Keep_Exp", false);
			return false;
		} else if (rev == 1.2) {
			super.set("Settings.Config_Revision", 1.3);
			super.set("DeathManager.Keep_Exp", null);
			super.set("DeathManager.Player.Keep_Exp", false);
			super.set("DeathManager.Entity.Drop_Exp", true);
			super.set("DeathManager.Entity.Drop_Items", true);
			return false;
		} else if (rev == 1.3) {
			super.set("Settings.Config_Revision", 1.4);
			super.set("Usage", null);
			List<String> list = c.getStringList("Usage.Blocked_Items");
			list.add("Potion:16394");
			super.set("Disable.Usage.Blocked_Items", list);
			return false;
		} else if (rev == 1.4) {
			super.set("Settings.Config_Revision", 1.5);
			super.set("DeathManager.Player.Keep_Items", false);
			super.set("SpawnManager.Remove_Armor", false);
			List<String> list = c.getStringList("SpawnManager.Disallowed_Mobs");
			list.add("Zombie:baby:villager");
			super.set("SpawnManager.Disallowed_Mobs", list);
			return false;
		} else if (rev == 1.5) {
			super.set("Settings.Config_Revision", 1.6);
			super.set("Disable.Interaction.Portal_Creation", false);
			return false;
		} else if (rev == 1.6) {
			super.set("Settings.Config_Revision", 1.7);
			super.set("EggManager.Disable_All", false);
			super.set("SpawnManager.Disallow_All", false);
			return false;
		} else if (rev == 1.7) {
			super.set("Settings.Config_Revision", 1.8);
			super.set("PotionManager.Enabled", false);
			List<String> list = new ArrayList<String>();
			list.add("Potion:16426");
			super.set("PotionManager.DisableThrowing", list);
			super.set("PotionManager.DisableDispensing", list);
			list.clear();
			list.add("Potion:16418:3");
			super.set("PotionManager.IntensityModifier", list);
			plugin.getLogger().info("Successfully updated " + fileName + " to 1.8");
			return false;
		}else {
			plugin.getLogger().warning("Config update failed, check version (" + rev + ")");
			return true;
		}
	}

	@Override
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

	public boolean has(EntityType type) {
		for (Spawnable eme : this.disMobs)
			if (eme.getType() == type)
				return true;
		return false;
	}

	public boolean usage(int id) {
		for (Item i : this.usageBlock)
			if (i.getId() == id)
				return true;
		return false;
	}

	public boolean usagePotion(int pot) {
		for (Item i : this.usageBlock)
			if (i.getId() == 373)
				if (i.getDurability() == pot)
					return true;
		return false;
	}

	public int getMultiplier(int pot) {
		int mult = 1;
		for (Potion i : this.ampedPots)
			return i.getMultiplier();
		return mult;
	}

	public boolean dispensePotion(int pot) {
		for (Item i : this.dispBlock)
			if (i.getId() == 373)
				if (i.getDurability() == pot)
					return true;

		return false;
	}

	public boolean dispense(int id) {
		for (Item i : this.dispBlock)
			if (i.getId() == id)
				return true;

		return false;
	}

	public boolean block(EntityType etype, Type type) {
		for (Spawnable eme : this.disMobs)
			if (eme.getType() == etype)
				if (eme.getBreed() == type)
					return true;

		return false;
	}

	public boolean block(EntityType etype, Type type, Color color) {
		for (Spawnable eme : this.disMobs)
			if (eme.getType() == etype)
				if (eme.getBreed() == type)
					if (eme.getColor() == null || eme.getColor() == color)
						return true;

		return false;
	}

	public boolean block(EntityType etype, Color color) {
		for (Spawnable eme : this.disMobs)
			if (eme.getType() == etype)
				if (eme.getColor() == null || eme.getColor() == color)
					return true;

		return false;
	}

	public boolean get(Option op) {
		return b[op.getId()];
	}

	public long g(Option op) {
		return l[op.getId()];
	}
}
