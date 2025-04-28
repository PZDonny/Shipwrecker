package net.donnypz.shipwrecker.listeners;

import net.donnypz.mccore.database.PlayerData;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.database.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(Shipwrecker.getInstance(), () -> DataManager.generatePlayerDocument(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        PlayerData.remove(player);
    }
}
