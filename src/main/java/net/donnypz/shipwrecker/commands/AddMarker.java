package net.donnypz.shipwrecker.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddMarker implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(commandSender instanceof Player p)){
            return true;
        }
        if (args.length == 0){
            sendValidTypes(p);
            return true;
        }
        String type = args[0].toLowerCase();
        switch(type){
            case "boarding", "laser", "falling" -> {
                Location loc = p.getLocation();
                loc.setPitch(0);
                loc.setYaw(0);
                loc.getWorld().spawn(loc, ArmorStand.class, a -> {
                    a.setMarker(true);
                    a.setPersistent(true);
                    a.setInvisible(true);
                    a.setInvulnerable(true);
                    a.setCanTick(false);
                });
            }
            case "show" -> {
                //show markers with particles
            }
            default -> {
                sendValidTypes(p);
                return true;
            }
        }

        return true;
    }

    private void sendValidTypes(Player p){
        p.sendMessage(Component.text("Provide a valid type!", NamedTextColor.RED));
        p.sendMessage(Component.text("- boarding", NamedTextColor.GRAY));
        p.sendMessage(Component.text("- laser", NamedTextColor.GRAY));
        p.sendMessage(Component.text("- falling", NamedTextColor.GRAY));
    }
}
