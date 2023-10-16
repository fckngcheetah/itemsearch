package com.github.fckngcheetah.itemsearch;

import com.github.fckngcheetah.itemsearch.commands.SearchCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SearchPlugin extends JavaPlugin {
    private static SearchPlugin SEARCH_PLUGIN;

    public static SearchPlugin getInstance() {
        return SearchPlugin.SEARCH_PLUGIN;
    }

    @Override
    public void onEnable() {
        SearchPlugin.SEARCH_PLUGIN = this;
        this.getCommand("search").setExecutor(new SearchCommand());
    }
}
