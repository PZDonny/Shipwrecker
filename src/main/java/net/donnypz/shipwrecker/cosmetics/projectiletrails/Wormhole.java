package net.donnypz.shipwrecker.cosmetics.projectiletrails;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.cosmeticConditions.FieldMinimumCondition;
import net.donnypz.mccore.cosmetics.preset.basic.ProjectileTrail;
import net.donnypz.mccore.utils.particles.DirectionalParticleRingRedstone;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

public class Wormhole extends ProjectileTrail {

    public Wormhole(CosmeticRegistry registry) {
        super("wormhole", registry);
        this.setCosmeticDisplayName("Wormhole")
                .addCondition(new FieldMinimumCondition("matches_played", "matches played", 2))
                .setDisplayMaterial(Material.END_PORTAL_FRAME);
    }

    @Override
    public void execute(Projectile projectile) {
        Player p = (Player) projectile.getShooter();
        if (p == null) return;
        DirectionalParticleRingRedstone ring = new DirectionalParticleRingRedstone(0.5, 3);
        ring.setColors(Color.PURPLE, Color.FUCHSIA, Color.YELLOW);
        Location loc = projectile.getLocation().clone();
        float yaw = p.getLocation().getYaw();
        loc.setYaw(yaw);
        new BukkitRunnable(){
            @Override
            public void run() {
                if (!isProjectileEligible(projectile)){
                    cancel();
                    return;
                }
                Location newLoc = projectile.getLocation().clone();
                newLoc.setYaw(yaw);
                ring.spawn(newLoc, null);
                p.playSound(projectile.getLocation(), Sound.ENTITY_ALLAY_ITEM_THROWN, 3f, 1.25f);
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 2, 2);
    }
}
