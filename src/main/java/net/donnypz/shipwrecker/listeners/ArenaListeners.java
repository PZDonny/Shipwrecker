package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.events.ArenaCreatedEvent;
import net.donnypz.mccore.events.ArenaWorldGeneratedEvent;
import net.donnypz.mccore.events.ArenaStateChangeEvent;
import net.donnypz.mccore.events.PlayerRemovedFromArenaEvent;
import net.donnypz.mccore.minigame.ArenaState;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.donnypz.shipwrecker.ui.scoreboard.LobbyScoreboard;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListeners implements Listener {


    @EventHandler
    public void onArenaCreated(ArenaCreatedEvent e){
        Arena arena = e.getArena();
        arena.generateArenaWorld(arena.getArenaSettings().mapName(), true);
        arena.setCountdownDuration(1);
    }

    @EventHandler
    public void onArenaWorldGenerated(ArenaWorldGeneratedEvent e){
        World w = e.getArena().getArenaAsBukkitWorld();
        w.setTime(18000);
        new SWArena(e.getArena());
    }

    @EventHandler
    public void onArenaStateChange(ArenaStateChangeEvent e){
        ArenaState newState = e.getState();
        if (newState == ArenaState.ENDING){
            e.getArenaContainer(SWArena.class)
                    .getWorldPositioner()
                    .useEndgameMaterials();
        }
        if (newState == ArenaState.DELETED){
            SWArena sw = e.getArenaContainer(SWArena.class);
            if (!sw.isStarted()) return;
            for (SWProfile profile : sw.getPlayerProfiles(SWProfile.class)){
                profile.updatePlayerData();
            }
            for (Player p : e.getArena().getArenaPlayers()){
                LobbyScoreboard.create(p);
            }
        }
    }

    @EventHandler
    public void onPlayerLeaveArena(PlayerRemovedFromArenaEvent e){
        LobbyScoreboard.create(e.getPlayer());
    }
}
