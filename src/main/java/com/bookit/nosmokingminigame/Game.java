package com.bookit.nosmokingminigame;

import com.bookit.nosmokingminigame.task.GameTask;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Game {
    public enum PROGRESS {
        READY, PROGRESSING
    }

    public enum GAME_OBJECT {
        WALL_LEFT, WALL_RIGHT, WALL_UPPER, BAR, ITEM
    }

    private static PROGRESS progress = PROGRESS.READY;

    private static World world ;
    private static Player player ;
    private static List<FallingBlock> bar = new ArrayList<>();

    private static GameTask task ;
    private static int point = 0;

    /**
     * Get Progress of game.
     * @return progress Progress of game
     */
    public static PROGRESS getProgress() {
        return progress;
    }

    /**
     * Set progress of game.
     * @return oldProgress Previous progress of game
     */
    public static PROGRESS setProgress(PROGRESS newProgress) {
        PROGRESS oldProgress = progress;
        progress = newProgress;
        return oldProgress;
    }

    /**
     * Get world of game.
     * If null returns null.
     * @return world World of game
     */
    public static World getWorld() {
        return world == null ? null : world;
    }

    /**
     * Set world of game.
     * If previous world is not exists, returns null.
     * @return oldWorld Previous world of game
     */
    public static World setWorld(World newWorld) {
        World oldWorld = world == null ? null : world;
        world = newWorld;
        return oldWorld;
    }

    /**
     * Get player of game.
     * If null returns null.
     * @return player Player of game
     */
    public static Player getPlayer() {
        return player == null ? null : player;
    }

    /**
     * Set player of game.
     * If previous player is not exists, returns null.
     * @return oldPlayer Previous player of game
     */
    public static Player setPlayer(Player newPlayer) {
        Player oldPlayer = player == null ? null : player;
        player = newPlayer;
        return oldPlayer;
    }

    /**
     * Get bars of game.
     * @return bars Bar of game
     */
    public static List<FallingBlock> getBar() {
        return bar;
    }

    /**
     * Get point of game.
     * @return point Point of game
     */
    public static int getPoint() {
        return point;
    }

    /**
     * Set point of game.
     * @return oldPoint Previous point of game
     */
    public static int setPoint(int newPoint) {
        int oldPoint = point;
        point = newPoint;
        return oldPoint;
    }

    /**
     * Spawn item on filed.
     */
    private static List<Material> itemList = Arrays.asList(Material.BEDROCK, Material.GLASS, Material.COAL_BLOCK, Material.GRAY_STAINED_GLASS);
    public static void spawnItem() {
        // 10.5 ~ -9.5
        double rand1 = Math.random() * 19;
        double z = -9.5 + rand1;
        double rand2 = Math.random() * 12;
        double y = 51 + rand2;
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(new Location(world, 20.5, y, z), EntityType.ARMOR_STAND);
        armorStand.addScoreboardTag("NoSmokingMiniGame");
        armorStand.addScoreboardTag("NoSmokingMiniGameItemArmorStand");
        armorStand.setMarker(true);
        Collections.shuffle(itemList);
        Material material = itemList.get(0);
        armorStand.setHelmet(new ItemStack(material));
        armorStand.setInvisible(true);
        armorStand.setGlowing(true);
    }

    /**
     * Start game.
     * Warning!! This funtion is only for developer.
     */
    public static void onStart() {
        Location loc = new Location(world, 0.5, 50, 0.5);
        loc.setYaw(-90);
        loc.setPitch(0);
        player.teleport(loc);

        for (int i = 0; i <= 2; i++) {
            FallingBlock fallingBlock =  world.spawnFallingBlock(new Location(world, 20.5, 48.5, -0.5 + i), Material.DIAMOND_BLOCK, (byte) 0);
            fallingBlock.setGravity(false);
            bar.add(fallingBlock);
        }

        GameTask gameTask = new GameTask();
        gameTask.runTaskTimer(NoSmokingMiniGame.getPlugin(), 0L, 1L);
        task = gameTask;
    }

    /**
     * Stop game.
     * Warning!! This funtion is only for developer.
     */
    public static void onStop() {
        progress = PROGRESS.READY;

        Ball.setSpeed(Ball.getDefalutSpeed());

        for (FallingBlock fallingBlock : bar) {
            fallingBlock.remove();
        }
        for (LivingEntity livingEntity : world.getLivingEntities()) {
            if (livingEntity instanceof ArmorStand && livingEntity.getScoreboardTags().contains("NoSmokingMiniGame")) {
                livingEntity.remove();
            }
        }
        bar = new ArrayList<>();
        task.cancel();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(GameTask.formattingInfo(), ChatColor.translateAlternateColorCodes('&', "&l&f[ " + player.getName() + "님의 결과 ]"), 10, 80, 10);
            world.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 20f, 1f);
        }
        point = 0;
    }
}
