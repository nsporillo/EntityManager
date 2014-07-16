package net.milkycraft.objects;

import org.bukkit.Color;

public class Meta {

	private Type type;
	private Color color;

	public Meta(Type type, Color color) {
		this.type = type;
		this.color = color;
	}

	public Meta(Type type) {
		this(type, null);
	}

	public Type getType() {
		return this.type;
	}

	public Color getColor() {
		if (this.color == null) {
			return null;
		}
		return this.color;
	}
}
