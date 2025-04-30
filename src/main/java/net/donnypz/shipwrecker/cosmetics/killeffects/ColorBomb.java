package net.donnypz.shipwrecker.cosmetics.killeffects;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.cosmeticConditions.FieldMinimumCondition;
import net.donnypz.mccore.cosmetics.preset.basic.KillEffect;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.Set;

public class ColorBomb extends KillEffect {
    Material[] blockdatas = new Material[]{Material.LIME_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE,
            Material.PINK_CONCRETE,
            Material.RED_CONCRETE,
            Material.ORANGE_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.YELLOW_CONCRETE};

    private static final Random random = new Random();
    public ColorBomb(CosmeticRegistry registry) {
        super("color_bomb", registry);
        this.addCondition(new FieldMinimumCondition(Set.of("kills_melee", "kills_projectile"), "kills", 3));
        this.setCosmeticDisplayName("Color Bomb");
        this.setDisplayMaterial(Material.WHITE_DYE);
    }

    @Override
    public void playKillEffect(Player killer, LivingEntity victim) {
        Location l = victim.getLocation();
        l.add(0, 2, 0);
        World w = l.getWorld();
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if (i == 30){
                    cancel();
                    return;
                }
                w.spawnParticle(Particle.BLOCK, l, 15, 0.15, 0.5, 0.15, 1, blockdatas[random.nextInt(blockdatas.length)].createBlockData());
                w.playSound(l, Sound.ENTITY_ITEM_PICKUP, 0.75f, 1.5f);
                i++;
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 2);

    }
}
