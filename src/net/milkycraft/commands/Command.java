package net.milkycraft.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * @author krinsdeath
 */
public interface Command {

	/**
	 * Checks whether the specified sender has access to this command.
	 * 
	 * @param sender
	 *            The originator of the command
	 * @return true if the sender is allowed to use this command, otherwise
	 *         false
	 */
	public boolean checkPermission(CommandSender sender);

	/**
	 * Checks how many arguments are required by the command.
	 * 
	 * @return the number of arguments required to use the command
	 */
	public int getRequiredArgs();

	/**
	 * Attempts to run the specified command with the given arguments.
	 * 
	 * @param sender
	 *            The originator of the command
	 * @param args
	 *            The arguments passed to the command
	 */
	public void runCommand(CommandSender sender, List<String> args);

	/**
	 * Shows a help message related to this command
	 * 
	 * @param sender
	 *            The originator of the command
	 * @param label
	 *            The original base command
	 */
	public void showHelp(CommandSender sender, String label);
}