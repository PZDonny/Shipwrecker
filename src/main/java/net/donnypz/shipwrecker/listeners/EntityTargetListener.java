package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.shipwrecker.gamemanager.enemy.EnemyUtils;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import org.bukkit.GameMode;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTargetListener implements Listener {

    @EventHandler
    public void onTargetStop(EntityTargetLivingEntityEvent e){
        Arena arena = ArenaManager.getArena(e.getEntity().getWorld().getName());
        if (arena == null) return;
        SWArena sw = SWArena.getArenaContainer(arena, SWArena.class);


        Mob mob = (Mob) e.getEntity();
        if (e.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY){
            if (mob.getTarget() instanceof Player){
                e.setCancelled(true);
                return;
            }
        }

        if (e.getTarget() instanceof Mob || mob.getTarget() instanceof Mob){
            if (!(mob instanceof Goat)){
                e.setCancelled(true);
                return;
            }
        }

        if (e.getReason() == EntityTargetEvent.TargetReason.FORGOT_TARGET){
            e.setCancelled(true);
        }

        if (e.getReason() == EntityTargetEvent.TargetReason.TARGET_INVALID){
            Player p = null;
            if (mob.getTarget() instanceof Player){
                p = (Player) mob.getTarget();
            }

            if (e.getTarget() instanceof Player){
                p = (Player) e.getTarget();
            }
            if (p != null){
                if (p.getGameMode() == GameMode.SPECTATOR){
                    if (!arena.isEndingOrNotUsable()){
                        EnemyUtils.pathFindToRandomPlayer(sw, mob, true);
                    }
                }
            }
        }
    }
}
