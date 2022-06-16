package eu.decent.holograms.api.component.page;

import eu.decent.holograms.api.component.common.IActionable;
import eu.decent.holograms.api.component.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a hologram page. A page is a collection of lines.
 *
 * @author d0by
 */
public interface Page extends IActionable {

    /**
     * Get the parent {@link Hologram} of this page.
     *
     * @return The parent {@link Hologram} of this page.
     */
    @NotNull
    Hologram getParent();

    /**
     * Get the {@link PageLineHolder} of this page.
     *
     * @return the {@link PageLineHolder} of this page.
     * @see PageLineHolder
     */
    @NotNull
    PageLineHolder getLineHolder();

    /**
     * Show this page to the specified player. This method displays all the lines
     * of this page to the player.
     *
     * @param player The player to show this page to.
     */
    void display(@NotNull Player player);

    /**
     * Hide this page from the specified player. This method hides all the lines
     * of this page from the player.
     *
     * @param player The player to hide this page from.
     */
    void hide(@NotNull Player player);

    /**
     * Update this page for the specified player. This method updates all the lines
     * of this page for the player.
     *
     * @param player The player to update this page for.
     */
    void update(@NotNull Player player);

}
