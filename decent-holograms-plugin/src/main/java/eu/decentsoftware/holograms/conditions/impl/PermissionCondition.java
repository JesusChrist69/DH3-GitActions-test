package eu.decentsoftware.holograms.conditions.impl;

import eu.decentsoftware.holograms.api.profile.Profile;
import eu.decentsoftware.holograms.conditions.DefaultCondition;
import eu.decentsoftware.holograms.hooks.PAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a {@link DefaultCondition} that checks
 * whether the player has a specific permission.
 */
public class PermissionCondition extends DefaultCondition {

    private final @NotNull String permission;

    public PermissionCondition(@NotNull String permission) {
        this(false, permission);
    }

    public PermissionCondition(boolean inverted, @NotNull String permission) {
        super(inverted);
        this.permission = permission;
    }

    @Override
    public boolean check(@NotNull Profile profile) {
        Player player = profile.getPlayer();
        return player != null && player.hasPermission(PAPI.setPlaceholders(player, this.permission));
    }

}
