/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public interface SpectatorMenuCommandGroup {
    public List<SpectatorMenuCommand> getCommands();

    public Component getPrompt();
}

