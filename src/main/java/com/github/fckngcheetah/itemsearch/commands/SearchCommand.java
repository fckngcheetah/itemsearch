package com.github.fckngcheetah.itemsearch.commands;

import com.github.fckngcheetah.itemsearch.api.ItemSearch;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    Component.text("Please use this command as a player.")
                            .color(NamedTextColor.RED)
            );
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("itemsearch.search")) {
            player.sendMessage(Bukkit.permissionMessage());
            return false;
        }

        if (args.length != 1) {
            return false;
        }

        String materialName = args[0].toUpperCase();
        Material material = Material.getMaterial(materialName);

        if (material == null) {
            player.sendMessage(
                    Component.text(materialName + " is not a valid material.")
                            .color(NamedTextColor.RED)
            );
            return true;
        }

        ItemSearch itemSearch = new ItemSearch(material);
        CompletableFuture<Chest[]> chestFuture = itemSearch.findChests(player.getWorld());
        CompletableFuture<Player[]> playerFuture = itemSearch.findPlayers();

        CompletableFuture.allOf(
                chestFuture,
                playerFuture
        ).thenRun(() -> {
            Chest[] foundChests;
            Player[] foundPlayers;
            try {
                foundChests = chestFuture.get();
                foundPlayers = playerFuture.get();
            } catch (Exception exception) {
                player.sendMessage(
                        Component.text("There was an error while searching.")
                                .color(NamedTextColor.RED)
                );
                return;
            }

            player.sendMessage(
                    ChatColor.GRAY + "Found " + materialName + " in " + ChatColor.GREEN +
                            foundChests.length + " chests" + ChatColor.GRAY + " and " +
                            ChatColor.GREEN + foundPlayers.length + " inventories" +
                            ChatColor.GRAY + "."
            );
            TextComponent showChestsComponent = Component.text("[Show chests]")
                    .color(NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.callback((audience) -> {
                        for (Chest chest : foundChests) {
                            String coordinateString = chest.getLocation().getBlockX() + " " +
                                    chest.getLocation().getBlockY() + " " +
                                    chest.getLocation().getBlockZ();
                            player.sendMessage(
                                    Component.text("» Chest at " + coordinateString + " ")
                                            .color(NamedTextColor.GRAY)
                                            .append(
                                                    Component.text("[Teleport]")
                                                            .color(NamedTextColor.GREEN)
                                                            .clickEvent(ClickEvent.runCommand("/tp " + coordinateString))
                                            )
                            );
                        }
                    }));
            TextComponent showInventoriesComponent = Component.text("[Show inventories]")
                    .color(NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.callback((audience) -> {
                        for (Player foundPlayer : foundPlayers) {
                            player.sendMessage(
                                    Component.text("» " + foundPlayer.getName() + " ")
                                            .color(NamedTextColor.GRAY)
                                            .append(
                                                    Component.text("[Inventory]")
                                                            .color(NamedTextColor.GREEN)
                                                            .clickEvent(ClickEvent.callback((unused) -> {
                                                                player.openInventory(foundPlayer.getInventory());
                                                            }))
                                            )
                            );
                        }
                    }));

            player.sendMessage(
                    Component.text()
                            .append(showChestsComponent)
                            .appendSpace()
                            .append(showInventoriesComponent)
            );
        });

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
