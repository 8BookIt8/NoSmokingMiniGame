package com.bookit.nosmokingminigame;

import org.bukkit.plugin.java.JavaPlugin;

public class NoSmokingMiniGame extends JavaPlugin {
    private static NoSmokingMiniGame plugin ;

    private static Game game = new Game();

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("nsmg").setExecutor(new nsmgCommand());
    }

    public static NoSmokingMiniGame getPlugin() {
        return plugin;
    }

    public static Game getGame() {
        return game;
    }
}
