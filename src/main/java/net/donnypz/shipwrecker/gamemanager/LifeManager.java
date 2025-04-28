package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class LifeManager {

    private static final int GOLD_REMOVAL_ON_DEATH = 100;
    static final PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 20, 0);

    public static void setSpectator(Player player, SWArena swArena){
        if (player.getGameMode() == GameMode.SPECTATOR){
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(blindness);

        Component diedComp = Component.text("☠ ", NamedTextColor.WHITE)
                .append(player.name().color(NamedTextColor.YELLOW))
                .append(Component.text(" has died!", NamedTextColor.RED));

        for (Player p : swArena.getArena().getArenaPlayers()){
            p.sendMessage(diedComp);
            p.playSound(player, Sound.ENTITY_CREAKING_DEATH, 10f, 1.5f);
        }

        swArena.getPlayerProfile(player, SWProfile.class).addDeath();

        if (swArena.getArena().isEndingOrNotUsable()){
            return;
        }


        Component titleComp = Component.text("☠", NamedTextColor.WHITE)
                .append(Component.text(" YOU DIED", NamedTextColor.RED, TextDecoration.BOLD));

        player.sendMessage(titleComp);
        swArena.getPlayerProfile(player, SWProfile.class)
                .removeGold(GOLD_REMOVAL_ON_DEATH, true, true);

        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO);
        new BukkitRunnable(){
            int seconds = 5;
            @Override
            public void run() {
                if (seconds == 0){
                    revivePlayer(player, swArena);
                    cancel();
                    return;
                }
                if (swArena.getArena().isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                player.showTitle(Title.title(titleComp, Component.text("You will respawn in "+seconds+" seconds"), times));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 0.75f);
                seconds--;

            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 20);
    }

    static void revivePlayer(Player player, SWArena swArena){
        if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) return;

        swArena.sendToSpawn(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.addPotionEffect(blindness);

        Component reviveComp = player.name().color(NamedTextColor.YELLOW).append(Component.text(" has been revived!", NamedTextColor.GREEN));
        for (Player p : swArena.getArena().getArenaPlayers()){
            p.sendMessage(reviveComp);
        }

        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> player.playEffect(EntityEffect.TOTEM_RESURRECT), 1); //PROTECTED_FROM_DEATH
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, swArena.spawnLoc.clone().add(0, 1, 0), 75,0.25,1,0.25,0.1);
    }
}
