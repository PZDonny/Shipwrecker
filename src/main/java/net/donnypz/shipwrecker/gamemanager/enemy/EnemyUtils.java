package net.donnypz.shipwrecker.gamemanager.enemy;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import org.bukkit.entity.*;

import java.util.List;
import java.util.Random;

public class EnemyUtils {
    public static void pathFindToRandomPlayer(SWArena swArena, Entity entity, boolean setTarget){
        List<Player> players = swArena.getArena().getPlayingPlayers();
        if (players.isEmpty()){
            return;
        }

        Player p = players.get(new Random().nextInt(players.size()));

        if (entity instanceof Mob mob){

//Pathfinds to players 30% faster if they're 13+ blocks away
            if (p.getLocation().distanceSquared(entity.getLocation()) > 13*13){
                mob.getPathfinder().moveTo(p.getLocation(), 1.3f);
            }
            else{
                mob.getPathfinder().moveTo(p.getLocation());
            }

            if (setTarget){
                mob.setTarget(p);
            }
        }
        else if (entity instanceof ShulkerBullet bullet){
            bullet.setTarget(p);
        }

    }
}
