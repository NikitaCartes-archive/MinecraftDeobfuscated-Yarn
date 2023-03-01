package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

/**
 * Represents the position of a {@link ScreenRect}.
 */
@Environment(EnvType.CLIENT)
public record ScreenPos(int x, int y) {
	public static ScreenPos of(NavigationAxis axis, int sameAxis, int otherAxis) {
		return switch (axis) {
			case HORIZONTAL -> new ScreenPos(sameAxis, otherAxis);
			case VERTICAL -> new ScreenPos(otherAxis, sameAxis);
		};
	}

	public ScreenPos add(NavigationDirection direction) {
		return switch (direction) {
			case DOWN -> new ScreenPos(this.x, this.y + 1);
			case UP -> new ScreenPos(this.x, this.y - 1);
			case LEFT -> new ScreenPos(this.x - 1, this.y);
			case RIGHT -> new ScreenPos(this.x + 1, this.y);
		};
	}

	public int getComponent(NavigationAxis axis) {
		return switch (axis) {
			case HORIZONTAL -> this.x;
			case VERTICAL -> this.y;
		};
	}
}
