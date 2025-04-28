package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.MusicArenaContainer;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.database.DataManager;
import net.donnypz.shipwrecker.gamemanager.enemy.EnemySpawner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.Date;
import java.util.Random;

public class SWArena extends MusicArenaContainer {
    private static final int gameDuration = 180; //3min

    BossBar distanceBar;
    Location spawnLoc;
    int shipSpeed = 1;
    int distanceRemaining = gameDuration;
    WorldPositioner wp = new WorldPositioner(this);
    EnemySpawner enemySpawner;
    boolean isVictory = false;
    Date startTime = new Date();

    public SWArena(Arena arena) {
        super(arena, gameDuration);
    }

    @Override
    protected void onPlayersSentToArena() {
        this.countdown(5, 1, false);
        distanceBar = BossBarUtils.sendBossBar((Player) null, arena.getArenaWorldName()+"_distance", "", BarColor.BLUE, BarStyle.SOLID);
        setShipSpeed(1);
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
            distanceBar.addPlayer(p);
            new SWProfile(p, this);
        }

        wp.decorateBorders();
        wp.decorateFish();
        wp.spawnFrontWall();
    }

    void sendToSpawn(Player player){
        player.teleport(spawnLoc);
    }

    public void setShipSpeed(int speed){
        this.shipSpeed = speed;
        distanceBar.setTitle(ChatColor.WHITE+"Distance Remaining (Bar) | "+ChatColor.AQUA+"Speed: "+speed);
    }

    public int getShipSpeed() {
        return shipSpeed;
    }

    @Override
    protected void onCountdownEnd() {
        watchEntityHeight();
        decrementDistance();
        enemySpawner = new EnemySpawner(this);


        //Spawn Crates
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
            if (arena.isEndingOrNotUsable()) return;
            wp.spawnCrate(true);
        }, new Random().nextInt(20)*20);


        Title title = Title.title(Component.text("SURVIVE", NamedTextColor.AQUA, TextDecoration.BOLD),
                Component.text("Reach the end to win!", NamedTextColor.YELLOW),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(1)));
        for (Player p : arena.getArenaPlayers()){
            p.playSound(p, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 1, 1.5f);
            p.showTitle(title);
            getPlayerProfile(p, SWProfile.class).goldPerSecond();
        }

        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
            if (arena.isEndingOrNotUsable()) return;
            playRadio(Shipwrecker.buildPlaylist(), 20, false);
            this.radio.setCategory(com.xxmicloxx.NoteBlockAPI.model.SoundCategory.RECORDS);
        }, 5);
    }

    private void watchEntityHeight(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if (arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                for (Entity e : arena.getArenaAsBukkitWorld().getEntities()){
                    if (e instanceof LivingEntity le){
                        if (le.getY() > 55){
                            continue;
                        }
                        if (le instanceof Player p){
                                LifeManager.setSpectator(p, SWArena.this);
                        }
                        else if (!le.isDead()){
                            le.damage(99999);
                        }
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
                    endGame();
                    return;
                }
                double currentDistance = (double) distanceRemaining /gameDuration;
                distanceBar.setProgress(Math.max(0, currentDistance));
                distanceRemaining-= shipSpeed;
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 20);
    }

    @Override
    protected void sendGameInfoMessage(Player player) {}

    @Override
    protected void onMatchTimeLeftChange() {

    }

    @Override
    protected void onArenaRemoval() { //Runs at game's completion
        BossBarUtils.removeBossBar(distanceBar);
        DataManager.generateMatchDocument(this);
    }

    public WorldPositioner getWorldPositioner() {
        return wp;
    }

    public Date getStartTime(){
        return startTime;
    }

    public boolean isVictory() {
        return isVictory;
    }

    public void endGame(){
        if (arena.isEndingOrNotUsable()) return;

        arena.endGame(0, 20*5);
        Title title;
        Sound sound;
        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(4), Duration.ofSeconds(1));
        if (isVictory){
            title = Title.title(MiniMessage.miniMessage().deserialize("<bold><gold>🏆 <green> ʏᴏᴜ ᴡɪɴ"), Component.empty(), times);
            sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        }
        else{
            title = Title.title(MiniMessage.miniMessage().deserialize("<white>☠ <red><bold>ɢᴀᴍᴇ ᴏᴠᴇʀ"), Component.empty(), times);
            sound = Sound.BLOCK_BEACON_DEACTIVATE;
        }
        pauseRadio();

        for (Player p : arena.getArenaPlayers()){
            p.setInvulnerable(true);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.showTitle(title);
            p.playSound(p, sound, 1, 1);
        }
    }
}
