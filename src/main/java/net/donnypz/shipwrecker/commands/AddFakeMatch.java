package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.Shipwrecker;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class AddFakeMatch implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }

        Date start = new Date(System.currentTimeMillis()-392714L);
        Date end = new Date();
        Shipwrecker.getInstance().getMatchesCollection().insertOne(
                new Document("players", List.of(player.getUniqueId().toString()))
                        .append("match_won", (args.length > 0 && args[0].equals("win")))
                        .append("start_time", start)
                        .append("end_time", end));

        return true;
    }
}
