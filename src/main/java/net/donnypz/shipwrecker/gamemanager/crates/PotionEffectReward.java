package net.donnypz.shipwrecker.gamemanager.crates;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Random;

class PotionEffectReward implements Reward{

    PotionEffect[] effects;

    PotionEffectReward(PotionEffect... potionEffects){
        this.effects = potionEffects;
    }

    @Override
    public PotionEffectReward giveReward(Player player, SWArena swArena) {
        for (PotionEffect effect : effects){
            player.addPotionEffect(effect);
        }
        player.playSound(player, Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1f);
        return this;
    }

    @Override
    public Component message() {
        int i = 0;
        Component comp = Component.empty();
        do{
            PotionEffect effect = effects[i];
            String key = effect.getType().getKey().getKey();

            comp = comp.append(Component.text("| "+key.substring(0, 1).toUpperCase()+key.substring(1)+" ("+effect.getDuration()/20+"s)", NamedTextColor.YELLOW))
                    .appendNewline();
            i++;
        } while(i < effects.length);
        return comp;
    }
}
