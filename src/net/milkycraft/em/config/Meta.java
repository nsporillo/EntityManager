package net.milkycraft.em.config;

public class Meta {

	private Type type;
	private byte color;

	public Meta(Type type, byte color) {
		this.type = type;
		this.color = color;
	}

	public Type getType() {
		return this.type;
	}

	public byte getColor() {
		return this.color;
	}
}
