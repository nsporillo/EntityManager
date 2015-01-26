package net.milkycraft.types;

import org.bukkit.Color;
import org.bukkit.entity.EntityType;

public class Spawnable {

	private Meta meta;
	private EntityType type;

	public Spawnable(EntityType type, Type t, Color color) {
		this(type, new Meta(t, color));
	}

	public Spawnable(EntityType type, Type t) {
		this(type, new Meta(t));
	}

	public Spawnable(EntityType type, Meta meta) {
		this.type = type;
		this.meta = meta;
	}

	public EntityType getType() {
		return type;
	}

	public String getName() {
		return this.getType().toString();
	}

	public Type getBreed() {
		return this.meta.getType();
	}

	public Color getColor() {
		return this.meta.getColor();
	}
}
