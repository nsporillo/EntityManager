package net.milkycraft.types;

public class Potion extends Item {

	private double mult;
	
	public Potion(int id, int dura, double mult) {
		super(id, dura);
		this.mult = mult;
	}

	public double getMultiplier() {
		return mult;
	}
	
	@Override
	public String toString() {
		return "Potion [mult=" + mult + ", getId()=" + getId() + ", getDurability()="
				+ getDurability() + "]";
	}

}
