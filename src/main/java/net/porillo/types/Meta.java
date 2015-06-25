package net.porillo.types;

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
        return this.color;
    }

    @Override
    public String toString() {
        return "Meta [type=" + type + ", color=" + color + "]";
    }
}
