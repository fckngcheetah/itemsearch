package com.github.fckngcheetah.itemsearch.api;

import com.github.fckngcheetah.itemsearch.SearchPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ItemSearch {
    private Material material;

    public ItemSearch(Material material) {
        this.material = material;
    }

    // TODO: Re-Implement async logic
    // As of now uses the main thread
    public CompletableFuture<Chest[]> findChests(World world) {
        CompletableFuture<Chest[]> foundChestsFuture = new CompletableFuture<>();
        ArrayList<Chest> foundChests = new ArrayList<>();
        ArrayList<CompletableFuture<Void>> chunkProcessor = new ArrayList<>();

        for (Chunk chunk : world.getLoadedChunks()) {
            BlockState[] blockStates = chunk.getTileEntities();

            for (BlockState blockState : chunk.getTileEntities()) {
                if (
                        blockState.getType() == Material.CHEST ||
                                blockState.getType() == Material.TRAPPED_CHEST
                ) {
                    Chest chest = (Chest) blockState.getBlock().getState();

                    if (chest.getBlockInventory().contains(material)) {
                        foundChests.add(chest);
                    }
                }
            }
        }
        foundChestsFuture.complete(foundChests.toArray(new Chest[0]));
        return foundChestsFuture;
    }

    // Does not use the main thread
    public CompletableFuture<Player[]> findPlayers() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Player> foundPlayers = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(material)) {
                    foundPlayers.add(player);
                }
            }

            Player[] players = foundPlayers.toArray(new Player[0]);

            return players;
        });
    }
}
