package net.porillo.types;

import org.bukkit.Material;

public class Item {

    private Material type;
    private int dura;

    public Item(Material type, int dura) {
        this.type = type;
        this.dura = dura;
    }

    public Item(Material type) {
        this(type, 0);
    }

    public Item(){}

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getDurability() {
        return dura;
    }

    @Override
    public String toString() {
        return "Item [type=" + type.name() + ", dura=" + dura + "]";
    }
}
