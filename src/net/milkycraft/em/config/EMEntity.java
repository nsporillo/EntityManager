package net.milkycraft.em.config;

import org.bukkit.entity.EntityType;

public class EMEntity {
	
	private Meta meta;
	private String type;
	
	public EMEntity(String type, Meta meta) {
		this.type = type;
		this.meta = meta;
	}
	
	public EMEntity(String type, Type t, byte color) {
		this(type, new Meta(t, color));
	}
	
	public EntityType getType() {
		return EntityType.valueOf(type);
	}
	
	public String getName() {
		return type;
	}
	
	public Type getBreed() {
		return this.meta.getType();
	}
	
	public byte getColor() {
		return this.meta.getColor();
	}
}

