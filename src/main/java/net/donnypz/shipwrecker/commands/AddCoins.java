package net.donnypz.shipwrecker.commands;

import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.database.PlayerDatabaseUpdate;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddCoins implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }

        try{
            int coins = Integer.parseInt(args[0]);
            player.sendMessage(Component.text("Updating coins in player's MongoDB document...", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("| Expecting +"+coins+" coins", NamedTextColor.GRAY));
            Document playerDoc = PlayerData.get(player).getDocument();
            new PlayerDatabaseUpdate(Shipwrecker.getInstance().getPlayersCollection(), player.getUniqueId())
                    .incrementValue(Shipwrecker.COINS, coins)
                    .update(playerDoc);
            Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
                PlayerScoreboard sb = ScoreboardUtils.getPlayerScoreboard(player);
                if (sb != null){
                    sb.updateValue("coins", Component.text(playerDoc.getInteger("coins"), NamedTextColor.YELLOW));
                }
            },10);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(Component.text("Enter a valid number!", NamedTextColor.RED));
        }

        return true;
    }
}
