package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.events.ArenaCreatedEvent;
import net.donnypz.mccore.events.ArenaWorldGeneratedEvent;
import net.donnypz.mccore.events.GameStateChangeEvent;
import net.donnypz.mccore.minigame.GameState;
import net.donnypz.mccore.minigame.arenaManager.Arena;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListeners implements Listener {

    @EventHandler
    public void onArenaCreated(ArenaCreatedEvent e){
        Arena arena = e.getArena();
        arena.generateArenaWorld(arena.getArenaSettings().mapName(), true);
        arena.setCountdownDuration(2);
    }

    @EventHandler
    public void onArenaWorldGenerated(ArenaWorldGeneratedEvent e){
        World w = e.getArena().getArenaAsBukkitWorld();
        w.setTime(18000);
        new SWArena(e.getArena());
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent e){
        GameState newState = e.getGameState();
        if (newState == GameState.ENDING){
            SWArena swArena = e.getArenaContainer(SWArena.class);
            swArena.getWorldPositioner().endGameCoverItems();
        }
        if (newState == GameState.DELETED){
            SWArena sw = e.getArenaContainer(SWArena.class);
            if (!sw.isStarted()) return;
            for (SWProfile profile : sw.getPlayerProfiles(SWProfile.class)){
                profile.updatePlayerData();
            }
        }
    }
}
