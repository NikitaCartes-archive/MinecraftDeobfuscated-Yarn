package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Drawable {
	void render(DrawContext context, int mouseX, int mouseY, float delta);
}
