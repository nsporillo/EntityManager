package net.milkycraft.em;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.milkycraft.em.config.Option;
import net.milkycraft.em.config.WorldConfiguration;

public class TimeManager extends Utility {

	public TimeManager(EntityManager manager) {
		super(manager);
		this.init();
	}

	private void init() {
		for (WorldConfiguration conf : super.getHandle().getWorlds()) {
			if (conf.get(Option.TIME)) {
				String m = conf.getWorld();
				long i = conf.g(Option.INTERVAL);
				long t = conf.g(Option.TARGET);
				start(m, i, t);
			}
		}
	}

	private void start(String name, final long target, long every) {
		final World world = Bukkit.getWorld(name);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(super.getHandle(),
				new Runnable() {

					@Override
					public void run() {
						world.setTime(target);
					}
				}, every, every);
	}
}
