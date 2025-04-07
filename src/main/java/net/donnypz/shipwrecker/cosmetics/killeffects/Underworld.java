package net.donnypz.shipwrecker.cosmetics.killeffects;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.KillEffect;
import net.donnypz.mccore.utils.particles.DirectionalParticleHelix;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Underworld extends KillEffect {

    public Underworld(CosmeticRegistry registry) {
        super("underworld", registry);
        this.setCosmeticDisplayName("Underworld");
        this.setDisplayMaterial(Material.MAGMA_BLOCK);
    }

    @Override
    public void playKillEffect(Player killer, LivingEntity victim) {
        Location l = victim.getLocation();

        l.getWorld().playSound(l, Sound.ENTITY_VEX_CHARGE, 1, 0.6f);
        World w = l.getWorld();

        l.add(0, 1, 0);
        w.spawnParticle(Particle.CRIMSON_SPORE, l, 200, 0, 1.2, 0, 1);
        w.spawnParticle(Particle.SOUL, l, 45, 0.1, 0.65, 0.1, 0.1);

        l.add(0, 1.5 ,0);
        l.setPitch(90);
        l.setYaw(0);

        DirectionalParticleHelix flameHelix1 = new DirectionalParticleHelix(0.75, 2, 0, 0.15);
        flameHelix1.setAngleToNextParticle(30);
        flameHelix1.setParticles(Particle.FLAME);
        flameHelix1.spawn(l, null);

        DirectionalParticleHelix soulFlameHelix1 = new DirectionalParticleHelix(0.75, 2, 0, 0.15);
        soulFlameHelix1.setParticles();
        soulFlameHelix1.setAngleToNextParticle(30);
        soulFlameHelix1.setParticles(Particle.SOUL_FIRE_FLAME);
        soulFlameHelix1.setStartAngle(90);
        soulFlameHelix1.spawn(l, null);


        DirectionalParticleHelix flameHelix2 = new DirectionalParticleHelix(0.75, 2, 0, 0.15);
        flameHelix2.setAngleToNextParticle(30);
        flameHelix2.setParticles(Particle.FLAME);
        flameHelix2.setStartAngle(180);
        flameHelix2.spawn(l, null);

        DirectionalParticleHelix soulFlameHelix2 = new DirectionalParticleHelix(0.75, 2, 0, 0.15);
        soulFlameHelix2.setAngleToNextParticle(30);
        soulFlameHelix2.setParticles(Particle.SOUL_FIRE_FLAME);
        soulFlameHelix2.setStartAngle(270);
        soulFlameHelix2.spawn(l, null);
    }
}
