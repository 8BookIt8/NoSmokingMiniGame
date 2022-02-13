package com.bookit.nosmokingminigame;

import com.bookit.nosmokingminigame.task.GameStartTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class nsmgCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&c/nsmg <start|stop|시작|중지|>"));
            return false;
        }
        if (args[0].equals("start")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&c/nsmg start <playerName>"));
                return false;
            }

            if (Bukkit.getPlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&cCan't find Player " + args[1]));
                return false;
            }

            if (Game.getProgress() == Game.PROGRESS.PROGRESSING) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&cThe game is already started."));
                return false;
            }

            Game.setProgress(Game.PROGRESS.PROGRESSING);
            Player player = Bukkit.getPlayer(args[1]);
            Game.setPlayer(player);
            Game.setWorld(player.getWorld());
            GameStartTask gameStartTask = new GameStartTask();
            gameStartTask.runTaskTimer(NoSmokingMiniGame.getPlugin(), 20L, 20L);
            return true;
        } else if (args[0].equals("stop")) {
            Game game = NoSmokingMiniGame.getGame();
            if (game.getProgress() == Game.PROGRESS.READY) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&cThe game is not in progress."));
                return false;
            }

            game.onStop();
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> argsRecommand = new ArrayList<>();
        if (args.length == 1) {
            argsRecommand = Arrays.asList("start", "stop");
        } else if (args.length == 2) {
            if (args[0].equals("start")) {
                Collection<Player> onlinePlayers = (Collection<Player>) Bukkit.getOnlinePlayers();
                List<String> finalArgsRecommand = argsRecommand;
                onlinePlayers.forEach(player -> finalArgsRecommand.add(player.getName()));
                argsRecommand = finalArgsRecommand;
            }
        }
        return argsRecommand;
    }
}
