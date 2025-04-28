package net.donnypz.shipwrecker;

import com.mongodb.client.MongoCollection;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import net.donnypz.mccore.utils.misc.NoteBlockUtils;
import net.donnypz.shipwrecker.commands.*;
import net.donnypz.shipwrecker.cosmetics.killeffects._KillEffectRegistry;
import net.donnypz.shipwrecker.cosmetics.projectiletrails._ProjectileTrailRegistry;
import net.donnypz.shipwrecker.leaderboard.LeaderboardUpdater;
import net.donnypz.shipwrecker.listeners.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Random;

public final class Shipwrecker extends JavaPlugin implements Listener {

    MongoCollection<Document> playersCollection;
    MongoCollection<Document> matchesCollection;
    MongoCollection<Document> killEffectCollection;
    MongoCollection<Document> projectileTrailCollection;
    private static Shipwrecker instance;

    public static final String COINS = "coins"; //Currency Field
    public static final String EQUIPPED_COSMETICS = "equipped_cosmetics"; //Nested Document Key
    private static Song[] songs;

    public static final TextComponent prefixLong = Component.text("------------=", NamedTextColor.GRAY, TextDecoration.BOLD)
            .append(Component.text("sʜɪᴘᴡʀᴇᴄᴋᴇʀ", NamedTextColor.AQUA))
            .append(Component.text("=------------", NamedTextColor.GRAY, TextDecoration.BOLD));

    //Cosmetic (Unlockable) Registries
    _KillEffectRegistry killEffectRegistry = new _KillEffectRegistry();
    _ProjectileTrailRegistry projectileTrailRegistry = new _ProjectileTrailRegistry();



    @Override
    public void onEnable() {
        instance = this;

        //Register Commands
        getCommand("startmatch").setExecutor(new StartMatch());
        getCommand("endmatch").setExecutor(new EndMatch());
        getCommand("addgold").setExecutor(new AddGold());
        getCommand("addcoins").setExecutor(new AddCoins());
        getCommand("addfakematch").setExecutor(new AddFakeMatch());
        getCommand("addfakeplayers").setExecutor(new AddFakePlayers());
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("shipspeed").setExecutor(new ShipSpeed());
        getCommand("updatelb").setExecutor(new UpdateLeaderboards());

        //Register Event Listeners
        getServer().getPluginManager().registerEvents(new ArenaListeners(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new EntityTargetListener(), this);
        getServer().getPluginManager().registerEvents(new InteractionListener(), this);
        getServer().getPluginManager().registerEvents(new MongoConnectedListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

        setSongs();
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e){
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD){
            return;
        }
        LeaderboardUpdater.start();
    }

    public Shipwrecker setPlayersCollection(MongoCollection<Document> collection){
        this.playersCollection = collection;
        return this;
    }

    public Shipwrecker setMatchesCollection(MongoCollection<Document> collection){
        this.matchesCollection = collection;
        return this;
    }

    public Shipwrecker setKillEffectCollection(MongoCollection<Document> collection){
        this.killEffectCollection = collection;
        this.killEffectRegistry.setUnlockCollection(collection);
        return this;
    }

    public Shipwrecker setProjectileTrailCollection(MongoCollection<Document> collection){
        this.projectileTrailCollection = collection;
        this.projectileTrailRegistry.setUnlockCollection(collection);
        return this;
    }

    private void setSongs(){
        songs = new Song[]{
                getSong("Castle Crashers - The Necromancer"),
                getSong("MAGO - GFRIEND - FIX"),
                getSong("Paramore - Ain't It Fun 7TPS"),

        };
    }

    private Song getSong(String songName){
        String path = "songs/";
        return NoteBlockUtils.getSongFromPlugin(this, path+songName);
    }


    public static Playlist buildPlaylist(){
        Random random = new Random();
        Song[] shuffle = Arrays.copyOf(songs, songs.length);
        for (int i = shuffle.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Song temp = shuffle[i];
            shuffle[i] = shuffle[j];
            shuffle[j] = temp;
        }
        return new Playlist(shuffle);
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
