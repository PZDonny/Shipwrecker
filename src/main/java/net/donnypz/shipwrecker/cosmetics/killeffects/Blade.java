package net.donnypz.shipwrecker.cosmetics.killeffects;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.cosmeticConditions.FieldMinimumCondition;
import net.donnypz.mccore.cosmetics.preset.basic.KillEffect;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Blade extends KillEffect {

    private static final int TELEPORT_DURATION = 10;

    public Blade(CosmeticRegistry registry) {
        super("blade", registry);
        this.addCondition(new FieldMinimumCondition("matches_played", "matches played", 1));
        this.setCosmeticDisplayName("Bladed");
        this.setDisplayMaterial(Material.IRON_SWORD);
    }

    @Override
    public void playKillEffect(Player killer, LivingEntity victim) {
        Location loc = victim.getLocation().add(0, 3, 0);
        loc.setPitch(0);
        ItemDisplay display = loc.getWorld().spawn(loc, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.IRON_SWORD));
            d.setBrightness(new Display.Brightness(15, 15));
            d.setTeleportDuration(TELEPORT_DURATION);
            d.setBillboard(Display.Billboard.VERTICAL);
            Transformation t = d.getTransformation();
            d.setTransformation(new Transformation(t.getTranslation(), new Quaternionf(0, 0, 0.953f, 0.302f), new Vector3f(1.25f, 1.25f, 1.25f), t.getRightRotation()));
        });
        loc.getWorld().playSound(loc, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM, 1, 0.75f);

        display.teleport(loc.subtract(0, 3, 0));
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 50, 0.25, 0.1, 0.25, 0);
            loc.getWorld().spawnParticle(Particle.SMOKE, loc.clone().add(0, 0.1, 0), 5, 0.15, 0.1, 0.15, 0.1);
            loc.getWorld().playSound(loc, Sound.BLOCK_VAULT_PLACE, 1, 0.5f);
        }, TELEPORT_DURATION);

        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
            if (display.isValid()){
                display.remove();
            }
        }, 20*7);

    }
}
