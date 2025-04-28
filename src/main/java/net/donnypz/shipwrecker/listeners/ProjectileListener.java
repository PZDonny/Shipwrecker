package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.cosmetics.preset.basic.ProjectileTrail;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileListener implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e){
        Projectile projectile = e.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }

        SWArena swArena = SWArena.getArenaContainer(player, SWArena.class);
        if (swArena == null) return;
        SWProfile profile = swArena.getPlayerProfile(player, SWProfile.class);
        if (profile == null) return;

        ProjectileTrail trail = profile.getProjectileTrail();
        if (trail != null){
            trail.execute(projectile);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e){
        Projectile projectile = e.getEntity();

        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }

        Arena arena = ArenaManager.getArenaOfPlayer(player);
        if (arena == null){
            return;
        }

        if (projectile instanceof Snowball snowball) {
            TNTPrimed tnt = player.getWorld().spawn(snowball.getLocation(), TNTPrimed.class);
            tnt.setFuseTicks(0);
            tnt.setYield(2.65f);
            tnt.setCustomNameVisible(false);
            tnt.setSource(player);
            snowball.remove();
        }
    }
}
