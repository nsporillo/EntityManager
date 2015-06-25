package net.porillo;

import java.util.ArrayList;
import java.util.List;

import net.porillo.commands.CommandHandler;
import net.porillo.config.WorldConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityManager extends JavaPlugin {

	private CommandHandler handler = new CommandHandler(this);
	private List<WorldConfiguration> configs;

	@Override
	public void onEnable() {
		Bukkit.getScheduler().runTask(this, new Runnable() {

			@Override
			public void run() {
				EntityManager.this.load();
				new TimeManager(EntityManager.this);
				getLogger().info(configs.size() + " worlds loaded");
				new EventListener(EntityManager.this);
			}

		});
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		handler.runCommand(s, l, a);
		return true;
	}

	public void load() {
		configs = new ArrayList<WorldConfiguration>();
		for (World w : Bukkit.getWorlds())
			load(w.getName());
	}

	public WorldConfiguration load(String w) {
		WorldConfiguration wc = new WorldConfiguration(this, w);
		configs.add(wc);
		return wc;
	}

	protected WorldConfiguration getWorld(String world) {
		for (WorldConfiguration wc : this.configs)
			if (wc.getWorld().equals(world))
				return wc;
		throw new RuntimeException("Config not found! " + world);
	}

	public List<WorldConfiguration> getWorlds() {
		return this.configs;
	}
}
