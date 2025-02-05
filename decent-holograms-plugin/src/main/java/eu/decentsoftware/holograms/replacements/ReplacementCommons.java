/*
 * DecentHolograms
 * Copyright (C) DecentSoftware.eu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.decentsoftware.holograms.replacements;

import eu.decentsoftware.holograms.Config;
import eu.decentsoftware.holograms.DecentHolograms;
import eu.decentsoftware.holograms.server.Server;
import eu.decentsoftware.holograms.utils.BungeeUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@UtilityClass
final class ReplacementCommons {

    private static final DecentHolograms PLUGIN = DecentHolograms.getInstance();

    static int getFromServerOrServersInt(Player player, @NotNull String argument, @NotNull Function<Server, Integer> getValue) {
        // -- Get from multiple servers
        if (argument.contains(",")) {
            int total = 0;
            for (String s : argument.split(",")) {
                if (Config.PINGER_ENABLED && PLUGIN.getServerRegistry().containsServer(s)) {
                    Server server = PLUGIN.getServerRegistry().getServer(s);
                    if (server != null && server.isOnline()) {
                        total += getValue.apply(server);
                    }
                } else if (player != null) {
                    // -- If not pinged, get from bungee
                    try {
                        total += BungeeUtils.sendPlayerCountRequest(player, s).get();
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                }
            }
            return total;
        }
        // -- Get from one server
        if (Config.PINGER_ENABLED && PLUGIN.getServerRegistry().containsServer(argument)) {
            Server server = PLUGIN.getServerRegistry().getServer(argument);
            if (server != null && server.isOnline()) {
                return getValue.apply(server);
            }
        } else if (player != null) {
            // -- If not pinged, get from bungee
            try {
                return BungeeUtils.sendPlayerCountRequest(player, argument).get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
        return -1;
    }

    static int getFromWorldOrWorldsInt(@NotNull String argument, @NotNull Function<World, Integer> getValue) {
        // -- Get from multiple servers
        if (argument.contains(",")) {
            int total = 0;
            for (String s : argument.split(",")) {
                World world = Bukkit.getWorld(s);
                if (world != null) {
                    total += getValue.apply(world);
                }
            }
            return total;
        }
        // -- Get from one server
        World world = Bukkit.getWorld(argument);
        if (world != null) {
            return getValue.apply(world);
        }
        return -1;
    }

}
