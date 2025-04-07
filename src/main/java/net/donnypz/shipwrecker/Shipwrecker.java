package net.donnypz.shipwrecker;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.donnypz.playerdbutils.database.MongoUtils;
import net.donnypz.shipwrecker.commands.*;
import net.donnypz.shipwrecker.cosmetics.killeffects._KillEffectRegistry;
import net.donnypz.shipwrecker.cosmetics.projectiletrails._ProjectileTrailRegistry;
import net.donnypz.shipwrecker.listeners.ArenaListeners;
import net.donnypz.shipwrecker.listeners.Death;
import net.donnypz.shipwrecker.listeners.PlayerConnectionListener;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Shipwrecker extends JavaPlugin implements Listener {

    MongoCollection<Document> playersCollection;
    MongoCollection<Document> matchesCollection;
    MongoCollection<Document> killEffectCollection;
    MongoCollection<Document> projectileTrailCollection;
    private static Shipwrecker instance;
    public static final String COINS = "coins";
    public static final String EQUIPPED_COSMETICS = "equipped_cosmetics";

    _KillEffectRegistry killEffectRegistry = new _KillEffectRegistry();
    _ProjectileTrailRegistry projectileTrailRegistry = new _ProjectileTrailRegistry();



    @Override
    public void onEnable() {
        instance = this;

        //Commands
        getCommand("startgame").setExecutor(new StartGame());
        getCommand("addmarker").setExecutor(new AddMarker());
        getCommand("addgold").setExecutor(new AddGold());
        getCommand("addcoins").setExecutor(new AddCoins());
        getCommand("addfakematch").setExecutor(new AddFakeMatch());
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("shipspeed").setExecutor(new ShipSpeed());

        //Listeners
        getServer().getPluginManager().registerEvents(new ArenaListeners(), this);
        getServer().getPluginManager().registerEvents(new Death(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(this, this);


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerStart(ServerLoadEvent e){
        MongoDatabase db = MongoUtils.registerDatabase("shipwrecker");
        playersCollection = MongoUtils.getCollection("players", db);
        matchesCollection = MongoUtils.getCollection("matches", db);
        killEffectCollection = MongoUtils.getCollection("cosmetics_kill_effects", db);
        projectileTrailCollection = MongoUtils.getCollection("cosmetics_projectile_trails", db);
        /*new BukkitRunnable(){
            int attempts = 0;
            @Override
            public void run() {
                if (attempts == 100){
                    Bukkit.broadcast(Component.text("Failed to get MongoDB collections after 100 attempts!"));
                    cancel();
                    return;
                }

                if (MongoUtils.isConnected()){

                    cancel();
                    return;
                }
                attempts++;

            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 5);*/
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Shipwrecker getInstance(){
        return instance;
    }

    public MongoCollection<Document> getPlayersCollection() {
        return playersCollection;
    }

    public MongoCollection<Document> getMatchesCollection() {
        return matchesCollection;
    }

    public MongoCollection<Document> getKillEffectCollection() {
        return killEffectCollection;
    }

    public MongoCollection<Document> getProjectileTrailCollection() {
        return projectileTrailCollection;
    }

    public _KillEffectRegistry getKillEffectRegistry() {
        return killEffectRegistry;
    }

    public _ProjectileTrailRegistry getProjectileTrailRegistry(){
        return projectileTrailRegistry;
    }

    public static Object getSelectedCosmetic(Document document, String fieldName){
        Document nestedDoc = document.get(EQUIPPED_COSMETICS, Document.class);
        return nestedDoc.getString(fieldName);
    }
}
