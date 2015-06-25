package net.porillo.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;

import java.util.List;

import net.porillo.EntityManager;
import net.porillo.config.WorldConfiguration;
import net.porillo.types.Permission;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {
	public ReloadCommand(EntityManager plugin) {
		super(plugin);
		super.setName("reload");
		super.addUsage("Reloads config (all if blank)", "[world]");
		super.setPermission(Permission.aDMINR);
	}

	@Override
	public void runCommand(CommandSender s, List<String> args) {
		if (!this.checkPermission(s)) {
			this.noPermission(s);
			return;
		}
		if (args.size() == 0) {
			for (WorldConfiguration c : plugin.getWorlds())
				reloadWorld(s, c);
			s.sendMessage(BLUE + "Reloaded all world configs!");
		} else
			for (WorldConfiguration c : plugin.getWorlds())
				if (c.getWorld().equalsIgnoreCase(args.get(0)))
					reloadWorld(s, c);
	}

	private void reloadWorld(CommandSender s, WorldConfiguration c) {
		c.reload();
		s.sendMessage(GREEN + "Configuration for " + c.getWorld() + " has been reloaded!");
	}
}
