package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpectatorMenuState {
	public static final int field_32444 = -1;
	private final List<SpectatorMenuCommand> commands;
	private final int selectedSlot;

	public SpectatorMenuState(List<SpectatorMenuCommand> commands, int selectedSlot) {
		this.commands = commands;
		this.selectedSlot = selectedSlot;
	}

	public SpectatorMenuCommand getCommand(int slot) {
		return slot >= 0 && slot < this.commands.size()
			? MoreObjects.firstNonNull((SpectatorMenuCommand)this.commands.get(slot), SpectatorMenu.BLANK_COMMAND)
			: SpectatorMenu.BLANK_COMMAND;
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}
}
