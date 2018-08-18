package net.porillo;

import net.porillo.config.WorldConfiguration;
import net.porillo.types.Potion;
import net.porillo.types.Type;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

import static net.porillo.types.Option.*;
import static net.porillo.types.Option.LIGHTNING;
import static net.porillo.types.Permission.*;
import static net.porillo.types.Type.*;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class EventListener extends Utility implements Listener {

    private Map<String, List<ItemStack>> drops = new HashMap<>();
    private Set<Entity> mobs = new HashSet<>();

    public EventListener(EntityManager manager) {
        super(manager);
        super.register(this);
    }

    private void handleSpawnTry(Player pl, String str, PlayerInteractEvent e, WorldConfiguration c) {
        if (!hasPermission(pl, sNTITY + str)) {

            String alert = str.replaceAll("_", " ");
            e.setUseItemInHand(Result.DENY);
            e.setCancelled(true);
            alertAdminsAndLog(c, "Player " + pl.getName() + " tried to spawn use " + alert);
            alertPlayer(c, pl, "&cYou don't have permission to use " + alert + "s.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilEnchant(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.ANVIL && e.getSlotType() == SlotType.RESULT && e.getWhoClicked() instanceof Player) {
            Player pl = (Player) e.getWhoClicked();
            WorldConfiguration conf = getConfig(pl.getWorld());

            if (conf.get(ENCHANTING) && !hasPermission(pl, iANVIL)) {
                e.setCancelled(true);
                alertAdminsAndLog(conf, "Player " + pl.getName() + " tried to enchant a "
                        + e.getCurrentItem().getType().toString().toLowerCase()
                        + " in an anvil.");
                alertPlayer(conf, pl, "&cYou don't have permission to use anvils.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player pl = ((Player) e.getEntity());
            WorldConfiguration conf = get(pl.getWorld().getName());

            if (conf.get(SHOOTING) && !hasPermission(pl, iSHOOT)) {
                e.setCancelled(true);
                alertAdminsAndLog(conf, "Player " + pl.getName() + " tried to shoot a bow.");
                alertPlayer(conf, pl, "&cYou don't have permission to shoot bows.");
            }

        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent e) {
        WorldConfiguration conf = getConfig(e.getBlock().getWorld());
        ItemStack is = e.getItem();

        if (is.getType().name().contains("POTION")) {
            e.setCancelled(conf.dispensePotion(is));
        } else {
            e.setCancelled(conf.dispense(is.getType()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Creature) {
            WorldConfiguration conf = getConfigByEntity(e.getEntity());

            if (mobs.contains(e.getEntity())) {
                if (conf.get(NOEXP)) {
                    e.setDroppedExp(0);
                } else if (conf.get(NODROPS)) {
                    e.getDrops().clear();
                }

                mobs.remove(e.getEntity());
            }

            if (!conf.get(EDEATHEXP)) {
                e.setDroppedExp(0);
            }

            if (!conf.get(EDEATHDROPS)) {
                e.getDrops().clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        LivingEntity en = e.getEntity();
        WorldConfiguration conf = getConfigByEntity(en);

        if (conf.get(SDISABLE) || conf.getDisabledSpawnReasons().contains(e.getSpawnReason().toString())) {
            e.setCancelled(true);
            return;
        }

        EntityType type = e.getEntityType();
        if (conf.isDisabledMob(type)) {
            if (en instanceof Ageable) {
                if (en instanceof Sheep) {
                    Sheep sheep = (Sheep) en;
                    Color color = sheep.getColor().getColor();

                    if (sheep.isAdult()) {
                        e.setCancelled(conf.block(type, ALL, color));
                        return;
                    }

                    e.setCancelled(conf.block(type, BABY, color));
                    return;
                }

                if (!((Ageable) en).isAdult()) {
                    e.setCancelled(conf.block(type, BABY));
                }

                return;
            } else if (en instanceof Zombie) {
                Zombie z = (Zombie) en;

                if (z.isBaby() && !z.isVillager()) {
                    e.setCancelled(conf.block(type, BABY));
                } else if (z.isVillager() && !z.isBaby()) {
                    e.setCancelled(conf.block(type, Type.VILLAGER));
                } else if (z.isBaby() || z.isVillager()) {
                    e.setCancelled(conf.block(type, BOTH));
                }

                return;
            }
            e.setCancelled(true);
        } else if (e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
            mobs.add(e.getEntity());
        }

        if (conf.get(NOMOBARMOR)) {
            en.getEquipment().setArmorContents(null);
            en.getEquipment().getItemInHand().setType(Material.AIR);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Player pl = e.getPlayer();

        if (e.getRightClicked() instanceof org.bukkit.entity.Villager) {
            WorldConfiguration conf = getConfigByEntity(e.getRightClicked());

            if (conf.get(TRADING) && !hasPermission(pl, iTRADE)) {
                e.setCancelled(true);
                alertPlayer(conf, pl, "&cYou don't have permission to trade.");
            }

        } else if (e.getRightClicked() instanceof Sheep) {
            WorldConfiguration conf = getConfigByEntity(e.getRightClicked());
            EntityType type = e.getRightClicked().getType();
            Player p = e.getPlayer();
            ItemStack is = p.getItemInHand();

            if (conf.isDisabledMob(type) && is.getType() == Material.INK_SAC) {
                DyeColor dc = DyeColor.getByDyeData((byte) is.getDurability());
                String c = dc.toString().toLowerCase();
                Sheep sheep = (Sheep) e.getRightClicked();

                if (sheep.isAdult()) {
                    e.setCancelled(conf.block(type, ALL, dc.getColor()) && !pl.isOp());
                } else {
                    e.setCancelled(conf.block(type, BABY, dc.getColor()) && !pl.isOp());
                }

                if (e.isCancelled()) {
                    alertPlayer(conf, p, "&cThat color of sheep is blocked (" + c + ")");
                    alertPlayer(conf, p, "&cDye apply was client side - relog :)");
                    alertAdminsAndLog(conf, "Player " + p.getName() + " tried to dye a sheep " + c);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteraction(PlayerInteractEvent e) {
        if (!e.hasItem())
            return;

        if (e.getAction() == RIGHT_CLICK_AIR || e.getAction() == RIGHT_CLICK_BLOCK) {
            WorldConfiguration conf = getConfig(e.getPlayer().getWorld());
            String itemName = e.getItem().getType().toString().toLowerCase();

            if (e.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
                if (conf.get(FIREWORKS) && !hasPermission(e.getPlayer(), iUITEM + itemName)) {
                    e.setUseItemInHand(Result.DENY);
                    e.setCancelled(true);
                    alertAdminsAndLog(conf, "Player " + e.getPlayer().getName() + " tried to use a firework.");
                    alertPlayer(conf, e.getPlayer(), "&cYou don't have permission to use fireworks.");
                }
            } else if (e.getItem().getType().name().endsWith("SPAWN_EGG")) {
                if (conf.get(EDISABLE))
                    handleSpawnTry(e.getPlayer(), itemName, e, conf);
                else {
                    EntityType type = fromId(e.getItem().getDurability());

                    if (conf.getDisabledSpawnEggs().contains(type))
                        handleSpawnTry(e.getPlayer(), itemName, e, conf);
                }
            } else if (e.getItem().getType().name().contains("POTION")) {
                String potionPerm = Potion.getPotionPermission(e.getItem());

                if (hasPermission(e.getPlayer(), iPTION) || hasPermission(e.getPlayer(), iPTION + "." + potionPerm))
                    return;

                if (conf.usagePotion(e.getItem())) {
                    e.setUseItemInHand(Result.DENY);
                    e.setCancelled(true);
                    warnPotion(conf, e.getPlayer(), e.getItem());
                }
            } else {
                if (conf.usage(e.getItem().getType()) && !hasPermission(e.getPlayer(), iUITEM + itemName)) {
                    String item = itemName.replace("_", " ");
                    e.setUseItemInHand(Result.DENY);
                    e.setCancelled(true);
                    alertAdminsAndLog(conf, "Player " + e.getPlayer().getName() + " tried to use an " + item + ".");
                    alertPlayer(conf, e.getPlayer(), "&cYou don't have permission to use that &6" + item + "&c.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemEnchant(EnchantItemEvent e) {
        Player pl = e.getEnchanter();
        WorldConfiguration conf = get(pl.getWorld().getName());

        if (conf.get(ENCHANTING) && !hasPermission(pl, iCHANT)) {
            e.setCancelled(true);
            alertAdminsAndLog(conf, "Player " + pl.getName() + " tried to enchant a "
                    + e.getItem().getType().toString().toLowerCase());
            alertPlayer(conf, pl, "&cYou don't have permission to enchant.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLightning(LightningStrikeEvent e) {
        e.setCancelled(getConfig(e.getWorld()).get(LIGHTNING));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCombat(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player attacked = ((Player) e.getEntity());
            WorldConfiguration conf = get(attacked.getWorld().getName());

            if (e.getDamager() instanceof Player) {
                Player ag = ((Player) e.getDamager());
                e.setCancelled(alertPlayerNoPvp(conf, ag, attacked.getName()));
            } else if (e.getDamager() instanceof Projectile && !(e.getDamager() instanceof EnderPearl)) {
                ProjectileSource a = ((Projectile) e.getDamager()).getShooter();

                if (a instanceof Player) {
                    Player p = (Player) a;
                    e.setCancelled(alertPlayerNoPvp(conf, p, attacked.getName()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player pl = e.getEntity();
        WorldConfiguration conf = getConfig(pl.getWorld());

        if (hasPermission(pl, dKITEM) || conf.get(PDEATHITEMS)) {
            drops.put(pl.getName(), new ArrayList<>(e.getDrops()));
            e.getDrops().clear();
        }

        if (hasPermission(pl, dKEEXP) || conf.get(PDEATHEXP)) {
            e.setKeepLevel(true);
            e.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();

        if (drops.containsKey(p.getName())) {
            Bukkit.getScheduler().runTaskLater(getHandle(), () -> {
                WorldConfiguration wc = getConfig(p.getWorld());
                alertAdminsAndLog(wc, "Player " + p.getName() + " respawned with their items");
                alertPlayer(wc, p, "&6Your items were returned after death!");

                for (ItemStack is : drops.get(p.getName())) {
                    if (is != null) {
                        p.getInventory().addItem(is);
                    }
                }

                drops.remove(p.getName());
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalMake(EntityCreatePortalEvent e) {
        if (e.getEntity() instanceof Player) {
            Player pl = ((Player) e.getEntity());
            WorldConfiguration conf = get(pl.getWorld().getName());

            if (conf.get(PORTAL_CREATE) && !hasPermission(pl, cPRTAL)) {
                e.setCancelled(true);
                alertAdminsAndLog(conf, "Player " + pl.getName() + " tried to create portals");
                alertPlayer(conf, pl, "&cYou don't have permission to create a portal.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            ItemStack is = e.getPotion().getItem();

            WorldConfiguration conf = getConfig(p.getWorld());
            String potionPerm = Potion.getPotionPermission(is);

            if (conf.usagePotion(is)) {
                if (hasPermission(p, iPTION) || hasPermission(p, iPTION + "." + potionPerm)) {
                    return;
                }
                e.setCancelled(true);
                warnPotion(conf, p, is);
                return;
            }

            double mult = conf.getMultiplier(is);

            for (LivingEntity le : e.getAffectedEntities()) {
                double iten = e.getIntensity(le);
                e.setIntensity(le, iten * mult);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        WorldConfiguration conf = getConfigByEntity(e.getEntity());
        Player p = (Player) e.getEntity().getShooter();
        EntityType type = e.getEntity().getType();

        if (type == SPLASH_POTION || type == LINGERING_POTION) {
            ItemStack stack = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = p.getInventory().getItemInMainHand().getItemMeta();
            if (itemMeta instanceof PotionMeta) {
                String potionPerm = Potion.getPotionPermission(stack);

                if (!hasPermission(p, iPTION + "_" + potionPerm) && conf.usagePotion(stack)) {
                    e.setCancelled(true);
                    warnPotion(conf, p, stack);
                }
            }

            return; // dont continue because the catch-all will banish all potions! not good
        }

        String englishType = null;
        Material matType = null;

        switch (type) {
            case ARROW:
                matType = Material.ARROW;
                break;
            case DRAGON_FIREBALL:
                break;
            case EGG:
                matType = Material.EGG;
                break;
            case ENDER_PEARL:
                matType = Material.ENDER_PEARL;
                break;
            case FIREBALL:
                break;
            case FISHING_HOOK:
                matType = Material.FISHING_ROD;
                break;
            case SMALL_FIREBALL:
                break;
            case LLAMA_SPIT:
                break;
            case SNOWBALL:
                matType = Material.SNOWBALL;
                break;
            case SPECTRAL_ARROW:
                matType = Material.SPECTRAL_ARROW;
                break;
            case THROWN_EXP_BOTTLE:
                matType = Material.EXPERIENCE_BOTTLE;
                englishType = "experience bottle";
                break;
            case TIPPED_ARROW:
                matType = Material.TIPPED_ARROW;
                break;
            case TRIDENT:
                matType = Material.TRIDENT;
                break;
            case WITHER_SKULL:
                matType = Material.WITHER_SKELETON_SKULL;
                break;
            default:
                try {
                    // last ditch attempt
                    matType = Material.valueOf(type.name());
                } catch(Exception ex) {
                    getHandle().getLogger().severe("Recognized Projectile: " + type.name());
                    getHandle().getLogger().severe("Unable to block this item from being launched! Contact support.");
                }

        }

        if (matType != null && conf.usage(matType) && !hasPermission(p, matType.name().toLowerCase())) {
            e.setCancelled(true);
            alertPlayerNoPvp(conf, p, type, englishType);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent e) {
        e.setCancelled(e.toThunderState() && getConfig(e.getWorld()).get(THUNDER));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(e.toWeatherState() && getConfig(e.getWorld()).get(RAIN));
    }
}
