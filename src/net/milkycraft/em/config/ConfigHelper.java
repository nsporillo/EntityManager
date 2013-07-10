package net.milkycraft.em.config;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ConfigHelper {

	private static final int EXTENDED_BIT = 0x40;
	private static final int POTION_BIT = 0xF;
	private static final int SPLASH_BIT = 0x4000;
	private static final int TIER_BIT = 0x20;
	private static final int TIER_SHIFT = 5;
	private static final int NAME_BIT = 0x3F;

	public static Potion fromDamage(int damage) {
		PotionType type = PotionType.getByDamageValue(damage & POTION_BIT);
		Potion potion;
		if (type == null || (type == PotionType.WATER && damage != 0)) {
			potion = new Potion(damage & NAME_BIT);
		} else {
			int level = (damage & TIER_BIT) >> TIER_SHIFT;
			level++;
			potion = new Potion(type, level);
		}
		if ((damage & SPLASH_BIT) > 0) {
			potion = potion.splash();
		}
		if ((damage & EXTENDED_BIT) > 0) {
			potion = potion.extend();
		}
		return potion;	
	}
}
