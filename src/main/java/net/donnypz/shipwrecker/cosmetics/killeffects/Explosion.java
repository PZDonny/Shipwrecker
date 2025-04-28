package net.donnypz.shipwrecker.cosmetics.killeffects;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.preset.basic.KillEffect;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Explosion extends KillEffect {

    public Explosion(CosmeticRegistry registry) {
        super("explosion", registry);
        this.setCurrency(500, Shipwrecker.COINS, "coins");
        this.setCosmeticDisplayName("Explosion");
        this.setDisplayMaterial(Material.TNT);
    }

    @Override
    public void playKillEffect(Player killer, LivingEntity victim) {
        Location l = victim.getLocation();
        l.add(0, victim.getBoundingBox().getHeight()/2, 0);
        World w = l.getWorld();
        w.spawnParticle(Particle.EXPLOSION, l, 2);
        w.playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.75f, 1.25f);
    }
}
