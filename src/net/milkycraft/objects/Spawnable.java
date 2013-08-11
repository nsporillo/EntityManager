package net.milkycraft.objects;

import org.bukkit.entity.EntityType;

public class Spawnable {

	private Meta meta;
	private short id;

	public Spawnable(short id, Meta meta) {
		this.id = id;
		this.meta = meta;
	}

	public Spawnable(short id, Type t, byte color) {
		this(id, new Meta(t, color));
	}

	public EntityType getType() {
		return EntityType.fromId(id);
	}

	public String getName() {
		return this.getType().toString();
	}

	public short getId() {
		return id;
	}

	public Type getBreed() {
		return this.meta.getType();
	}

	public byte getColor() {
		return this.meta.getColor();
	}
}
