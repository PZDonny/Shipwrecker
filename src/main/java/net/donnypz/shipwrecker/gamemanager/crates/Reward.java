package net.donnypz.shipwrecker.gamemanager.crates;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Reward {

    Reward giveReward(Player player, SWArena swArena);

    Component message();
}
