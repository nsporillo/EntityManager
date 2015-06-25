package net.porillo.types;

public class Item {

	private int id;
	private int dura;

	public Item(int id, int dura) {
		this.id = id;
		this.dura = dura;
	}

	public Item(int id) {
		this(id, 0);
	}

	public int getId() {
		return id;
	}

	public int getDurability() {
		return dura;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", dura=" + dura + "]";
	}
}
