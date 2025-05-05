package net.donnypz.shipwrecker.database;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.database.DatabaseUpdate;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.database.PlayerDatabaseUpdate;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.donnypz.shipwrecker.ui.scoreboard.LobbyScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class DataManager {

    //"Schema" Document with same initial data for every player
    private static final Document playerDataSchema = new Document()
            .append(Shipwrecker.COINS, 0)
            .append("kills_melee", 0)
            .append("kills_projectile", 0)
            .append("crates_collected", 0)
            .append("deaths", 0)
            .append("matches_played", 0)
            .append("matches_won", 0)
            .append("equipped_cosmetics",
                    //Nested Document
                    new Document("kill_effect", null)
                            .append("projectile_trail", null));


    private DataManager() {}

    /**
     * Generate a real match document after a match ends
     * @param swArena the game arena where the match took place
     */
    public static void generateMatchDocument(@NotNull SWArena swArena){
        Arena arena = swArena.getArena();
        List<String> playerUUIDs = new ArrayList<>();
        arena.getStartPlayers().forEach(p -> playerUUIDs.add(p.getUniqueId().toString()));

        Shipwrecker.getInstance().getMatchesCollection().insertOne(
                new Document("players", playerUUIDs)
                        .append("match_won", swArena.isVictory()) //Boolean
                        .append("start_time", swArena.getStartTime()) //Date (At game start)
                        .append("end_time", new Date())); //Date (Now)
    }

    /**
     * Generate a fake match document for a player
     * @param playerUUID the player to generate the document for
     * @param count the number of matches to generate
     */
    public static void generateFakeMatchDocuments(@NotNull UUID playerUUID, int count){
        Random random = new Random();
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < count; i++){
            long subtract = random.nextInt(180000);
            Date start = new Date(System.currentTimeMillis()-subtract);
            Date end = new Date();

            documents.add(new Document("players", List.of(playerUUID.toString()))
                    .append("match_won", new Random().nextBoolean())
                    .append("start_time", start)
                    .append("end_time", end));
        }
        Shipwrecker.getInstance().getMatchesCollection().insertMany(documents);
    }

    /**
     * Generate fake match documents for a player
     * @param playerUUID the player to generate the document for
     * @param matchesWon the number of matches to generate as won
     * @param matchesLost the number of matches to generate as lost
     */
    private static void generateFakeMatchDocuments(@NotNull UUID playerUUID, int matchesWon, int matchesLost){
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < matchesWon; i++){
            documents.add(createMatchDocument(playerUUID, true));
        }
        for (int i = 0; i < matchesLost; i++){
            documents.add(createMatchDocument(playerUUID, false));
        }
        Shipwrecker.getInstance().getMatchesCollection().insertMany(documents);
    }

    private static Document createMatchDocument(UUID playerUUID, boolean won){
        long subtract = new Random().nextInt(180000);
        Date start = new Date(System.currentTimeMillis()-subtract);
        Date end = new Date();

        return new Document("players", List.of(playerUUID.toString()))
                .append("match_won", won)
                .append("start_time", start)
                .append("end_time", end);
    }

    /**
     * Generate a player data document after a player joins a server.
     * Cache the data if the player is still online after getting the document.
     * @param player
     */
    public static void generatePlayerDocument(@NotNull Player player){
        generatePlayerDocument(player.getUniqueId(), false, true);
    }

    /**
     * Generate a player data document with a player's UUID. Can be used for testing.
     * @param uuid the player's uuid
     * @param randomizeData if the document data should be randomized in addition to create randomize match documents
     * @param cache Whether the player data should be cached if the player is still online.
     */
    public static void generatePlayerDocument(@NotNull UUID uuid, boolean randomizeData, boolean cache){
        Bukkit.getScheduler().runTaskAsynchronously(Shipwrecker.getInstance(), () -> {
            String uuidString = uuid.toString();

            MongoCollection<Document> playerCollection = Shipwrecker.getInstance().getPlayersCollection();

            Document cacheDoc;
            Document existingDoc = playerCollection.find(new Document("uuid", uuidString)).first();

            //Player already has existing data
            if (existingDoc != null){
                cacheDoc = existingDoc;
            }
            else{
                Document newDoc = new Document("uuid", uuidString)
                        .append("creation_time", new Date());

                //Append data from playerDataSchema to the new player document
                newDoc.putAll(playerDataSchema);

                //Randomize Data
                if (randomizeData) randomizeData(newDoc, uuid);

                //Insert new Player Document into database
                playerCollection.insertOne(newDoc);
                Bukkit.getConsoleSender().sendMessage(Component.text("Generated MongoDB document for player: "+uuid, NamedTextColor.GREEN));
                cacheDoc = newDoc;
            }

            if (cache){
                Player player = Bukkit.getPlayer(uuid);
                if (player != null){
                    new PlayerData(player, cacheDoc);
                    Bukkit.getScheduler().runTask(Shipwrecker.getInstance(), () -> {
                        if (player.isConnected()){
                            LobbyScoreboard.create(player); //Send Sidebar/Scoreboard UI to player, since they're online
                        }
                    });
                }
            }
        });
    }

    private static void randomizeData(Document doc, UUID playerUUID){
        Random random = new Random();
        int matchesWon = random.nextInt(5)+1;
        int matchesPlayed = matchesWon+random.nextInt(5)+1;
        doc
                .append("kills_melee", random.nextInt(10))
                .append("kills_projectile", random.nextInt(10))
                .append("deaths", random.nextInt(10))
                .append("matches_won", matchesWon)
                .append("matches_played", matchesPlayed);

        generateFakeMatchDocuments(playerUUID, matchesWon, matchesPlayed-matchesWon);
    }

    /**
     * Update a player's document based on their performance in a match
     * @param profile the profile holding a player's statistics
     */
    public static void updatePlayerData(SWProfile profile){
        UUID uuid = profile.getPlayer().getUniqueId();
        DatabaseUpdate updater = new PlayerDatabaseUpdate(Shipwrecker.getInstance().getPlayersCollection(), uuid)
                .incrementValue("kills_melee", profile.getMeleeKills())
                .incrementValue("kills_projectile", profile.getProjectileKills())
                .incrementValue("deaths", profile.getDeaths())
                .incrementValue("matches_played", 1)
                .incrementValue(Shipwrecker.COINS, profile.getCurrencyEarned());

        if (((SWArena) profile.getArenaContainer()).isVictory()){
            updater.incrementValue("matches_won", 1);
        }

        PlayerData data = PlayerData.get(uuid);
        if (data != null){ //Send updates to database, and apply changes to cached document
            updater.update(PlayerData.get(uuid).getDocument());
        }
        else{ //Send updates to database (No cached document found)
            updater.update();
        }
    }
}
