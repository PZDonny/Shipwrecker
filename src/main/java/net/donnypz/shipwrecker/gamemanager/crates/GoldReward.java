package net.donnypz.shipwrecker.gamemanager.crates;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Random;

class GoldReward implements Reward{

    int startBound;
    int endBound;
    static final Random random = new Random();

    GoldReward(int startBound, int endBound){
        this.startBound = startBound;
        this.endBound = endBound;
    }
    @Override
    public GoldReward giveReward(Player player, SWArena swArena) {
        SWProfile profile = swArena.getPlayerProfile(player, SWProfile.class);
        profile.addGold(random.nextInt(startBound, endBound), true, true);
        return this;
    }

    @Override
    public Component message() {
        return null;
    }
}
