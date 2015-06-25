package net.porillo.commands;

import net.porillo.EntityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

/**
 * @author krinsdeath
 */
public abstract class BaseCommand implements Command {

    protected EntityManager plugin;
    protected String name;
    protected String permission;
    protected int required = 0;
    protected List<String> usages = new ArrayList<String>();

    public BaseCommand(final EntityManager plugin) {
        this.plugin = plugin;
    }

    public final void addUsage(String desc, String... uses) {
        final StringBuilder usage = new StringBuilder().append(BLUE).append(String.format("%1$-" + 8 + "s", this.name));
        boolean color = true;
        for (String use : uses) {
            if (color)
                usage.append(YELLOW);
            else
                usage.append(AQUA);
            color = !color;
            usage.append(String.format("%1$-" + 8 + "s", use));
        }
        usage.append(GREEN);
        usage.append(desc);
        this.usages.add(usage.toString());
    }

    @Override
    public boolean checkPermission(final CommandSender sender) {
        return sender.hasPermission(this.permission);
    }

    @Override
    public int getRequiredArgs() {
        return this.required;
    }

    protected final void setRequiredArgs(final int req) {
        this.required = req;
    }

    protected void noPermission(final CommandSender sender) {
        sender.sendMessage(RED + "You do not have permission to use that command!");
    }

    protected final void setName(final String name) {
        this.name = name;
    }

    protected final void setPermission(final String perm) {
        this.permission = perm;
    }

    @Override
    public void showHelp(final CommandSender sender, final String label) {
        for (final String usage : this.usages) {
            sender.sendMessage(GRAY + String.format("%1$-" + 10 + "s", label) + ChatColor.translateAlternateColorCodes('&', usage));
        }
    }
}
