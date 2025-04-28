package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.cosmetics.preset.basic.KillEffect;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.shipwrecker.gamemanager.LifeManager;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        LivingEntity victim = e.getEntity();
        Arena arena = ArenaManager.getArena(victim.getWorld().getName());
        SWArena swArena =  SWArena.getArenaContainer(arena, SWArena.class);
        if (arena == null || swArena == null){
            return;
        }

        if (victim instanceof Player player) {
            e.setCancelled(true);
            LifeManager.setSpectator(player, swArena);
            return;
        }

        Player killer = victim.getKiller();
        e.setDroppedExp(0);
        e.getDrops().clear();
        if (killer == null) return;

        SWProfile profile = swArena.getPlayerProfile(killer, SWProfile.class);
        if (profile == null){
            return;
        }

        if (!arena.isEndingOrNotUsable()){
            if (e.getDamageSource().getDirectEntity() instanceof Projectile){
                profile.addProjectileKill();
            }
            else{
                //Add Melee Kill Credit
                profile.addMeleeKill();
            }
        }


        //Play Cosmetic Kill Effect
        KillEffect killEffect = profile.getKillEffect();
        if (killEffect != null){
            killEffect.playKillEffect(killer, victim);
        }
    }
}
