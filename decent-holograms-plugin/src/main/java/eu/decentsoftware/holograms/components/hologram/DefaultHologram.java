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

package eu.decentsoftware.holograms.components.hologram;

import com.google.common.collect.ImmutableList;
import eu.decentsoftware.holograms.api.component.hologram.*;
import eu.decentsoftware.holograms.api.component.page.HologramPage;
import eu.decentsoftware.holograms.components.DefaultPositionManager;
import eu.decentsoftware.holograms.conditions.ConditionHolder;
import eu.decentsoftware.holograms.ticker.Ticked;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class DefaultHologram implements Hologram, Ticked {

    private final @NotNull String name;
    private final @NotNull HologramConfig config;
    private final @NotNull HologramSettings settings;
    private final @NotNull DefaultPositionManager positionManager;
    private final @NotNull HologramVisibilityManager visibilityManager;
    private final @NotNull List<HologramPage> pages;
    /**
     * The condition holder of the hologram, used for managing the view conditions of the hologram.
     */
    private final @NotNull ConditionHolder viewConditions;

    @Getter(AccessLevel.NONE)
    private final @NotNull AtomicLong lastVisibilityUpdate;
    @Getter(AccessLevel.NONE)
    private final @NotNull AtomicLong lastContentUpdate;

    /**
     * Creates a new instance of {@link DefaultHologram} with the given name.
     *
     * @param name     The name of the hologram.
     * @param location The location of the hologram.
     */
    public DefaultHologram(@NotNull String name, @NotNull Location location) {
        this(name, location, true, true);
    }

    /**
     * Creates a new instance of {@link DefaultHologram} with the given name.
     *
     * @param name       The name of the hologram.
     * @param location   The location of the hologram.
     * @param enabled    Whether the hologram is enabled or not.
     * @param persistent Whether the hologram is persistent.
     */
    public DefaultHologram(@NotNull String name, @NotNull Location location, boolean enabled, boolean persistent) {
        this.name = name;
        this.config = new DefaultHologramConfig(this);
        this.positionManager = new DefaultPositionManager(location);
        this.settings = new DefaultHologramSettings(false, persistent);
        this.visibilityManager = new DefaultHologramVisibilityManager(this);
        this.pages = new ArrayList<>();
        this.viewConditions = new ConditionHolder();
        this.lastVisibilityUpdate = new AtomicLong(0);
        this.lastContentUpdate = new AtomicLong(0);

        // Start the ticking.
        this.startTicking();

        // Load the hologram from the file.
        this.getConfig().reload().thenRun(() -> getSettings().setEnabled(enabled));
    }

    protected DefaultHologram(@NotNull String name, @NotNull Location location, @NotNull HologramSettings settings,
                              @NotNull ConditionHolder viewConditions) {
        this.name = name;
        this.config = new DefaultHologramConfig(this);
        this.positionManager = new DefaultPositionManager(location);
        this.settings = settings;
        this.visibilityManager = new DefaultHologramVisibilityManager(this);
        this.pages = new ArrayList<>();
        this.viewConditions = viewConditions;
        this.lastVisibilityUpdate = new AtomicLong(0);
        this.lastContentUpdate = new AtomicLong(0);

        // Start the ticking.
        this.startTicking();
    }

    @Override
    public void tick() {
        if (!getSettings().isEnabled()) {
            // Do not tick if the hologram is disabled.
            return;
        }

        long currentTime = System.currentTimeMillis();

        // If the location is bound, update the location.
        if (positionManager.isLocationBound()
                || settings.isRotateHorizontal()
                || settings.isRotateVertical()
                || settings.isRotateHeads()
        ) {
            getPages().forEach(HologramPage::recalculate);
        }

        // Update the visibility of the hologram if the time difference is greater than 500ms.
        if (500L < (currentTime - lastVisibilityUpdate.get())) {
            visibilityManager.updateVisibility();
            lastVisibilityUpdate.set(currentTime);
        }

        // Update the content of the hologram.
        if (settings.getUpdateInterval() * 50L < (currentTime - lastContentUpdate.get())) {
            visibilityManager.updateContents();
            lastContentUpdate.set(currentTime);
        }
    }

    @Override
    public void destroy() {
        this.stopTicking();
        visibilityManager.destroy();
    }

    @Override
    public HologramPage getPage(int index) {
        return pages.get(index);
    }

    @Override
    public int getIndex(@NotNull HologramPage page) {
        return pages.contains(page) ? getPages().indexOf(page) : -1;
    }

    @Override
    public void addPage(@NotNull HologramPage page) {
        pages.add(page);
    }

    @Override
    public void addPage(int index, @NotNull HologramPage page) {
        pages.add(index, page);

        // Shift the player page indexes in visibility manager.
        shiftPlayerPages(index, 1);
    }

    @Override
    public void removePage(int index) {
        pages.remove(index);

        // Shift the player page indexes in visibility manager.
        shiftPlayerPages(index, -1);
    }

    @Override
    public void clearPages() {
        pages.clear();

        // Reset the player page indexes in visibility manager to 0.
        visibilityManager.getPlayerPages().replaceAll((k, v) -> 0);
    }

    @Override
    public void setPages(@NotNull List<HologramPage> pages) {
        clearPages();
        this.pages.addAll(pages);
    }

    @NotNull
    @Override
    public List<HologramPage> getPages() {
        return ImmutableList.copyOf(pages);
    }

    /**
     * Shift the player page indexes in visibility manager by the given amount at the given index.
     *
     * @param index The index to start shifting from.
     * @param shift The amount to shift by.
     */
    private void shiftPlayerPages(int index, int shift) {
        for (Map.Entry<String, Integer> entry : visibilityManager.getPlayerPages().entrySet()) {
            if (entry.getValue() >= index) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    visibilityManager.setPage(player, entry.getValue() + shift);
                }
            }
        }
    }

}
