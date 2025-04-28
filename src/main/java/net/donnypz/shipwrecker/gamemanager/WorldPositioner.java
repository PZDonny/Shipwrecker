package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.gamemanager.crates.Crate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class WorldPositioner {

    SWArena swArena;
    Location environmentOrigin;
    Location shipOrigin;
    Location leftWallStart;
    Location rightWallStart;
    Location ceilingStart;
    Location floorStart;

    ItemStack ceilingItem = new ItemStack(Material.DARK_PRISMARINE);
    ItemStack floorItem = new ItemStack(Material.SAND);
    ItemStack wallItem = new ItemStack(Material.DARK_PRISMARINE);

    public static final Direction displayTranslation = Direction.WEST;

    static final int pickupOffset = 11;
    static final int sections = 5;
    static final float translationDistance = 180;
    public static final float translationDistanceSquared = translationDistance*translationDistance;
    static final long timeBetweenWalls = 60;
    public static final long translationTime = timeBetweenWalls*sections;


    static final float height = 60;
    static final float width = 100; //leftWallStartOffset+rightWallOffset
    static final float fishScale = 2.25f;

    static final Transformation wallTransformation = new Transformation(
            new Vector3f(0, 0,0),
            new Quaternionf(),
            new Vector3f(translationDistance/sections, height, 1),
            new Quaternionf());


    static final Transformation coverTransformation = new Transformation(
            new Vector3f(0, 0,0),
            new Quaternionf(),
            new Vector3f(translationDistance/sections, 1f, width),
            new Quaternionf());

    static final Transformation frontWallTransformation = new Transformation(
            new Vector3f(0, 0,0),
            new Quaternionf(),
            new Vector3f(1, height, width),
            new Quaternionf());


    static final ItemStack[] fish = new ItemStack[]{new ItemStack(Material.COD), new ItemStack(Material.SALMON), new ItemStack(Material.TROPICAL_FISH), new ItemStack(Material.PUFFERFISH)};

    static final Random random = new Random();

    WorldPositioner(SWArena swArena){
        this.swArena = swArena;
        World w  = swArena.getArena().getArenaAsBukkitWorld();
        environmentOrigin = new Location(w, 60, 65, 1);
        shipOrigin = new Location(w, 0, 65, 0);

        leftWallStart = environmentOrigin.clone();
        leftWallStart.setZ(-50);

        rightWallStart = environmentOrigin.clone();
        rightWallStart.setZ(51);

        ceilingStart = environmentOrigin.clone();
        ceilingStart.setY(ceilingStart.y()+(height /2));

        floorStart = environmentOrigin.clone();
        floorStart.setY(floorStart.y()-(height/2));
    }

    void spawnFrontWall(){
        Location fwPoint = environmentOrigin.clone();
        fwPoint.setX(45);
        fwPoint.getWorld().spawn(fwPoint, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.BLACK_CONCRETE));
            d.setTransformation(frontWallTransformation);
        });
    }

    void decorateBorders(){
        final int shipSpeed = swArena.shipSpeed;
        new BukkitRunnable(){
            public void run(){
                if (!swArena.getArena().isUsable()){
                    cancel();
                    return;
                }

                if (shipSpeed != swArena.shipSpeed){
                    decorateBorders();
                    cancel();
                    return;
                }

                spawnWall(leftWallStart, true);
                spawnWall(rightWallStart, false);
                spawnCovers();
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 60/swArena.shipSpeed);
    }

    void decorateFish(){
        if (swArena.getArena().isEndingOrNotUsable()){
            return;
        }

        int fishCount = random.nextInt(5, 11)+1;
        World w = environmentOrigin.getWorld();
        for (int i = 0; i < fishCount; i++){
            int xOffset = random.nextInt(4);
            int yOffset = random.nextInt(-15, 15);
            int zOffset = random.nextInt(-35, 36);
            Location spawnLoc = environmentOrigin.clone();
            spawnLoc.setX(spawnLoc.x()+xOffset);
            spawnLoc.setY(spawnLoc.y()+yOffset);
            spawnLoc.setZ(spawnLoc.z()+zOffset);

            ItemDisplay display = w.spawn(spawnLoc, ItemDisplay.class, d -> {
                d.setItemStack(fish[random.nextInt(fish.length)]);
                float xRot = (float) Math.toRadians(random.nextInt(360));
                float yRot = (float) Math.toRadians(random.nextInt(360));
                float zRot = (float) Math.toRadians(random.nextInt(360));
                d.setTransformation(new Transformation(new Vector3f(), new Quaternionf()
                        .rotationXYZ(xRot, yRot, zRot), new Vector3f(fishScale), new Quaternionf()));
            });

            applyData(display, true, true);
        }
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
            decorateFish();
        }, (long) random.nextInt(2, 5)*20);
    }

    void spawnCrate(boolean scheduleNext){
        if (swArena.getArena().isEndingOrNotUsable()){
            return;
        }

        Component message;
        if (random.nextInt(5) == 0){ //20% chance for Special Crate
            Crate.spawnSpecial(swArena);
            message = MiniMessage.miniMessage().deserialize("\n<red><bold>❗</bold> <light_purple>SPECIAL <gold>SEA CRATE INCOMING\n");
        }
        else{
            Crate.spawnNormal(swArena);
            message = MiniMessage.miniMessage().deserialize("\n<red><bold>❗</bold> <gold>SEA CRATE INCOMING\n");
        }

        for (Player p : swArena.getArena().getArenaPlayers()){
            p.sendMessage(message);
            p.playSound(environmentOrigin, Sound.ENTITY_AXOLOTL_SPLASH, 10, 1.25f);
        }
        if (scheduleNext){
            Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
                spawnCrate(true);
            }, (long) random.nextInt(15, 45)*20);
        }

    }

    public Location getEnvironmentOrigin(){
        return environmentOrigin;
    }

    public Location getRandomPickupLocation(){
        Location l = environmentOrigin.clone();
        int yOffset = random.nextInt(-5, 5);
        int zOffset = random.nextBoolean() ? -(pickupOffset+1) : pickupOffset;

        l.setY(l.y()+yOffset);
        l.setZ(l.z()+zOffset);
        return l;
    }



    void spawnWall(Location loc, boolean isLeft){
        ItemDisplay wall = loc.getWorld().spawn(loc, ItemDisplay.class, d -> {
            d.setTransformation(getWallTransformation(isLeft));
            d.setItemStack(wallItem);
        });

        applyData(wall, true, true);
    }

    void spawnCovers(){
        World w = floorStart.getWorld();
        ItemDisplay ceiling = w.spawn(ceilingStart, ItemDisplay.class, d -> {
            d.setTransformation(getCoverTransformation(false));
            d.setItemStack(ceilingItem);
        });

        ItemDisplay floor = w.spawn(floorStart, ItemDisplay.class, d -> {
            d.setTransformation(getCoverTransformation(true));
            d.setItemStack(floorItem);
            d.setBrightness(new Display.Brightness(7, 7));
        });

        applyData(ceiling, true, true);
        applyData(floor, true, false);
    }

    private Transformation getWallTransformation(boolean isLeft){
        int shipSpeed = swArena.shipSpeed;
        Vector3f v = new Vector3f(wallTransformation.getTranslation());
        if (isLeft){
            v.z += ((shipSpeed-1)*0.05f);
        }
        else{
            v.z -= ((shipSpeed-1)*0.05f);
        }

        return new Transformation(v, wallTransformation.getLeftRotation(), wallTransformation.getScale(), wallTransformation.getRightRotation());
    }

    private Transformation getCoverTransformation(boolean isFloor){
        int shipSpeed = swArena.shipSpeed;
        Vector3f v = new Vector3f(coverTransformation.getTranslation());
        if (isFloor){
            v.y += ((shipSpeed-1)*0.05f);
        }
        else{
            v.y -= ((shipSpeed-1)*0.05f);
        }
        return new Transformation(v, coverTransformation.getLeftRotation(), coverTransformation.getScale(), coverTransformation.getRightRotation());
    }



    private void applyData(Display display, boolean despawnDelayed, boolean setBrightness){
        display.setViewRange(2);
        translateDisplay(display);

        if (despawnDelayed){
            Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), display::remove, WorldPositioner.translationTime/swArena.shipSpeed);
        }
        if (setBrightness){
            display.setBrightness(new Display.Brightness(15, 15));
        }
    }

    public void translateDisplay(Display display){
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () ->{
            DisplayUtils.translate(display, displayTranslation, translationDistance, (int) translationTime/swArena.shipSpeed, 0);
        }, 2);
    }


    public void useEndgameMaterials(){
        if (swArena.isVictory){
            ceilingItem = new ItemStack(Material.QUARTZ_BRICKS);
            wallItem = new ItemStack(Material.QUARTZ_PILLAR);
            floorItem = ceilingItem;
        }
        else{
            ceilingItem = new ItemStack(Material.SMOOTH_BASALT);
            wallItem = new ItemStack(Material.BLACKSTONE);
            floorItem = new ItemStack(Material.MAGMA_BLOCK);
        }

    }
}
