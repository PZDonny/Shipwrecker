package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.cosmetics.KillEffect;
import net.donnypz.mccore.minigame.arenaManager.Arena;
import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Death implements Listener {
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
            swArena.eliminatePlayer(player);
            return;
        }

        //Play Kill Effect
        Player killer = victim.getKiller();
        if (killer != null){
            SWProfile profile = swArena.getPlayerProfile(killer, SWProfile.class);
            if (profile == null){
                return;
            }

            //Add Kill Credit
            profile.addMeleeKill();

            //Play Cosmetic Kill Effect
            KillEffect killEffect = profile.getKillEffect();
            if (killEffect != null){
                killEffect.playKillEffect(killer, victim);
            }
        }
    }
}
