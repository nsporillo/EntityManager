package net.milkycraft.em.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkycraft.em.EntityManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author krinsdeath
 */
public class CommandHandler {

	@SuppressWarnings("unused")
	private final EntityManager plugin;
	private final Map<String, Command> commands = new HashMap<String, Command>();

	public CommandHandler(final EntityManager plugin) {
		this.plugin = plugin;
		commands.put("reload", new ReloadCommand(plugin));
	}

	public void runCommand(final CommandSender sender, final String label,
			final String[] args) {
		sender.sendMessage(label);
		if (this.commands.get(args[0].toLowerCase()) == null) {
			sender.sendMessage(ChatColor.GREEN + "===" + ChatColor.GOLD
					+ " EntityManager Help " + ChatColor.GREEN + "===");
			for (final Command cmd : this.commands.values()) {
				if (cmd.checkPermission(sender)) {
					cmd.showHelp(sender, label);
				}
			}
			return;
		}
		final List<String> arguments = new ArrayList<String>(
				Arrays.asList(args));
		final Command cmd = this.commands
				.get(arguments.remove(0).toLowerCase());
		if (arguments.size() < cmd.getRequiredArgs()) {
			cmd.showHelp(sender, label);
			return;
		}
		cmd.runCommand(sender, arguments);
	}

}