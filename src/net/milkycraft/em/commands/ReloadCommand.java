package net.milkycraft.em.commands;

import static org.bukkit.ChatColor.*;

import java.util.List;

import net.milkycraft.em.EntityManager;
import net.milkycraft.em.config.WorldConfiguration;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

	public ReloadCommand(EntityManager plugin) {
		super(plugin);
		super.setName("reload");
		super.addUsage(null, null, "Reloads the world configurations");
		super.setPermission("entitymanager.reload");
	}

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
		if (!this.checkPermission(sender)) {
			this.noPermission(sender);
			return;
		}
		if (args.size() == 0) {
			for (WorldConfiguration conf : plugin.getWorlds()) {
				sender.sendMessage(GREEN + "Configuration for " + conf.getWorld() + " reloaded!");
				conf.reload();
			}
			sender.sendMessage(BLUE + "Reloaded all world configs!");
		} else {
			for (WorldConfiguration conf : plugin.getWorlds()) {
				if(conf.getWorld().equalsIgnoreCase(args.get(0))) {					
					sender.sendMessage(GREEN + "Configuration for " + conf.getWorld() + " reloaded!");
					conf.reload();
				}
			}
		}
	}
}
