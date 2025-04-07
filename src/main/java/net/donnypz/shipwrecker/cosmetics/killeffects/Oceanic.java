package net.donnypz.shipwrecker.cosmetics.killeffects;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.KillEffect;
import net.donnypz.mccore.utils.inventory.cosmetic.DocumentCountCondition;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Oceanic extends KillEffect {
    public Oceanic(CosmeticRegistry registry) {
        super("oceanic", registry);
        this.addDocumentCountCondition(new DocumentCountCondition(Shipwrecker.getInstance().getKillEffectCollection()
                ,2,
                "player_uuid",
                DocumentCountCondition.CountType.AT_LEAST,
                "unlocked kill effects"));
        this.setCosmeticDisplayName("Oceanic");
        this.setDisplayMaterial(Material.KELP);
    }

    @Override
    public void playKillEffect(Player killer, LivingEntity victim) {
        Location l = victim.getLocation();
        l.add(0, 2, 0);
        World w = l.getWorld();
        w.spawnParticle(Particle.NAUTILUS, l, 100, 0 , 0.5, 0, 1);
        w.playSound(l, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 1, 1);
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
            w.spawnParticle(Particle.BUBBLE_POP, l, 200, 0.3, 0.15, 0.3, 0.25);
            w.spawnParticle(Particle.GLOW, l, 100, 0.2, 0.1, 0.2, 0.75);
            Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> w.spawnParticle(Particle.FALLING_WATER, l.clone().add(0, 0.1, 0) , 125, 0.25, 0.1, 0.25), 2);
            w.playSound(l, Sound.ENTITY_AXOLOTL_SPLASH, 1, 1.25f);
        }, 38);

    }
}
