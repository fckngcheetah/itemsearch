package com.github.fckngcheetah.itemsearch.api;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ItemSearch {
    private final Material material;

    public ItemSearch(final Material material) {
        this.material = material;
    }

    // TODO: Re-Implement async logic
    // As of now uses the main thread
    public CompletableFuture<Chest[]> findChests(final World world) {
        final CompletableFuture<Chest[]> foundChestsFuture = new CompletableFuture<>();
        final ArrayList<Chest> foundChests = new ArrayList<>();
        final ArrayList<CompletableFuture<Void>> chunkProcessor = new ArrayList<>();

        for (final Chunk chunk : world.getLoadedChunks()) {
            final BlockState[] blockStates = chunk.getTileEntities();

            for (final BlockState blockState : chunk.getTileEntities()) {
                if (
                        Material.CHEST == blockState.getType() ||
                                Material.TRAPPED_CHEST == blockState.getType()
                ) {
                    final Chest chest = (Chest) blockState.getBlock().getState();

                    if (chest.getBlockInventory().contains(this.material)) {
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
            final ArrayList<Player> foundPlayers = new ArrayList<>();

            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(this.material)) {
                    foundPlayers.add(player);
                }
            }

            final Player[] players = foundPlayers.toArray(new Player[0]);

            return players;
        });
    }
}
