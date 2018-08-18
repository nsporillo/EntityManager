package net.porillo.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;

public class Potion extends Item {

	private PotionEffectType effect;
	private int tier;
	private int extended;
	private double mult;

	/**
	 * Potion String format
	 * Potion:Heal:2:extended
	 * potionType:potionEffect:tier:extended
	 *
	 * @param potionString input string
	 */
	public Potion(String potionString) throws IllegalArgumentException {
		String[] potData = potionString.split(":");
		super.setType(Material.valueOf(potData[0].toUpperCase().trim()));
		effect = PotionEffectType.getByName(potData[1].toUpperCase().trim());

		if (potData.length > 2) {
			if (potData[2].equals("*")) {
				tier = -1;
			}

			try {
				tier = Integer.parseInt(potData[2].trim());
			} catch (NumberFormatException ex) {
				if (potData[2].toLowerCase().trim().equals("extended")) {
					extended = 1;
				} else if (potData[2].equals("*")) {
					extended = -1;
				}

				if (extended == 0) {
					try {
						mult = Double.parseDouble(potData[2].trim());
					} catch (NumberFormatException ex2) {
					}
				}
			}
		}

		if (potData.length > 3) {
			if (potData[3].toLowerCase().trim().equals("extended")) {
				extended = 1;
			} else if (potData[3].equals("*")) {
				extended = -1;
			}

			if (extended == 0) {
				try {
					mult = Double.parseDouble(potData[3].trim());
				} catch (NumberFormatException ex2) {
				}
			}
		}

		if (potData.length > 4) {
			mult = Double.parseDouble(potData[4].trim());
		}
	}

	public double getMultiplier() {
		return mult;
	}

	public static String getPotionPermission(ItemStack is) {
		String item = is.getType().name().toLowerCase();
		PotionMeta potionMeta = (PotionMeta) is.getItemMeta();

		item += "." + potionMeta.getBasePotionData().getType().getEffectType().getName().toLowerCase();

		if (potionMeta.getBasePotionData().isUpgraded()) {
			item += ".2";
		}

		if (potionMeta.getBasePotionData().isExtended()) {
			item += ".extended";
		}

		return item;
	}

	public boolean equalsStack(ItemStack is) {
		if (is.getItemMeta() instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) is.getItemMeta();
			PotionData potionData = potionMeta.getBasePotionData();
			PotionEffectType potionEffectType = potionData.getType().getEffectType();

			if (tier == -1 || (tier == 2 && potionData.isUpgraded() || (tier == 1 || tier == 0) && !potionData.isUpgraded())) {
				if (extended == -1 || (extended == 1 && potionData.isExtended() || extended == 0 && !potionData.isExtended())) {
					return effect.getName().equals(potionEffectType.getName());
				}
			}
		}

		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Potion potion = (Potion) o;

		if (tier != potion.tier) return false;
		if (extended != potion.extended) return false;
		if (Double.compare(potion.mult, mult) != 0) return false;
		return effect.equals(potion.effect);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = effect.hashCode();
		result = 31 * result + tier;
		result = 31 * result + extended;
		temp = Double.doubleToLongBits(mult);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		String item = super.getType().name().replaceAll("_", " ").toLowerCase();
		item += " of " + effect.getName().replace("_", " ").toLowerCase();

		if (tier == 2) {
			item += " II";
		}

		if (extended == 1) {
			item += " (extended)";
		}

		return item;
	}
}
