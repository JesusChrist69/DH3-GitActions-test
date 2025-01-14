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

package eu.decentsoftware.holograms.editor.move;

import eu.decentsoftware.holograms.hologram.DefaultHologram;
import eu.decentsoftware.holograms.hologram.page.DefaultHologramPage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * This class is used as a location binder to bind a {@link DefaultHologram} to a {@link Player}. It
 * is used by the {@link MoveController} to move the hologram.
 *
 * @author d0by
 * @see MoveController
 * @since 3.0.0
 */
@Getter
@Setter
public class MoveLocationBinder implements Supplier<Location> {

    private final @NotNull Player player;
    private final @NotNull DefaultHologram hologram;
    private int distance;

    @Contract(pure = true)
    public MoveLocationBinder(@NotNull Player player, @NotNull DefaultHologram hologram) {
        this(player, hologram, 5);
    }

    @Contract(pure = true)
    public MoveLocationBinder(@NotNull Player player, @NotNull DefaultHologram hologram, int distance) {
        this.player = player;
        this.hologram = hologram;
        this.distance = distance;
    }

    @Override
    public Location get() {
        Location location = player.getEyeLocation();
        Vector lookDirection = location.getDirection();

        location.add(lookDirection.multiply(distance));

        int pageIndex = hologram.getVisibilityManager().getPage(player);
        DefaultHologramPage page = (DefaultHologramPage) hologram.getPage(pageIndex);
        if (page == null) {
            return location;
        }

        double height = page.getHeight();

        location.add(0, height / 2, 0);
        if (hologram.getSettings().isDownOrigin()) {
            location.subtract(0, height, 0);
        }

        // Snap to block center if sneaking
        if (player.isSneaking()) {
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);
        }

        return location;
    }

    /**
     * Increase the distance by 1. The maximum distance is 13.
     *
     * @see #decreaseDistance()
     */
    public void increaseDistance() {
        if (distance < 13) {
            distance++;
        }
    }

    /**
     * Decrease the distance by 1. The minimum distance is 3.
     *
     * @see #increaseDistance()
     */
    public void decreaseDistance() {
        if (distance > 3) {
            distance--;
        }
    }

}
