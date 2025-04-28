package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.mccore.utils.inventory.gui.InventoryUtils;
import net.donnypz.mccore.utils.item.ItemAction;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.item.ItemUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

public class UtilityShop {
    static ChestGUI gui;

    static{
        gui = new ChestGUI(6, Component.text("Utility Shop"));
        InventoryUtils.setInventoryOutline(gui, Material.YELLOW_STAINED_GLASS_PANE, InventoryUtils.OutlineType.TOPROW);
        InventoryUtils.setExitItemSlot(gui, 49);
        gui.removeOnClose(false);

        //Food
        createItem(11, 30, new ItemBuilder(Material.DRIED_KELP).setAmount(4).build());
        createItem(12, 50, new ItemStack(Material.APPLE));
        createItem(13, 100, new ItemBuilder(Material.SALMON).setAmount(2).build());
        createItem(14, 150, new ItemStack(Material.BREAD));
        createItem(15, 250, new ItemStack(Material.GOLDEN_APPLE));

        //Utility
        shipSpeedBoost(30, 400, new ItemBuilder(Material.RABBIT_FOOT)
                .setDisplayName(Component.text("+1 Ship Speed", NamedTextColor.AQUA))
                .build());

        createItem(31, 100, new ItemBuilder(Material.WIND_CHARGE).setAmount(3).build());

        createImpactItemAction();
        createItem(32, 250, getImpactTnT(1));
    }

    public static void open(Player player){
        gui.openToPlayer(player);
    }

    public static ItemStack getImpactTnT(int amount){
        return new ItemBuilder(Material.TNT)
                .setDisplayName(Component.text("Impact TnT", NamedTextColor.RED)
                        .append(Component.text(" (Click)", NamedTextColor.YELLOW)))
                .setItemAction("impact")
                .setAmount(amount)
                .build();
    }

    private static void createItem(int slot, int price, ItemStack item){
        ItemStack inventoryItem = item.clone();
        ItemUtils.addLore(inventoryItem, Component.text("Price: "+price, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        new GUIItem(gui, slot, inventoryItem, click -> {
            Player p = (Player) click.getWhoClicked();
            SWArena swArena = SWArena.getArenaContainer(p, SWArena.class);
            SWProfile profile = swArena.getPlayerProfile(p, SWProfile.class);
            if (profile.getGold() < price){
                p.sendMessage(Component.text("You do not have enough Gold to purchase that!", NamedTextColor.RED));
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
                p.closeInventory();
                return;
            }

            p.playSound(p, Sound.BLOCK_VAULT_INSERT_ITEM, 1, 1);
            p.sendMessage(Component.text("Utility Item Purchased!", NamedTextColor.GREEN));
            profile.removeGold(price, true, false);
            p.getInventory().addItem(item);
            p.closeInventory();
        });
    }

    private static void shipSpeedBoost(int slot, int price, ItemStack item){
        ItemStack inventoryItem = item.clone();
        ItemUtils.addLore(inventoryItem, Component.text("Price: "+price, NamedTextColor.GOLD));
        new GUIItem(gui, slot, inventoryItem, click -> {
            Player p = (Player) click.getWhoClicked();
            SWArena swArena = SWArena.getArenaContainer(p, SWArena.class);
            SWProfile profile = swArena.getPlayerProfile(p, SWProfile.class);
            if (profile.getGold() < price){
                p.sendMessage(Component.text("You do not have enough Gold to purchase that!", NamedTextColor.RED));
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
                p.closeInventory();
                return;
            }
            swArena.setShipSpeed(swArena.getShipSpeed()+1);
            p.sendMessage(Component.text("Ship Speed Increased! ("+swArena.getShipSpeed()+")", NamedTextColor.GREEN));
            profile.removeGold(price, true, false);
            p.playSound(p, Sound.BLOCK_BREWING_STAND_BREW, 1,0.75f);
            p.closeInventory();
        });
    }

    private static void createImpactItemAction(){
        new ItemAction("impact")
                .setAnyClickAction(result -> {
                    Player player = result.player();

                    result.itemStack().setAmount(result.itemStack().getAmount()-1);

                    Snowball snowball = player.launchProjectile(Snowball.class);

                    snowball.setVelocity(snowball.getVelocity().multiply(0.8));

                    ItemDisplay display = player.getWorld().spawn(snowball.getLocation(), ItemDisplay.class, d -> {
                        d.setItemStack(new ItemStack(Material.TNT));
                        d.setRotation(player.getYaw(), 0);
                    });
                    snowball.addPassenger(display);

                    Matrix4f matrix = new Matrix4f();
                    new BukkitRunnable() { //Particles and Sound
                        public void run() {
                            if (!snowball.isValid()){
                                display.remove();
                                cancel();
                                return;
                            }

                            display.setTransformationMatrix(matrix.rotateX(((float) Math.toRadians(90)) + 0.1F));
                            display.setInterpolationDelay(0);
                            display.setInterpolationDuration(3);

                            player.getWorld().playSound(snowball.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.5f, 0.8f);
                            snowball.getWorld().spawnParticle(Particle.DUST, snowball.getLocation(), 10, 0.15, 0.15, 0.15, new Particle.DustOptions(Color.RED, 1));
                        }
                    }.runTaskTimer(Shipwrecker.getInstance(), 0L, 3L);
                });
    }
}
