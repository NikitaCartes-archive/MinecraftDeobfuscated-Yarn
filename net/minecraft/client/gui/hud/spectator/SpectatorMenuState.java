/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenuState {
    private final SpectatorMenuCommandGroup group;
    private final List<SpectatorMenuCommand> commands;
    private final int selectedSlot;

    public SpectatorMenuState(SpectatorMenuCommandGroup spectatorMenuCommandGroup, List<SpectatorMenuCommand> list, int i) {
        this.group = spectatorMenuCommandGroup;
        this.commands = list;
        this.selectedSlot = i;
    }

    public SpectatorMenuCommand getCommand(int i) {
        if (i < 0 || i >= this.commands.size()) {
            return SpectatorMenu.BLANK_COMMAND;
        }
        return MoreObjects.firstNonNull(this.commands.get(i), SpectatorMenu.BLANK_COMMAND);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}

