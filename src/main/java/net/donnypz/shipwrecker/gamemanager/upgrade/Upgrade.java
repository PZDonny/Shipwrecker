package net.donnypz.shipwrecker.gamemanager.upgrade;

import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public abstract class Upgrade {
    protected SWProfile profile;

    Upgrade(SWProfile profile, Player player){
        this.profile = profile;
    }

    public SWProfile getProfile(){
        return profile;
    }

    protected boolean canUpgrade(){
        if (profile.getGold() < 500){
            Player p = profile.getPlayer().getPlayer();
            p.sendMessage(Component.text("You do not have enough Gold to upgrade!", NamedTextColor.RED));
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return false;
        }
        return true;
    }
}
