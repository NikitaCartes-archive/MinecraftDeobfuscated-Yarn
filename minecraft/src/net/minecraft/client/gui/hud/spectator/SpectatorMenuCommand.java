package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommand {
	void use(SpectatorMenu menu);

	Text getName();

	void renderIcon(MatrixStack matrices, float f, int i);

	boolean isEnabled();
}
