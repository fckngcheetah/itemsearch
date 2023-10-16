package com.github.fckngcheetah.itemsearch;

import com.github.fckngcheetah.itemsearch.commands.SearchCommand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.java.JavaPlugin;

public class SearchPlugin extends JavaPlugin {
    private static SearchPlugin SEARCH_PLUGIN;

    public static SearchPlugin getInstance() {
        return SEARCH_PLUGIN;
    }

    @Override
    public void onEnable() {
        SEARCH_PLUGIN = this;
        getCommand("search").setExecutor(new SearchCommand());
    }
}
