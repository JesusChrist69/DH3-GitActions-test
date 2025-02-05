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

package eu.decentsoftware.holograms.utils;

import eu.decentsoftware.holograms.DecentHolograms;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for Bungee Messaging Channel.
 *
 * @author d0by
 * @since 3.0.0
 */
@UtilityClass
public final class BungeeUtils {

    private static final DecentHolograms PLUGIN = DecentHolograms.getInstance();
    private static final Map<String, CompletableFuture<Integer>> PLAYER_COUNT_REQUESTS = new ConcurrentHashMap<>();

    /**
     * Init Bungee connection; Register BungeeCord channel.
     */
    public static void init() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(PLUGIN, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(PLUGIN, "BungeeCord", new BungeeListener());
    }

    /**
     * Shutdown Bungee connection; Unregister BungeeCord channel.
     */
    public static void shutdown() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(PLUGIN, "BungeeCord");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(PLUGIN, "BungeeCord");
    }

    private static void handleReceive(@NotNull String server, int playerCount) {
        if (PLAYER_COUNT_REQUESTS.containsKey(server)) {
            PLAYER_COUNT_REQUESTS.get(server).complete(playerCount);
            PLAYER_COUNT_REQUESTS.remove(server);
        }
    }

    /**
     * Connect player to the given server.
     *
     * @param player The player.
     * @param server The servers name.
     */
    public static void connect(@NotNull Player player, @NotNull String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        player.sendPluginMessage(PLUGIN, "BungeeCord", b.toByteArray());
    }

    /**
     * Request the player count of the given server from BungeeCord.
     *
     * @param player The player to send the request from.
     * @param server The server.
     * @return CompletableFuture with the player count.
     */
    public static CompletableFuture<Integer> sendPlayerCountRequest(@NotNull Player player, @NotNull String server) {
        if (PLAYER_COUNT_REQUESTS.containsKey(server)) {
            return PLAYER_COUNT_REQUESTS.get(server);
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        player.sendPluginMessage(PLUGIN, "BungeeCord", b.toByteArray());

        CompletableFuture<Integer> future = new CompletableFuture<>();
        PLAYER_COUNT_REQUESTS.put(server, future);
        return future;
    }

    private static class BungeeListener implements PluginMessageListener {

        @Override
        public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            try {
                String subChannel = dataInputStream.readUTF();
                if (subChannel.equals("PlayerCount")) {
                    String server = dataInputStream.readUTF();
                    int count = dataInputStream.readInt();
                    BungeeUtils.handleReceive(server, count);
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }

    }

}
