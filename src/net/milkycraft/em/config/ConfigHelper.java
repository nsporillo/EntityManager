package net.milkycraft.em.config;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ConfigHelper {

	public static Potion fromDamage(int damage) {
		PotionType type = PotionType.getByDamageValue(damage & 0xF);
		Potion potion;
		if (type == null || (type == PotionType.WATER && damage != 0)) {
			potion = new Potion(damage & 0x3F);
		} else {
			int level = (damage & 0x20) >> 5;
			level++;
			potion = new Potion(type, level);
		}
		if ((damage & 0x4000) > 0) {
			potion = potion.splash();
		}
		if ((damage & 0x40) > 0) {
			potion = potion.extend();
		}
		return potion;
	}
}
