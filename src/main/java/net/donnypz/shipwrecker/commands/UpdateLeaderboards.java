package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.database.LeaderboardUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UpdateLeaderboards implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.text("Updating leaderboards..."));
        Bukkit.getScheduler().runTaskAsynchronously(Shipwrecker.getInstance(), () -> {
            LeaderboardUpdater.updateKills(false);
            LeaderboardUpdater.updateWins(false);
            sender.sendMessage(Component.text("Done!", NamedTextColor.AQUA));
        });
        return true;
    }
}
