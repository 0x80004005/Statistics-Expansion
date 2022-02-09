/*
 *
 * Statistics-Expansion
 * Copyright (C) 2020 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.mcstatistics;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.logging.Level;

import static org.bukkit.Bukkit.getServer;

public class StatisticsExpansion extends PlaceholderExpansion implements Cacheable {

    private final ListMultimap<Statistic, Material> ignoredMaterials = ArrayListMultimap.create();
    private final String VERSION = "1.0";
    private final boolean isLegacy = !Enums.getIfPresent(Material.class, "TURTLE_HELMET").isPresent();
    public static final String SERVER_VERSION = Bukkit.getBukkitVersion().split("-")[0];
    private final boolean supportOfflinePlayers = Enums.getIfPresent(Material.class, "BEEHIVE").isPresent();

    @Override
    public String getAuthor() {
        return "medievalknievel";
    }

    @Override
    public String getIdentifier() {
        return "agstat";
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @SuppressWarnings({"DuplicatedCode", "Guava"})
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        OfflinePlayer[] allPlayers = getServer().getOfflinePlayers();
        final int splitterIndex = identifier.indexOf(':');

        switch (identifier.toLowerCase()) {
            case "mine_block": {
                if (splitterIndex == -1) {
                    return StatisticsUtils.getStatistic(player, identifier, supportOfflinePlayers);
                }else{
                    return calculateTotal(player, Statistic.MINE_BLOCK, allPlayers);
                }
            }
            case "use_item": {
                if (splitterIndex == -1) {
                    return StatisticsUtils.getStatistic(player, identifier, supportOfflinePlayers);
                }else{
                    return calculateTotal(player, Statistic.USE_ITEM, allPlayers);
                }
            }
            case "break_item": {
                if (splitterIndex == -1) {
                    return StatisticsUtils.getStatistic(player, identifier, supportOfflinePlayers);
                }else{
                    return calculateTotal(player, Statistic.BREAK_ITEM, allPlayers);
                }
            }

            case "craft_item": {
                if (splitterIndex == -1) {
                    return StatisticsUtils.getStatistic(player, identifier, supportOfflinePlayers);
                }
                else{
                    return calculateTotal(player, Statistic.CRAFT_ITEM, allPlayers);
                }
            }
        }

        // %statistic_<Statistic>%
        if (splitterIndex == -1) {
            return StatisticsUtils.getStatistic(player, identifier, supportOfflinePlayers);
        }

        final String statisticIdentifier = identifier.substring(0, splitterIndex).toUpperCase();
        final String types = identifier.substring(splitterIndex + 1).toUpperCase();

        if (types.trim().isEmpty()) {
            return StatisticsUtils.getStatistic(player, statisticIdentifier, supportOfflinePlayers);
        }

        final Optional<Statistic> statisticOptional = Enums.getIfPresent(Statistic.class, statisticIdentifier);

        if (!statisticOptional.isPresent()) {
            return "Unknown statistic '" + statisticIdentifier + "', check https://helpch.at/docs/" + SERVER_VERSION + "/org/bukkit/Statistic.html for more info";
        }

        final Statistic statistic = statisticOptional.get();


        // %agstat_<Statistic>:<Material/Entity>%
        if (!types.contains(",")) {
            switch (statistic.getType()) {
                case BLOCK:
                case ITEM: {
                    final Optional<Material> material = Enums.getIfPresent(Material.class, types);

                    if (!material.isPresent()) {
                        return "Invalid material " + types;
                    }

                    try {
                        int total = 0;
                        for(OfflinePlayer p : allPlayers){
                            total += p.getStatistic(statistic, material.get());
                        }
                        String totalReturn = String.valueOf(total);
                        return totalReturn;
                    } catch (IllegalArgumentException e) {
                        errorLog("Could not get the statistic '" + statistic.name() + "' for '" + material.get().name() + "'", e);
                        return "Could not get the statistic '" + statistic.name() + "' for '" + material.get().name() + "'";
                    }
                }

                case ENTITY: {
                    final Optional<EntityType> entityType = Enums.getIfPresent(EntityType.class, types);

                    if (!entityType.isPresent()) {
                        return "Invalid entity " + types;
                    }
                    try {
                        int total = 0;
                        for(OfflinePlayer p : allPlayers){
                            total += p.getStatistic(statistic, entityType.get());
                        }
                        String totalReturn = String.valueOf(total);
                        return totalReturn;
                    } catch (IllegalArgumentException e) {
                        errorLog("Could not get the statistic '" + statistic.name() + "' for '" + entityType.get().name() + "'", e);
                        return "Could not get the statistic '" + statistic.name() + "' for '" + entityType.get().name() + "'";
                    }
                }
                default:
                    break;
            }
        }

        // %statistic_<Statistic>:<Material/Entity>,<Material/Entity>,<Material/Entity>%
        final String[] args = types.split(",");
        int total = 0;

        switch (statistic.getType()) {
            case BLOCK:
            case ITEM: {
                for (String arg : args) {
                    final Optional<Material> material = Enums.getIfPresent(Material.class, arg);

                    if (!material.isPresent()) {
                        continue;
                    }

                    try {
                        for(OfflinePlayer p : allPlayers){
                            total += p.getStatistic(statistic, material.get());
                        }

                    } catch (IllegalArgumentException e) {
                        errorLog("Could not get the statistic '" + statistic.name() + "' for '" + material.get().name() + "'", e);
                        break;
                    }
                }

                break;
            }

            case ENTITY: {
                for (String arg : args) {
                    final Optional<EntityType> entityType = Enums.getIfPresent(EntityType.class, arg);

                    if (!entityType.isPresent()) {
                        continue;
                    }

                    try {
                        for(OfflinePlayer p : allPlayers){
                            total += p.getStatistic(statistic, entityType.get());
                        }

                    } catch (IllegalArgumentException e) {
                        errorLog("Could not get the statistic '" + statistic.name() + "' for '" + entityType.get().name() + "'", e);
                        break;
                    }
                }

                break;
            }

            default:
                break;
        }

        String totalReturn = String.valueOf(total);
        return totalReturn;
    }

    private String calculateTotal(final OfflinePlayer player, final Statistic statistic, final OfflinePlayer[] allPlayers) {
        String totalReturn = "";

        for (Material material : Material.values()) {
            if (ignoredMaterials.get(statistic).contains(material)) {
                continue;
            }

            if (statistic == Statistic.MINE_BLOCK && (material.name().equals("GRASS") || material.name().equals("SOIL"))) {
                continue;
            }

            try {
                int total = 0;
                for(OfflinePlayer p : allPlayers){

                    total += p.getStatistic(statistic, material);
                }
                totalReturn = String.valueOf(total);

            } catch (IllegalArgumentException ignored) {
                ignoredMaterials.put(statistic, material);
            }
        }

        return totalReturn;
    }

    private void errorLog(final String message, final Throwable exception) {
        PlaceholderAPIPlugin.getInstance().getLogger().log(Level.SEVERE, "[Statistic Expansion] " + message, exception);
    }

    @Override
    public void clear() {
        ignoredMaterials.clear();
    }
}
