package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector2ic;

@Environment(EnvType.CLIENT)
public interface TooltipPositioner {
	Vector2ic getPosition(int screenWidth, int screenHeight, int x, int y, int width, int height);
}
