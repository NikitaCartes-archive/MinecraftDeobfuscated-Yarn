package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface Drawable {
	void render(MatrixStack matrices, int mouseX, int mouseY, float delta);
}
