package com.bookit.nosmokingminigame;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Ball {
    public enum Axis {
        X, Y
    }

    private static ArmorStand ball ;
    private static BallMoveTask task ;

    private static double defalutSpeed = 0.4;
    private static double speed = defalutSpeed;
    private static double dir = Math.random() * 360;

    /**
     * Get ball of game.
     * If null returns null.
     * @return ball Ball of game
     */
    public static ArmorStand getBall() {
        return ball;
    }


    /**
     * Get default speed of game.
     * @return defaultSpeed Default speed of game
     */
    public static double getDefalutSpeed() {
        return defalutSpeed;
    }

    /**
     * Get speed of game.
     * @return speed Speed of game
     */
    public static double getSpeed() {
        return speed;
    }

    /**
     * Set speed of game.
     * @return oldSpeed Previous speed of game
     */
    public static double setSpeed(double newSpeed) {
        double oldSpeed = speed;
        speed = newSpeed;
        return oldSpeed;
    }

    /**
     * Spawn ball and return & remove old one.
     * If old one is not exists, return null.
     * @return oldBall Old ball
     */
    public static ArmorStand spawn() {
        ArmorStand oldBall = null;
        if (ball != null) {
            oldBall = ball;
            task.cancel();
            ball.remove();
        }

        World world = Game.getWorld();
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(new Location(world, 20.5, 60, 0.5), EntityType.ARMOR_STAND);
        armorStand.addScoreboardTag("NoSmokingMiniGame");
        armorStand.addScoreboardTag("NoSmokingMiniGameBallArmorStand");
        armorStand.setMarker(true);
        armorStand.setHelmet(new ItemStack(Material.GOLD_BLOCK));
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        ball = armorStand;

        dir = Math.random() * 360;

        BallMoveTask ballMoveTask = new BallMoveTask();
        ballMoveTask.runTaskTimer(NoSmokingMiniGame.getPlugin(), 20L, 1L);
        task = ballMoveTask;

        return oldBall;
    }

    public static double getDistanceFrom(Location loc) {
        if (ball == null) {
            return -1;
        }

        Location ballVec = ball.getLocation();
        return Math.sqrt(Math.pow(loc.getY() - ballVec.getY(), 2) + Math.pow(loc.getZ() - ballVec.getZ(), 2));
    }

    public static void move() {
        if (ball == null) {
            return ;
        }

        ball.teleport(ball.getLocation().add(0, Math.sin(Math.toRadians(dir)) * speed, Math.cos(Math.toRadians(dir)) * speed));
    }

    public static void collisionWithBar() {
        FallingBlock centerBlock = Game.getBar().get(1);
        double diff = ball.getLocation().getZ() - centerBlock.getLocation().getZ();
        if (diff <= 0) {
            dir = 90 + (Math.abs(diff) * 45);
        } else {
            dir = 90 - (Math.abs(diff) * 45);
        }
        Game.getWorld().playSound(ball.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 20f, 1f);
    }

    public static void collisionWithWall(Axis axis) {
        if (axis == Axis.X) {
            dir = 180 - dir;
//            dir += (Math.random() * 10) - 5;
        } else {
            dir = 360 - dir;
//            dir += (Math.random() * 10) - 5;
        }
        Game.getWorld().playSound(ball.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 20f, 1f);
    }

    public static void destroyItem(Material material) {
        int point = 0;
        String itemName = "";
        String itemDescription = "";
        if (material == Material.BEDROCK) {
            point = 100;
            itemName = "니코틴";
            itemDescription = "혈압을 높여 고혈압을 유발할 수 있으며 뇌졸중, 탈모 등의 증상을 유발한다.";
        } else if (material == Material.GLASS) {
            point = 80;
            itemName = "아세트알데하이드";
            itemDescription = "간뿐만 아니라 다른 장기들에게 부정적인 영향을 주어 암을 유발한다.";
        } else if (material == Material.COAL_BLOCK) {
            point = 70;
            itemName = "일산화탄소";
            itemDescription = "헤모글로빈과 결합하여 산소 운반 기능을 저하시킨다.";
        } else if (material == Material.GRAY_STAINED_GLASS) {
            point = 75;
            itemName = "메탄올";
            itemDescription = "메탄올이 산화한 포름알데히드는 과섭취 시 실명과 죽음을 유발한다";
        }
        Game.getWorld().spawnParticle(Particle.BLOCK_CRACK, Ball.getBall().getLocation().add(0, 1.5, 0), 50, Bukkit.createBlockData(material));
        Game.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&6" + itemName));
        Game.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&f" + itemDescription));
        Game.setPoint(Game.getPoint() + point);
    }
}

class BallMoveTask extends BukkitRunnable {
    Game.GAME_OBJECT lastHitObject ;

    @Override
    public void run() {
        ArmorStand ball = Ball.getBall();
        Location loc = ball.getLocation();
        // 공과 막대 충돌
        for (FallingBlock bar : Game.getBar()) {
            if (Ball.getDistanceFrom(bar.getLocation()) <= 0.6 && lastHitObject != Game.GAME_OBJECT.BAR) {
                this.lastHitObject = Game.GAME_OBJECT.BAR;
                Ball.collisionWithBar();
                break;
            }
        }

        // 공과 벽 충돌
        if (loc.getZ() <= -9.7 && lastHitObject != Game.GAME_OBJECT.WALL_LEFT) {
            this.lastHitObject = Game.GAME_OBJECT.WALL_LEFT;
            Ball.collisionWithWall(Ball.Axis.X);
        } else if (loc.getZ() >= 10.7 && lastHitObject != Game.GAME_OBJECT.WALL_RIGHT) {
            this.lastHitObject = Game.GAME_OBJECT.WALL_RIGHT;
            Ball.collisionWithWall(Ball.Axis.X);
        } else if (loc.getY() >= 63.5 && lastHitObject != Game.GAME_OBJECT.WALL_UPPER) {
            this.lastHitObject = Game.GAME_OBJECT.WALL_UPPER;
            Ball.collisionWithWall(Ball.Axis.Y);
        } else if (loc.getY() <= 46) {
            Game.onStop();
            this.cancel();
        }

        // 공과 필드 아이템 충돌
        for (LivingEntity item : Game.getWorld().getLivingEntities()) {
            if (item instanceof ArmorStand && item.getScoreboardTags().contains("NoSmokingMiniGameItemArmorStand") && Ball.getDistanceFrom(item.getLocation()) <= 0.7) {
                Location itemLoc = item.getLocation();
                Location ballLoc = ball.getLocation();
                if (Math.abs(itemLoc.getY() - ballLoc.getY()) < Math.abs(itemLoc.getZ() - ballLoc.getZ())) {
                    Ball.collisionWithWall(Ball.Axis.X);
                } else {
                    Ball.collisionWithWall(Ball.Axis.Y);
                }
                this.lastHitObject = Game.GAME_OBJECT.ITEM;
                Ball.setSpeed(Ball.getSpeed() + 0.05);
                Ball.destroyItem(((ArmorStand) item).getHelmet().getType());
                item.remove();
            }
        }

        Ball.move();
    }
}
