package net.porillo.commands;

import net.porillo.EntityManager;
import net.porillo.LogFile;
import net.porillo.types.Permission;
import org.bukkit.command.CommandSender;

import java.util.List;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;

public class DumpCommand extends BaseCommand {
    public DumpCommand(EntityManager plugin) {
        super(plugin);
        super.setName("dump");
        super.addUsage("Generates memory dump file");
        super.setPermission(Permission.aDMIND);
    }

    @Override
    public void runCommand(CommandSender s, List<String> args) {
        if (!this.checkPermission(s)) {
            this.noPermission(s);
            return;
        }
        int lines = LogFile.newDump(super.plugin);
        s.sendMessage(BLUE + "Generated log file with " + GREEN + lines + " lines");
    }
}
