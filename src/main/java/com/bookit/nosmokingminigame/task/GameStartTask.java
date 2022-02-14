package com.bookit.nosmokingminigame.task;

import com.bookit.nosmokingminigame.Ball;
import com.bookit.nosmokingminigame.Game;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartTask extends BukkitRunnable {
    private int ticks = 0;

    @Override
    public void run() {
        Player player = Game.getPlayer();
        switch (ticks) {
            case 0:
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&l&f3"), "Ready for Start!", 0, 30, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 20f, 1f);
                Game.onStart();
                break;
            case 1:
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&l&62"), "Ready for Start!", 0, 30, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 20f, 1f);
                break;
            case 2:
                Ball.spawn();
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&l&c1"), "Ready for Start!", 0, 30, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 20f, 1f);
                break;
            case 3:
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&l&aStart!"), "Ready for Start!", 0, 20, 0);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 20f, 1f);
                this.cancel();
        }

        this.ticks++;
    }
}
