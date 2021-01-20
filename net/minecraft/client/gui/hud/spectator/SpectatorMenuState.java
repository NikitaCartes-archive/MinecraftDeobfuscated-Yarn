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

@Environment(value=EnvType.CLIENT)
public class SpectatorMenuState {
    private final List<SpectatorMenuCommand> commands;
    private final int selectedSlot;

    public SpectatorMenuState(List<SpectatorMenuCommand> commands, int selectedSlot) {
        this.commands = commands;
        this.selectedSlot = selectedSlot;
    }

    public SpectatorMenuCommand getCommand(int slot) {
        if (slot < 0 || slot >= this.commands.size()) {
            return SpectatorMenu.BLANK_COMMAND;
        }
        return MoreObjects.firstNonNull(this.commands.get(slot), SpectatorMenu.BLANK_COMMAND);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}

