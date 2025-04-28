package net.donnypz.shipwrecker.leaderboard;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.database.LeaderboardQuery;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class LeaderboardUpdater {

    private static final int THREE_MINUTES = 20*60*3;

    public static void start(){
        Bukkit.getScheduler().runTaskAsynchronously(Shipwrecker.getInstance(), () -> {
           updateWins(true);
           updateKills(true);
        });
    }

    public static void updateWins(boolean scheduleNext){
        updateLB("win_lb", LeaderboardQuery.getHighestWins(), LeaderboardQuery.WINS_FIELD);
        if (scheduleNext){
            Bukkit.getScheduler().runTaskLaterAsynchronously(Shipwrecker.getInstance(), () -> {
                updateWins(true);
            }, THREE_MINUTES);
        }

    }

    public static void updateKills(boolean scheduleNext){
        updateLB("kill_lb", LeaderboardQuery.getHighestKills(), LeaderboardQuery.KILLS_FIELD);
        if (scheduleNext){
            Bukkit.getScheduler().runTaskLaterAsynchronously(Shipwrecker.getInstance(), () -> {
                updateKills(true);
            }, THREE_MINUTES);
        }

    }

    private static void updateLB(String leaderboard, List<Document> documents, String field){
        if (DHAPI.getHologram(leaderboard) != null){
            Hologram holo = DHAPI.getHologram(leaderboard);


            for (int line = 1; line <= 10; line++){
                if (line > documents.size()){
                    DHAPI.setHologramLine(holo, line, ChatColor.GRAY+" - ");
                }
                else{
                    Document data = documents.get(line-1);
                    UUID uuid = UUID.fromString(data.getString("uuid"));
                    int fieldCount = data.getInteger(field);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    String playerName = player.getName();
                    if (playerName == null){
                        playerName = nameFromUUID(uuid);
                    }
                    DHAPI.setHologramLine(holo, line, ChatColor.GOLD+""+(line)+ChatColor.GRAY+") - "+ChatColor.AQUA+playerName+": "+ChatColor.RED+ fieldCount);
                }

            }
        }
    }

    private static String nameFromUUID(UUID uuid){
        return getFromURL(uuid.toString(), "username");
    }

    private static String getFromURL(String input, String search){
        try {
            URL url = new URL("https://playerdb.co/api/player/minecraft/user-agent/DONNY_PZ___TWITTER/"+input);
            BufferedReader inStream = new BufferedReader(new InputStreamReader(url.openStream()));
            String username = inStream.readLine();
            inStream.close();
            if (username.contains("minecraft.api_failure")){
                return "Failed to get username";
            }

            String find = username.split("\""+search+"\":\"")[1];
            find = find.split("\"")[0];
            return find;
        }
        catch (IOException e1) {
            e1.printStackTrace();
            return "Failed to get username";
        }
    }
}
