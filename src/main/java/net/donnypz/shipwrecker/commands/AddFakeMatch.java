package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.database.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddFakeMatch implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }
        if (args.length == 0){
            incorrectUsage(player);
            return true;
        }

        try{
            int matches = Integer.parseInt(args[0]);
            DataManager.generateFakeMatchDocuments(player.getUniqueId(), matches);
            player.sendMessage(Component.text(matches+" fake match document(s) generated!", NamedTextColor.GREEN));
        }
        catch(NumberFormatException e){
            incorrectUsage(player);
        }
        return true;
    }

    private void incorrectUsage(Player player){
        player.sendMessage(Component.text("Incorrect Usage! /addfakematch <match-count>", NamedTextColor.RED));
    }
}
