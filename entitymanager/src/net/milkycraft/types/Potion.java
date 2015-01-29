package net.milkycraft.types;

public class Potion extends Item {

	private int mult;
	
	public Potion(int id, int dura, int mult) {
		super(id, dura);
		this.mult = mult;
	}

	public int getMultiplier() {
		return mult;
	}
	
	@Override
	public String toString() {
		return "Potion [mult=" + mult + ", getId()=" + getId() + ", getDurability()="
				+ getDurability() + "]";
	}

}
