package net.donnypz.shipwrecker.cosmetics.projectiletrails;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.cosmeticConditions.FieldMinimumCondition;
import net.donnypz.mccore.cosmetics.preset.basic.ProjectileTrail;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

public class Sparked extends ProjectileTrail {

    public Sparked(CosmeticRegistry registry) {
        super("sparked", registry);
        this
                .setCosmeticDisplayName("Sparked")
                .setDisplayMaterial(Material.LIGHTNING_ROD)
                .addCondition(new FieldMinimumCondition("kills_projectile", "projectile kills", 3))
                .addCondition(new FieldMinimumCondition("matches_won", "matches won", 1));
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
                p.spawnParticle(Particle.ELECTRIC_SPARK, projectile.getLocation(), 5, 0.15, 0.15, 0.15, 0.2);
                p.playSound(projectile.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 0.7f, 1.5f);
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 1, 1);
    }
}
