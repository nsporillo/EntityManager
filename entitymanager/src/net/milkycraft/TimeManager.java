package net.milkycraft;

import static net.milkycraft.types.Option.INTERVAL;
import static net.milkycraft.types.Option.TARGET;
import static net.milkycraft.types.Option.TIME;
import net.milkycraft.config.WorldConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeManager {

	public TimeManager(EntityManager em) {
		for (WorldConfiguration conf : em.getWorlds()) {
			if (conf.get(TIME)) {
				start(em, conf.getWorld(), conf.g(INTERVAL), conf.g(TARGET));
			}
		}
	}

	private void start(EntityManager em, String name, final long target, long every) {
		final World world = Bukkit.getWorld(name);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(em, new Runnable() {

			@Override
			public void run() {
				world.setTime(target);
			}
		}, every, every);
	}
}
