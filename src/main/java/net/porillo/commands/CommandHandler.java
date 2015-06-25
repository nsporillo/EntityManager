package net.porillo.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.porillo.EntityManager;

import org.bukkit.command.CommandSender;

/**
 * @author krinsdeath
 */
public class CommandHandler {

	private Map<String, Command> cmds = new HashMap<String, Command>();

	public CommandHandler(EntityManager plugin) {
		cmds.put("reload", new ReloadCommand(plugin));
		cmds.put("dump", new DumpCommand(plugin));
	}

	public void runCommand(CommandSender s, String l, String[] a) {
		if (a.length == 0 || this.cmds.get(a[0].toLowerCase()) == null) {
			this.showHelp(s, l);
			return;
		}
		List<String> args = new ArrayList<String>(Arrays.asList(a));
		Command cmd = this.cmds.get(args.remove(0).toLowerCase());
		if (args.size() < cmd.getRequiredArgs()) {
			cmd.showHelp(s, l);
			return;
		}
		cmd.runCommand(s, args);
	}

	private void showHelp(CommandSender s, String l) {
		s.sendMessage(GREEN + "===" + GOLD + " EntityManager Help " + GREEN + "===");
		for (Command cmd : this.cmds.values()) {
			if (cmd.checkPermission(s)) {
				cmd.showHelp(s, l);
			}
		}
	}
}