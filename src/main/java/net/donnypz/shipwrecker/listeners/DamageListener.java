package net.donnypz.shipwrecker.listeners;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class DamageListener implements Listener {

    private static final int GOLD_PER_DAMAGE = 5;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if (!(e.getDamageSource().getCausingEntity() instanceof Player player)){
            return;
        }
        if (!(e.getEntity() instanceof LivingEntity le) || le instanceof Player){
            return;
        }

        SWArena swArena = SWArena.getArenaContainer(player, SWArena.class);
        if (swArena == null) return;
        SWProfile profile = swArena.getPlayerProfile(player, SWProfile.class);
        if (profile == null) return;
        profile.addGold(GOLD_PER_DAMAGE, true, false);
        player.playSound(player, Sound.ENTITY_ALLAY_ITEM_TAKEN, 1, 1f);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e){
        if (e.getEntity() instanceof TNTPrimed){
            e.blockList().clear();
        }
    }
}
