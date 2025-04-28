package net.donnypz.shipwrecker.cosmetics.projectiletrails;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.preset.basic.ProjectileTrail;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

public class Hearty extends ProjectileTrail {
    public Hearty(CosmeticRegistry registry) {
        super("hearty", registry);
        setCosmeticDisplayName("Hearty");
        setDisplayMaterial(Material.GOLDEN_APPLE);
    }

    @Override
    public void execute(Projectile projectile) {
        Player p = (Player) projectile.getShooter();
        if (p == null) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                if (!isProjectileEligible(projectile)){
                    cancel();
                    return;
                }
                p.spawnParticle(Particle.HEART, projectile.getLocation(), 1, 0, 0, 0, 0);
                p.playSound(projectile.getLocation(), Sound.ENTITY_CHICKEN_EGG, 4f, 1.5f);
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 2, 1);
    }
}
