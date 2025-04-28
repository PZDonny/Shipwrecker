package net.donnypz.shipwrecker.gamemanager.enemy;

import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EnemySpawner {

    private static final BlockData prismarineData = Material.PRISMARINE.createBlockData();
    private static final BlockData seaGrassData = Material.TALL_SEAGRASS.createBlockData();
    private static final int ZOMBIE_COUNT = 3;

    private final Random random = new Random();
    private final SWArena swArena;

    public EnemySpawner(SWArena swArena){
        this.swArena = swArena;
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), this::spawn, 60);
    }


    void spawn(){
        if (swArena.getArena().isEndingOrNotUsable()){
            return;
        }
        for (int i = 0; i < ZOMBIE_COUNT; i++){
            createZombie();
        }

        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), this::spawn, (random.nextInt(13, 16)+1)* 20L);
    }

    private void createZombie(){
        Location spawnLoc = PointHolder.getRandomRisePoint(swArena);
        World w  = spawnLoc.getWorld();

        Drowned drowned = w.spawn(spawnLoc.clone().subtract(0, 1.5, 0), Drowned.class, d -> {
            d.setInvulnerable(true);
            d.setGravity(false);
            d.setGlowing(true);
            d.setAdult();
        });
        for (Player p : swArena.getArena().getArenaPlayers()){
            PlayerScoreboard sb = ScoreboardUtils.getPlayerScoreboard(p);
            if (sb != null){
                sb.addEntityToOtherTeam("enemy", drowned);
            }
        }
        Bukkit.getScheduler().runTask(Shipwrecker.getInstance(), () -> { //Prevent Jockey
            Entity vehicle = drowned.getVehicle();
            if (vehicle != null){
                vehicle.remove();
            }
        });

        w.playSound(spawnLoc, Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 0.75f);
        new BukkitRunnable(){
            final double y = spawnLoc.getY();
            @Override
            public void run() {
                if (drowned.isDead()){
                    cancel();
                    return;
                }
                if (y <= drowned.getY()){
                    drowned.setInvulnerable(false);
                    drowned.setGravity(true);
                    drowned.getEquipment().setItemInMainHand(null);
                    EnemyUtils.pathFindToRandomPlayer(swArena, drowned, true);
                    cancel();
                    return;
                }
                w.spawnParticle(Particle.BLOCK, spawnLoc, 30, 0.2, 0.5, 0.25, 0, prismarineData);
                w.spawnParticle(Particle.BLOCK, spawnLoc, 30, 0.25, 0.5, 0.25, 0, seaGrassData);
                w.playSound(spawnLoc, Sound.BLOCK_ROOTED_DIRT_BREAK, 0.2f, 0.75f);
                drowned.teleport(drowned.getLocation().clone().add(0, 0.1, 0));
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 2);
    }
}
