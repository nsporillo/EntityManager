package net.milkycraft.em.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.List;

import net.milkycraft.em.EntityManager;

import org.bukkit.command.CommandSender;

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

	protected final void addUsage(final String sub1, final String sub2,
			final String description) {
		final StringBuilder usage = new StringBuilder().append(BLUE).append(
				String.format("%1$-" + 8 + "s", this.name));
		if (sub1 != null) {
			usage.append(YELLOW);
			usage.append(String.format("%1$-" + 8 + "s", sub1));
		} else {
			usage.append(String.format("%1$-" + 8 + "s", ""));
		}
		if (sub2 != null) {
			usage.append(AQUA);
			usage.append(String.format("%1$-" + 8 + "s", sub2));
		} else {
			usage.append(String.format("%1$-" + 8 + "s", ""));
		}
		usage.append(GREEN);
		usage.append(description);
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

	protected void noPermission(final CommandSender sender) {
		sender.sendMessage(RED
				+ "You do not have permission to use that command!");
	}

	protected final void setName(final String name) {
		this.name = name;
	}

	protected final void setPermission(final String perm) {
		this.permission = perm;
	}

	protected final void setRequiredArgs(final int req) {
		this.required = req;
	}

	@Override
	public void showHelp(final CommandSender sender, final String label) {
		for (final String usage : this.usages) {
			sender.sendMessage(GRAY + String.format("%1$-" + 10 + "s", label)
					+ usage);
		}
	}
}
