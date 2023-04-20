package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@Environment(EnvType.CLIENT)
public class HoveredTooltipPositioner implements TooltipPositioner {
	public static final TooltipPositioner INSTANCE = new HoveredTooltipPositioner();

	private HoveredTooltipPositioner() {
	}

	@Override
	public Vector2ic getPosition(int screenWidth, int screenHeight, int x, int y, int width, int height) {
		Vector2i vector2i = new Vector2i(x, y).add(12, -12);
		this.preventOverflow(screenWidth, screenHeight, vector2i, width, height);
		return vector2i;
	}

	private void preventOverflow(int screenWidth, int screenHeight, Vector2i pos, int width, int height) {
		if (pos.x + width > screenWidth) {
			pos.x = Math.max(pos.x - 24 - width, 4);
		}

		int i = height + 3;
		if (pos.y + i > screenHeight) {
			pos.y = screenHeight - i;
		}
	}
}
