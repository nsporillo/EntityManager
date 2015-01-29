package net.milkycraft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.EntityType;

import net.milkycraft.config.WorldConfiguration;
import net.milkycraft.types.Option;

public class LogFile {

	private static String[] names = { "Amplified Potions", "Blocked Blocks", "Disabled Eggs",
			"Disabled Mobs", "Dispenser Block", "Disabled Reasons", "Usage Block" };

	public static void newDump(EntityManager em) {
		List<String> lines = new ArrayList<String>();

		for (WorldConfiguration wc : em.getWorlds()) {
			lines.add("WORLD: " + wc.getWorld());
			lines.add("OPTIONS: ");
			for (Option op : Option.values()) {
				lines.add("\t" + op.name() + " -> " + wc.get(op));
			}
			lines.add("COLLECTIONS: ");
			lines.addAll(collect(wc.ampedPots, wc.blockedBlocks, wc.disEggs, wc.disMobs,
					wc.dispBlock, wc.disReasons, wc.usageBlock));
			lines.add("=================================================================");
		}
		try {
			writeDump(System.currentTimeMillis() + ".log", lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> collect(Object... all) {
		List<String> list = new ArrayList<String>();
		int i = 0;
		for (Object c : all) {
			list.add("\t" + names[i] + ": ");
			Set<Object> cc = (Set<Object>) c;
			for (Object s : cc) {
				if(s instanceof Short) {
					list.add("\t\t" + EntityType.fromId((int) s));
					continue;
				}
				list.add("\t\t" + s.toString());
			}
			i++;
		}
		return list;
	}

	private static void writeDump(String aFileName, List<String> aLines) throws IOException {
		Path path = Paths.get(aFileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (String line : aLines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}
}
