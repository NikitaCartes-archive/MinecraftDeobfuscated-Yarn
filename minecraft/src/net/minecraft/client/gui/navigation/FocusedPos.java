package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents the position of an {@link FocusedRect}.
 */
@Environment(EnvType.CLIENT)
public record FocusedPos(int x, int y) {
	public static FocusedPos of(NavigationAxis axis, int sameAxis, int otherAxis) {
		return switch (axis) {
			case HORIZONTAL -> new FocusedPos(sameAxis, otherAxis);
			case VERTICAL -> new FocusedPos(otherAxis, sameAxis);
		};
	}

	public FocusedPos add(NavigationDirection direction) {
		return switch (direction) {
			case DOWN -> new FocusedPos(this.x, this.y + 1);
			case UP -> new FocusedPos(this.x, this.y - 1);
			case LEFT -> new FocusedPos(this.x - 1, this.y);
			case RIGHT -> new FocusedPos(this.x + 1, this.y);
		};
	}

	public int getComponent(NavigationAxis axis) {
		return switch (axis) {
			case HORIZONTAL -> this.x;
			case VERTICAL -> this.y;
		};
	}
}
