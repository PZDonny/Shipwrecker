package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddGold implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player p)) {
            return true;
        }
        SWArena swArena = SWArena.getArenaContainer(p, SWArena.class);
        if (swArena == null){
            return true;
        }

        SWProfile profile = swArena.getPlayerProfile(p, SWProfile.class);
        if (profile == null){
            return true;
        }

        try{
            profile.addGold(Integer.parseInt(args[0]), true, true);
        }
        catch(NumberFormatException e){
            p.sendMessage(Component.text("Enter a valid number!", NamedTextColor.RED));
        }

        return true;
    }
}
