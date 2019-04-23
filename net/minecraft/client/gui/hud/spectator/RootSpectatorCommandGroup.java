/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeamTeleportSpectatorMenu;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(value=EnvType.CLIENT)
public class RootSpectatorCommandGroup
implements SpectatorMenuCommandGroup {
    private final List<SpectatorMenuCommand> elements = Lists.newArrayList();

    public RootSpectatorCommandGroup() {
        this.elements.add(new TeleportSpectatorMenu());
        this.elements.add(new TeamTeleportSpectatorMenu());
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }

    @Override
    public Component getPrompt() {
        return new TranslatableComponent("spectatorMenu.root.prompt", new Object[0]);
    }
}

