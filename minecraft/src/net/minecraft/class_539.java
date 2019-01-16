package net.minecraft;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuElement;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuImpl;

@Environment(EnvType.CLIENT)
public class class_539 {
	private final SpectatorMenu field_3270;
	private final List<SpectatorMenuElement> field_3271;
	private final int field_3269;

	public class_539(SpectatorMenu spectatorMenu, List<SpectatorMenuElement> list, int i) {
		this.field_3270 = spectatorMenu;
		this.field_3271 = list;
		this.field_3269 = i;
	}

	public SpectatorMenuElement method_2786(int i) {
		return i >= 0 && i < this.field_3271.size()
			? MoreObjects.firstNonNull((SpectatorMenuElement)this.field_3271.get(i), SpectatorMenuImpl.field_3260)
			: SpectatorMenuImpl.field_3260;
	}

	public int method_2787() {
		return this.field_3269;
	}
}
