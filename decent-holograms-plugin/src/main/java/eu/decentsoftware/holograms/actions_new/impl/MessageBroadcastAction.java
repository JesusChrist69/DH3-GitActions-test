package eu.decentsoftware.holograms.actions_new.impl;

import eu.decentsoftware.holograms.Lang;
import eu.decentsoftware.holograms.api.profile.Profile;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class MessageBroadcastAction extends MessageAction {

    public MessageBroadcastAction(@NotNull String message) {
        super(message);
    }

    public MessageBroadcastAction(long delay, double chance, @NotNull String message) {
        super(delay, chance, message);
    }

    @Override
    public void execute(@NotNull Profile profile) {
        Bukkit.broadcastMessage(Lang.formatString(message, profile));
    }

}
