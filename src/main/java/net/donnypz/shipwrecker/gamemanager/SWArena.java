package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.mccore.minigame.arenaManager.Arena;
import net.donnypz.mccore.minigame.arenaManager.ArenaContainer;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SWArena extends ArenaContainer {

    WorldPositioner wp = new WorldPositioner(this);
    private static final int gameDuration = 300; //5min
    private int shipHealth = 100;
    KeyedBossBar shipHealthBar;
    BossBar distanceBar;
    Location spawnLoc;
    int shipSpeed = 1;
    int distanceRemaining = gameDuration;
    boolean isVictory = false;
    PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 20, 0);
    Date startTime = new Date();

    public SWArena(Arena arena) {
        super(arena, gameDuration);
    }

    @Override
    protected void onPlayersSentToArena() {
        wp.decorateBorders();
        wp.decorateExtra();
        wp.spawnFrontWall();

        this.countdown(10, 1, false);
        shipHealthBar = BossBarUtils.sendBossBar((Player) null, arena.getArenaWorldName()+"_health", ChatColor.YELLOW+"Ship Health", BarColor.GREEN, BarStyle.SEGMENTED_20);
        distanceBar = BossBarUtils.sendBossBar((Player) null, arena.getArenaWorldName()+"_distance", ChatColor.WHITE+"Distance Remaining", BarColor.BLUE, BarStyle.SOLID);
        shipHealthBar.setProgress(1);
        distanceBar.setProgress(1);
        //shipHealthBar.addFlag(BarFlag.CREATE_FOG); //Maybe make optional with packets
        World w = arena.getArenaAsBukkitWorld();
        spawnLoc = new Location(w, -42.5, 61, 4.5, -90, 0);
        for (Player p : arena.getArenaPlayers()){
            Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
                if (!arena.isEndingOrNotUsable()){
                    sendToSpawn(p);
                }
            },2);
            p.setGameMode(GameMode.ADVENTURE);
            //p.teleportAsync(spawnLoc);
            shipHealthBar.addPlayer(p);
            distanceBar.addPlayer(p);
            new SWProfile(p, this);
        }
    }

    void sendToSpawn(Player player){
        player.teleport(spawnLoc);
    }

    public void damageShip(int percentage){
        if (arena.isEndingOrNotUsable()){
            return;
        }
        shipHealth = Math.max(0, shipHealth-percentage);
        updateShipHealth();
        if (shipHealth == 0){
            arena.endGame(0, 20*5);
        }
    }

    public void repairShip(int percentage){
        if (arena.isEndingOrNotUsable()){
            return;
        }
        shipHealth = Math.min(100, shipHealth+percentage);
        updateShipHealth();
    }

    private void updateShipHealth(){
        if (shipHealthBar != null){
            shipHealthBar.setProgress(shipHealth/100.0);
        }
    }

    public void setShipSpeed(int speed){
        this.shipSpeed = speed;
        wp.updateActiveDisplaySpeed(speed);
    }

    public void revivePlayer(Player player){
        if (player.getGameMode() == GameMode.ADVENTURE){
            return;
        }

        sendToSpawn(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.addPotionEffect(blindness);

        Component reviveComp = player.name().color(NamedTextColor.YELLOW).append(Component.text(" has been revived!", NamedTextColor.GREEN));
        for (Player p : arena.getArenaPlayers()){
            p.sendMessage(reviveComp);
        }

        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> player.playEffect(EntityEffect.TOTEM_RESURRECT), 1); //PROTECTED_FROM_DEATH
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, spawnLoc.clone().add(0, 1, 0), 75,0.25,1,0.25,0.1);
    }

    public void eliminatePlayer(Player player){
        if (player.getGameMode() == GameMode.SPECTATOR){
            return;
        }
        getPlayerProfile(player).addDeath();

        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(blindness);

        Component diedComp = Component.text("☠ ", NamedTextColor.WHITE)
                .append(player.name().color(NamedTextColor.YELLOW))
                .append(Component.text(" has died!", NamedTextColor.RED));

        for (Player p : arena.getArenaPlayers()){
            p.sendMessage(diedComp);
            p.playSound(player, Sound.ENTITY_CREAKING_DEATH, 10f, 1.5f);
        }

        Component titleComp = Component.text("☠", NamedTextColor.WHITE)
                .append(Component.text(" YOU DIED", NamedTextColor.RED, TextDecoration.BOLD));

        player.sendMessage(titleComp);
        getPlayerProfile(player, SWProfile.class)
                .removeGold(100, true);

        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO);
        new BukkitRunnable(){
            int seconds = 10;
            @Override
            public void run() {
                if (seconds == 0){
                    revivePlayer(player);
                    cancel();
                    return;
                }
                if (arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                player.showTitle(Title.title(titleComp, Component.text("You will respawn in "+seconds+" seconds"), times));
                seconds--;

            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 20);

    }

    public void sendPriorityTitle(){

    }

    @Override
    protected void onCountdownEnd() {
        checkPlayerHeight();
        decrementDistance();
    }

    private void checkPlayerHeight(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if (arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                for (Player p : arena.getPlayingPlayers()){
                    if (p.getY() <= 55){
                        eliminatePlayer(p);
                    }
                }
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 15);
    }

    private void decrementDistance(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if (arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                if (distanceRemaining <= 0){
                    isVictory = true;
                    arena.endGame(0, 20*5);
                    return;
                }
                double currentDistance = (double) distanceRemaining /gameDuration;
                distanceBar.setProgress(Math.max(0, currentDistance));
                distanceRemaining-= shipSpeed;
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 20);
    }


    @Override
    protected void sendGameInfoMessage(Player player) {

    }

    @Override
    protected void onMatchTimeLeftChange() {

    }

    @Override
    protected void onArenaRemoval() {
        BossBarUtils.removeBossBar(shipHealthBar);
        BossBarUtils.removeBossBar(distanceBar);
        generateMatchDocument();
        wp.activeDisplays.clear();
    }

    //Insert a Document in the "matches" collection after a player's game ends
    private void generateMatchDocument(){
        List<String> playerUUIDs = new ArrayList<>();
        arena.getStartPlayers().forEach(p -> playerUUIDs.add(p.getUniqueId().toString()));

        Shipwrecker.getInstance().getMatchesCollection().insertOne(
                new Document("players", playerUUIDs)
                .append("match_won", isVictory)
                .append("start_time", startTime)
                .append("end_time", new Date()));
    }

    public WorldPositioner getWorldPositioner() {
        return wp;
    }
}
