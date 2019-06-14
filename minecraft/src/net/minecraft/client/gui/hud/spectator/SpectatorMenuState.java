package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
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
		return i >= 0 && i < this.commands.size()
			? MoreObjects.firstNonNull((SpectatorMenuCommand)this.commands.get(i), SpectatorMenu.field_3260)
			: SpectatorMenu.field_3260;
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}
}
