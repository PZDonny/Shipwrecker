package net.donnypz.shipwrecker.listeners;

import com.mongodb.client.MongoCollection;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class PlayerConnectionListener implements Listener {
    //"Schema" Document with same starting data for every player
    private static final Document playerDataSchema = new Document()
            .append(Shipwrecker.COINS, 0)
            .append("kills_melee", 0)
            .append("kills_projectile", 0)
            .append("matches_played", 0)
            .append("matches_won", 0)
            .append("equipped_cosmetics",
                new Document("kill_effect", null)
                .append("projectile_trail", null)
                .append("ship_skin", null)
                .append("armor_trim_pattern", null)
                .append("armor_trim_material", null));

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        MongoCollection<Document> playerCollection = Shipwrecker.getInstance().getPlayersCollection();
        Document existingDoc = playerCollection.find(new Document("uuid", p.getUniqueId().toString())).first();

        //New Player
        if (existingDoc == null){
            Document playerDoc = new Document("uuid", p.getUniqueId().toString())
                    .append("creation_time", new Date());
            playerDoc.putAll(playerDataSchema);

            //Send new Document to database
            Shipwrecker.getInstance().getPlayersCollection().insertOne(playerDoc);
            Bukkit.getConsoleSender().sendMessage(Component.text("Generated MongoDB document for player", NamedTextColor.RED));
        }
        //Existing Player
        else{
            Bukkit.getConsoleSender().sendMessage(Component.text("Player has an existing MongoDB document", NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
    }
}
