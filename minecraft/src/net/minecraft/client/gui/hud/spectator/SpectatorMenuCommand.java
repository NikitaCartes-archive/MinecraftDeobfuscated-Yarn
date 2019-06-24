package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommand {
	void use(SpectatorMenu spectatorMenu);

	Text getName();

	void renderIcon(float f, int i);

	boolean isEnabled();
}
