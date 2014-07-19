package net.milkycraft.config;

import static net.milkycraft.objects.Type.ALL;
import static net.milkycraft.objects.Type.BABY;
import static net.milkycraft.objects.Type.BOTH;

import java.util.Set;

import net.milkycraft.objects.Item;
import net.milkycraft.objects.Meta;
import net.milkycraft.objects.Spawnable;
import net.milkycraft.objects.Type;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class ConfigUtility {

	public static void load(WorldConfiguration wc) {
		ConfigUtility.loadBlockedItems(wc);
		ConfigUtility.loadBlockedDispenserItems(wc);
		ConfigUtility.loadBlockedInteractionBlocks(wc);
		ConfigUtility.loadBlockedSpawnEggs(wc);
		ConfigUtility.loadBlockedEntities(wc);
		ConfigUtility.loadBlockedSpawnReasons(wc);
	}
	
	private static void loadBlockedItems(WorldConfiguration wc) {
		for (String s : wc.c.getStringList("Disable.Usage.Blocked_Items")) {
			try {
				Material mat = Material.valueOf(s.toUpperCase());
				wc.usageBlock.add(new Item(mat.getId()));
			} catch (Exception ex) {
				if (s.toLowerCase().startsWith("potion")) {
					String[] args = s.split(":");
					try {
						Integer id = Integer.parseInt(args[1]);
						wc.usageBlock.add(new Item(373, id));
					} catch (Exception ex2) {
						wc.getLog().severe("Invalid value: " + s);
						wc.getLog().severe("Potion format=> \"potion:#\"");
					}
				} else {
					wc.getLog().severe("Invalid value: " + s);
					wc.getLog().severe("Reference: http://goo.gl/f1Nmb");
				}
			}
		}
	}

	private static void loadBlockedInteractionBlocks(WorldConfiguration wc) {
		for (String s : wc.c.getStringList("Disable.Interaction.Blocked_Blocks")) {
			try {
				Material mat = Material.valueOf(s.toUpperCase());
				if (mat.isBlock()) {
					wc.blockedBlocks.add(new Item(mat.getId()));
				} else {
					wc.getLog().severe("Material: " + mat.toString() + " is not a block!");
				}
			} catch (Exception ex) {
				wc.getLog().severe("Invalid value: " + s);
				wc.getLog().severe("Reference: http://goo.gl/f1Nmb");

			}
		}
	}

	private static void loadBlockedDispenserItems(WorldConfiguration wc) {
		for (String s : wc.c.getStringList("Disable.Dispensing.Blocked_Items")) {
			try {
				Material mat = Material.valueOf(s.toUpperCase());
				wc.dispBlock.add(new Item(mat.getId()));
			} catch (Exception ex) {
				if (s.toLowerCase().startsWith("potion")) {
					String[] args = s.split(":");
					Integer id = Integer.parseInt(args[1]);

					wc.dispBlock.add(new Item(373, id));
				} else {
					wc.getLog().severe("Invalid value: " + s);
					wc.getLog().severe("Reference: http://goo.gl/f1Nmb");
				}
			}
		}
	}

	private static void loadBlockedSpawnEggs(WorldConfiguration wc) {
		for (String s : wc.c.getStringList("EggManager.Disabled_Eggs")) {
			try {
				wc.disEggs.add(EntityType.valueOf(s.toUpperCase()).getTypeId());
			} catch (Exception ex) {
				wc.getLog().severe("Invalid value: " + s);
				wc.getLog().severe("Reference: http://goo.gl/E7mVB");
			}
		}
	}

	private static void loadBlockedEntities(WorldConfiguration wc) {
		Set<Spawnable> l = wc.disMobs;
		for (String s : wc.c.getStringList("SpawnManager.Disallowed_Mobs")) {
			try {
				if (s.indexOf(":") <= 0) {
					l.add(new Spawnable(EntityType.valueOf(s.toUpperCase()), ALL));
				} else {
					String[] a = s.split(":");
					EntityType t = EntityType.valueOf(a[0].toUpperCase());
					String s1 = t.toString();
					String s2 = a[1].toUpperCase();
					if (a.length == 2) {
						Meta meta;
						try {
							meta = new Meta(Type.valueOf(s2));
						} catch (Exception ex) {
							meta = new Meta(ALL, DyeColor.valueOf(s2).getColor());
						}
						l.add(new Spawnable(t, meta));
					} else if (a.length == 3) {
						String s3 = a[2].toUpperCase();
						if (s1.equals("ZOMBIE")) {
							if (s2.equals("BABY") || s2.equals("VILLAGER")) {
								if (s3.equals("BABY") || s3.equals("VILLAGER")) {
									l.add(new Spawnable(t, BOTH));
								}
							}
						} else if (s1.equals("SHEEP")) {
							if (s2.equals("BABY")) {
								Color c = DyeColor.valueOf(s3).getColor();
								l.add(new Spawnable(t, BABY, c));
							}
						}
					}
				}
			} catch (IllegalArgumentException ex) {
				wc.getLog().severe("Invalid value: " + s);
				wc.getLog().severe("Reference: http://goo.gl/E7mVB");
			} catch (Exception ex) {
				wc.getLog().severe("Unhandled error occured loading disallowed mobs!");
				ex.printStackTrace();
			}
		}
	}

	private static void loadBlockedSpawnReasons(WorldConfiguration wc) {
		for (String s : wc.c.getStringList("SpawnManager.Disallowed_Reasons")) {
			try {
				wc.disReasons.add(SpawnReason.valueOf(s.toUpperCase()).toString());
			} catch (Exception ex) {
				wc.getLog().severe("Invalid value: " + s);
				wc.getLog().severe("Reference: http://goo.gl/a4XRB");
			}
		}
	}

}
