package net.porillo.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author krinsdeath
 */
interface Command {

    boolean checkPermission(CommandSender sender);

    int getRequiredArgs();

    void runCommand(CommandSender sender, List<String> args);

    void showHelp(CommandSender sender, String label);
}