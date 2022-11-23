package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Vector2ic;

@Environment(EnvType.CLIENT)
public interface TooltipPositioner {
	Vector2ic getPosition(Screen screen, int x, int y, int width, int height);
}
