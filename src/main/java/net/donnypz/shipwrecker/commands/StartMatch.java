package net.donnypz.shipwrecker.commands;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.mccore.minigame.arena.ArenaSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartMatch implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)){
            commandSender.sendMessage(Component.text("You can only run this command in-game!", NamedTextColor.RED));
            return true;
        }

        if (ArenaManager.getArenaOfPlayer(player) != null) {
            player.sendMessage(Component.text("You cannot run that command while in a match!", NamedTextColor.RED));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return true;
        }

        ArenaManager.refreshPlayer(player, GameMode.ADVENTURE, true, true);
        new Arena(null, Arena.ArenaType.SLIMEMANUAL,
                new ArenaSettings("shipwrecker", "normal", "sw_default", 1, 4, null))
            .addPlayerManual(player, true, false);
        return true;
    }
}
