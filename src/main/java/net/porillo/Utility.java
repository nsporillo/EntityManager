package net.porillo;

import net.porillo.config.WorldConfiguration;
import net.porillo.types.Option;
import net.porillo.types.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import static org.bukkit.ChatColor.*;

public class Utility {

    private EntityManager manager;

    public Utility(EntityManager manager) {
        this.manager = manager;
    }

    private void alertAdmins(String message, String world) {
        String msg = "&2[&4Alert&2] [&6" + world + "&2] &c" + message;
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.hasPermission(Permission.aDMINA))
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, manager);
    }

    public boolean alertPlayerNoPvp(WorldConfiguration conf, Player p, String name) {
        if (conf.get(Option.PVP) && !p.hasPermission(Permission.iNTPVP)) {
            alertAdminsAndLog(conf, "Player " + p.getName() + " tried to attack " + name);
            alertPlayer(conf, p, "&cYou don't have permission to pvp.");
            return true;
        }
        return false;
    }

    public WorldConfiguration getConfigByEntity(Entity e) {
        return this.getConfig(e.getWorld());
    }

    public WorldConfiguration getConfig(World w) {
        return this.get(w.getName());
    }

    public WorldConfiguration get(String world) {
        try {
            return manager.getWorld(world);
        } catch (NullPointerException ex) {
            manager.getLogger().warning("No config found for " + world + ", generating one now");
            return manager.loadWorldConfigurations(world);
        }
    }

    public boolean hasPermission(Player p, String perm) {
        return p.hasPermission(perm);
    }

    public void alertAdminsAndLog(WorldConfiguration conf, String adminMsg) {
        if (conf.get(Option.ADMIN_ALERTS))
            alertAdmins(adminMsg, conf.getWorld());
        if (conf.get(Option.LOGGING))
            manager.getLogger().info(adminMsg);
    }

    public void alertPlayer(WorldConfiguration conf, Player player, String message) {
        if (conf.get(Option.PLAYER_ALERTS))
            player.sendMessage(translateAlternateColorCodes('&', message));
    }

    public void alertPlayerNoPvp(WorldConfiguration conf, Player p, EntityType type, String iType) {
        String item = type.name().toLowerCase().replace("_", "");
        item = item.replace("thrown_", "").replace("splash_", "");

        if (iType != null) {
            item = iType;
        }

        alertAdminsAndLog(conf, "Player " + p.getName() + " tried to use a " + item + ".");

        if (item.endsWith("y")) {
            alertPlayer(conf, p, "&cYou don't have permission to throw &6" + item.concat("."));
        } else {
            alertPlayer(conf, p, "&cYou don't have permission to throw &6" + item.concat("s."));
        }

    }

    public void warnPotion(WorldConfiguration conf, Player p, ItemStack itemStack) {
        PotionMeta meta = (PotionMeta)itemStack.getItemMeta();
        String item = itemStack.getType().name().replace("_", " ").toLowerCase();
        item += " of " + meta.getBasePotionData().getType().getEffectType().getName().toLowerCase();

        if (meta.getBasePotionData().isUpgraded()) {
            item += " II";
        }
        if (meta.getBasePotionData().isExtended()) {
            item += " (extended)";
        }

        alertAdminsAndLog(conf, "Player " + p.getName() + " tried to use a " + item + ".");

        if (item.endsWith("y")) {
            alertPlayer(conf, p, "&cYou don't have permission to use " + item.concat("."));
        } else {
            alertPlayer(conf, p, "&cYou don't have permission to use " + item.concat("s."));
        }
    }

    public EntityManager getHandle() {
        return this.manager;
    }
}
