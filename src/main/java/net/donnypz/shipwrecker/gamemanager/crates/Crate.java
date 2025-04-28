package net.donnypz.shipwrecker.gamemanager.crates;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.item.ItemUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.donnypz.shipwrecker.gamemanager.WorldPositioner;
import net.donnypz.shipwrecker.gamemanager.UtilityShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Random;
import org.joml.Vector3f;

public class Crate {

    private static final Random random = new Random();
    private static final float displayScale = 4.5f;
    private static final Reward[] normal = new Reward[]{
            new GoldReward(50, 150),
            new ItemReward(ItemUtils.makeItem(Material.GOLDEN_CARROT, 2)),
            new ItemReward(ItemUtils.makeItem(Material.COOKED_COD, 3)),
            new ItemReward(UtilityShop.getImpactTnT(1)),
            new PotionEffectReward(new PotionEffect(PotionEffectType.REGENERATION, 20*20,1, false)),
            new PotionEffectReward(new PotionEffect(PotionEffectType.SPEED, 20*60,0, false))
    };

    private static final Reward[] special = new Reward[]{
            new GoldReward(200, 500),
            new ItemReward(ItemUtils.makeItem(Material.GOLDEN_APPLE, 3)),
            new ItemReward(new ItemBuilder(Material.DIAMOND_AXE)
                    .setDisplayName(Component.text("Sea Basher", NamedTextColor.AQUA))
                    .addEnchantment(Enchantment.SHARPNESS, 2)
                    .addEnchantment(Enchantment.KNOCKBACK, 1)
                    .setMaxDamage(30)
                    .build()),
            new PotionEffectReward(new PotionEffect(PotionEffectType.STRENGTH, 20*30,0, false)),
            new ItemReward(UtilityShop.getImpactTnT(5)),
    };

    private Crate(){}

    public static void spawnNormal(SWArena swArena){
        spawn(swArena, false);
    }

    public static void spawnSpecial(SWArena swArena){ //20% faster
        spawn(swArena, true);
    }

    private static void spawn(SWArena swArena, boolean isSpecial){
        int period = isSpecial ? 2 : 3;
        Location spawnLoc = swArena.getWorldPositioner().getRandomPickupLocation();

        ItemDisplay display = spawnLoc.getWorld().spawn(spawnLoc, ItemDisplay.class, d -> {
            d.setTeleportDuration(period);
            d.setViewRange(2);
            d.setBrightness(new Display.Brightness(15, 15));
            d.setPersistent(true);
            d.setGlowing(true);
            d.setTransformation(new Transformation(new Vector3f(), new Quaternionf()
                    .rotationYXZ(
                            random.nextInt(360),
                            random.nextInt(360),
                            random.nextInt(360)),
                    new Vector3f(displayScale),
                    new Quaternionf()));
            if (isSpecial){
                d.addScoreboardTag("special");
                d.setItemStack(new ItemStack(Material.MAGENTA_SHULKER_BOX));
                d.setGlowColorOverride(Color.PURPLE);
            }
            else{
                d.addScoreboardTag("normal");
                d.setItemStack(new ItemStack(Material.BARREL));
                d.setGlowColorOverride(Color.ORANGE);
            }
        });

        new BukkitRunnable(){
            Location environmentOrigin = swArena.getWorldPositioner().getEnvironmentOrigin();
            Vector v = Direction.WEST.getVector(environmentOrigin);
            World w = environmentOrigin.getWorld();
            float boundingBoxSize = displayScale+0.5f;
            @Override
            public void run() {
                if (
                        swArena.getArena().isEndingOrNotUsable() ||
                        display.getLocation().distanceSquared(environmentOrigin) >= WorldPositioner.translationDistanceSquared ||
                        display.isDead()
                ){
                    display.remove();
                    return;
                }

                Location displayLoc = display.getLocation();
                displayLoc.add(v);
                display.teleport(displayLoc, TeleportFlag.EntityState.RETAIN_PASSENGERS);

                //Check for fishing rods
                BoundingBox box = BoundingBox.of(display.getLocation(), boundingBoxSize, boundingBoxSize, boundingBoxSize);
                for (Entity e : w.getNearbyEntities(box, e -> e instanceof FishHook)){
                    FishHook fh = (FishHook) e;
                    Player p = Bukkit.getPlayer(fh.getOwnerUniqueId());
                    reward(display, p, swArena);
                    removeDisplay(display);
                    fh.remove();
                    return;
                }
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, period);
    }

    private static void removeDisplay(Display display){
        World w = display.getWorld();
        Location l = display.getLocation();
        float d = displayScale/2;
        w.spawnParticle(Particle.CLOUD, l, 50, d, d, d, 0.5);
        w.spawnParticle(Particle.GLOW_SQUID_INK, l, 50, d, d, d, 0.5);
        display.remove();
    }

    public static void reward(Display display, Player player, SWArena swArena){
        SWProfile profile = swArena.getPlayerProfile(player, SWProfile.class);

        profile.addCrateCollected();
        Reward reward;
        if (display.getScoreboardTags().contains("normal")){
            reward = normal[random.nextInt(normal.length)];
        }
        else{
            reward = special[random.nextInt(special.length)];
        }
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.sendMessage(Component.text("TREASURE CAUGHT", NamedTextColor.GREEN, TextDecoration.BOLD));
        Component msg = reward.message();
        if (msg != null){
            player.sendMessage(msg);
        }
        reward.giveReward(player, swArena);
    }
}
