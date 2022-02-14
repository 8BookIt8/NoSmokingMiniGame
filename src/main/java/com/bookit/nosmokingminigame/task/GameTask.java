package com.bookit.nosmokingminigame.task;

import com.bookit.nosmokingminigame.Game;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class GameTask extends BukkitRunnable {
    private static int ticks = -60;

    public GameTask() {
        ticks = -60;
    }

    @Override
    public void run() {
        Player player = Game.getPlayer();
        List<FallingBlock> bar = Game.getBar();
        Block targetBlock = player.getTargetBlock(null, 50);
        for (int i = 0; i <= 2; i++) {
            Vector targetVec = new Vector(20.5, 48, targetBlock.getZ() - 1);
            targetVec.setZ(Math.max(-9.5, targetVec.getZ()));
            targetVec.setZ(Math.min(8.5, targetVec.getZ()));
            targetVec.add(new Vector(0, 0, i));
            moveTo(bar.get(i), targetVec);
            bar.get(i).setTicksLived(10);
        }

        if (ticks % 100 == 0) {
            Game.spawnItem();
        }

        ticks++;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattingInfo()));
    }

    /**
     * Formatting ticks and point to information.
     * @return Formatted String
     */
    public static String formattingInfo() {
        int min = ticks / 1200;
        int sec = (ticks % 1200) / 20;
        return ChatColor.translateAlternateColorCodes('&', "&l&f" + min + "&6분 &f" + sec + "&6초 &f" + Game.getPoint() + "&6포인트");
    }

    /**
     * Move bar to vec.
     * @param block Falling block to move
     * @param vec Target vector
     */
    private static void moveTo(FallingBlock block, Vector vec) {
        Vector blockVec = block.getLocation().toVector();
        Vector velocity = vec.subtract(blockVec).setX(0).setY(0).multiply(0.7);
        block.setVelocity(velocity);
    }
}
