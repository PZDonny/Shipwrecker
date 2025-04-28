package net.donnypz.shipwrecker.listeners;

import com.mongodb.client.MongoDatabase;
import net.donnypz.mccore.database.MongoUtils;
import net.donnypz.mccore.events.MongoConnectedEvent;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MongoConnectedListener implements Listener {

    @EventHandler
    public void onMongoConnected(MongoConnectedEvent e){
        MongoDatabase db = MongoUtils.registerDatabase("shipwrecker");
        Shipwrecker shipwrecker = Shipwrecker.getInstance();
        shipwrecker
                .setPlayersCollection(MongoUtils.getCollection("players", db))
                .setMatchesCollection(MongoUtils.getCollection("matches", db))
                .setKillEffectCollection(MongoUtils.getCollection("cosmetics_kill_effects", db))
                .setProjectileTrailCollection(MongoUtils.getCollection("cosmetics_projectile_trails", db));
    }
}
