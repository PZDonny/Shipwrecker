package net.donnypz.shipwrecker.listeners;

import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.donnypz.shipwrecker.gamemanager.UtilityShop;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InteractionListener implements Listener {

    @EventHandler
    public void onInteractionClicked(InteractionClickEvent e){
        Interaction i = e.getInteraction();
        String groupTag = DisplayUtils.getGroupTag(i);
        if (groupTag == null) return;
        Player p = e.getPlayer();
        SWArena swArena = SWArena.getArenaContainer(p, SWArena.class);
        if (swArena == null) return;
        SWProfile profile = swArena.getPlayerProfile(p, SWProfile.class);

        switch(groupTag){
            case "upgrade_weapon" -> {
                profile.getWeaponUpgrade().upgrade();
            }
            case "upgrade_armor" -> {
                profile.getArmorUpgrade().upgrade();
            }
            case "utility_shop" -> {
                UtilityShop.open(p);
            }
        }
    }
}
