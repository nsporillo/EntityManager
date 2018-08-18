package net.porillo.config;

import net.porillo.types.*;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.text.ParseException;
import java.util.Set;

import static net.porillo.types.Type.*;

public class ConfigListUtility {

    private final WorldConfiguration wc;

    public ConfigListUtility(WorldConfiguration wc) {
        this.wc = wc;
    }

    public void loadConfigLists() {
        loadBlockedItems();
        loadBlockedDispenserItems();
        loadBlockedInteractionBlocks();
        loadBlockedSpawnEggs();
        loadBlockedEntities();
        loadBlockedSpawnReasons();
        loadPotionManager();
    }

    private void loadBlockedItems() {
        for (String s : wc.c.getStringList("Disable.Usage.Blocked_Items")) {
            try {
                Material mat = Material.valueOf(s.toUpperCase().trim());
                wc.usageBlock.add(new Item(mat));
            } catch (Exception ex) {
                wc.getLog().severe("Invalid value: " + s);
                wc.getLog().severe("Found in Disable.Usage.Blocked_Items");
            }
        }
    }

    private void loadPotionManager() {
        if (wc.get(Option.POTION)) {
            wc.getLog().info("PotionManager enabled");

            // store disabled hand throwing potions
            for (String s : wc.c.getStringList("PotionManager.DisableThrowing")) {
                try {
                    wc.usageBlock.add(new Potion(s));
                } catch(IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            // store disabled dispenser throwing potions
            for (String s : wc.c.getStringList("PotionManager.DisableDispensing")) {
                try {
                    wc.dispBlock.add(new Potion(s));
                } catch(IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            // store amplified potions
            for (String s : wc.c.getStringList("PotionManager.IntensityModifier")) {
                try {
                    wc.ampedPots.add(new Potion(s));
                } catch(IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void loadBlockedInteractionBlocks() {
        for (String s : wc.c.getStringList("Disable.Interaction.Blocked_Blocks")) {
            try {
                Material mat = Material.valueOf(s.toUpperCase().trim());

                if (mat.isBlock()) {
                    wc.blockedBlocks.add(new Item(mat));
                } else {
                    wc.getLog().severe("Material: " + mat.toString() + " is not a block!");
                }
            } catch (Exception ex) {
                wc.getLog().severe("Invalid value: " + s);
                wc.getLog().severe("Found in Disable.Interaction.Blocked_Blocks");
            }
        }
    }

    private void loadBlockedDispenserItems() {
        for (String s : wc.c.getStringList("Disable.Dispensing.Blocked_Items")) {
            try {
                Material mat = Material.valueOf(s.toUpperCase());
                wc.dispBlock.add(new Item(mat));
            } catch (Exception ex) {
                wc.getLog().severe("Invalid value: " + s);
                wc.getLog().severe("Found in Disable.Dispensing.Blocked_Items");
            }
        }
    }

    private void loadBlockedSpawnEggs() {
        for (String s : wc.c.getStringList("EggManager.Disabled_Eggs")) {
            try {
                wc.disEggs.add(EntityType.valueOf(s.toUpperCase()));
            } catch (Exception ex) {
                wc.getLog().severe("Invalid value: " + s);
            }
        }
    }

    private void loadBlockedEntities() {
        Set<Spawnable> l = wc.disMobs;

        for (String s : wc.c.getStringList("SpawnManager.Disallowed_Mobs")) {
            try {
                if (!s.contains(":")) {
                    l.add(new Spawnable(EntityType.valueOf(s.toUpperCase().trim()), ALL));
                } else {
                    // ex. zombie:baby sheep:red sheep:baby:black
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

                        if (s1.equals("ZOMBIE") && (s2.equals("BABY") || s2.equals("VILLAGER") && (s3.equals("BABY") || s3.equals("VILLAGER")))) {
                            l.add(new Spawnable(t, BOTH));
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
            } catch (Exception ex) {
                wc.getLog().severe("Unhandled error occurred loading disallowed mobs!");
                ex.printStackTrace();
            }
        }
    }

    private  void loadBlockedSpawnReasons() {
        for (String s : wc.c.getStringList("SpawnManager.Disallowed_Reasons")) {
            try {
                wc.disReasons.add(SpawnReason.valueOf(s.toUpperCase().trim()).toString());
            } catch (Exception ex) {
                wc.getLog().severe("Invalid value: " + s);
            }
        }
    }
}
