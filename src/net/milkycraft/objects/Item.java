package net.milkycraft.objects;

import org.bukkit.Material;

public class Item {

	private int id;
	private int potid;
	private boolean isPot;

	public Item(int id, int potid, boolean isPot) {
		this.id = id;
		this.potid = potid;
		this.isPot = isPot;
	}

	public Item(int id) {
		this(id, Integer.MAX_VALUE, false);
	}

	public boolean isPotion() {
		return isPot;
	}

	public int getId() {
		return id;
	}

	public int getNameId() {
		return potid;
	}

	public String getName() {
		return Material.getMaterial(id).toString();
	}
}
