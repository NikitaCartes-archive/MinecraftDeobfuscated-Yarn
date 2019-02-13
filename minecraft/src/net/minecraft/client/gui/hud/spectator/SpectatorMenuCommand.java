package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommand {
	void use(SpectatorMenu spectatorMenu);

	TextComponent getName();

	void renderIcon(float f, int i);

	boolean enabled();
}
