package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.database.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class AddFakePlayers implements CommandExecutor {

    private static final String[] uuids = new String[]{
            "f7c77d99-9f15-4a66-a87d-c4a51ef30d19", //hypixel
            "77cc85ae-388a-46ec-a535-9e2ffef71b29", //damtdm
            "5f8eb73b-25be-4c5a-a50f-d27d65e30ca0", //grian
            "0c063bfd-3521-413d-a766-50be1d71f00e", //antvenom
            "c7da90d5-6a05-4217-b94a-7d427cbbcad8", //mumbojumbo
            "93b459be-ce4f-4700-b457-c1aa91b3b687", //etho
            "069a79f4-44e9-4726-a5be-fca90e38aaf5", //notch
            "9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e", //slicedlime
            "853c80ef-3c37-49fd-aa49-938b674adae6", //jeb_
    };
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }

        player.sendMessage(Component.text("Generating 9 Player Documents with random match statistics!", NamedTextColor.GREEN));
        Random random = new Random();
        for (String s : uuids){
            UUID uuid = UUID.fromString(s);
            DataManager.generatePlayerDocument(uuid, true, false);

            player.sendMessage(Component.text("| "+uuid, NamedTextColor.GRAY)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.YELLOW)))
                    .clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
            DataManager.generateFakeMatchDocuments(uuid, random.nextInt(10)+1);
        }

        player.sendMessage(Component.text("Done!", NamedTextColor.GREEN));
        player.sendMessage(Component.text("| This command should only be ran once to populate database collections", NamedTextColor.GRAY));
        return true;
    }
}
