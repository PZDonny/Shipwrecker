package net.donnypz.shipwrecker.commands;

import net.donnypz.playerdbutils.database.PlayerDatabaseUpdater;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            new PlayerDatabaseUpdater(Shipwrecker.getInstance().getPlayersCollection(), player.getUniqueId())
                    .incrementValue(Shipwrecker.COINS, coins)
                    .update();
        }
        catch(IllegalArgumentException e){
            player.sendMessage(Component.text("Enter a valid number!"));
        }

        return true;
    }
}
