package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum NavigationAxis {
	HORIZONTAL,
	VERTICAL;

	public NavigationAxis getOther() {
		return switch (this) {
			case HORIZONTAL -> VERTICAL;
			case VERTICAL -> HORIZONTAL;
		};
	}

	public NavigationDirection getPositiveDirection() {
		return switch (this) {
			case HORIZONTAL -> NavigationDirection.RIGHT;
			case VERTICAL -> NavigationDirection.DOWN;
		};
	}

	public NavigationDirection getNegativeDirection() {
		return switch (this) {
			case HORIZONTAL -> NavigationDirection.LEFT;
			case VERTICAL -> NavigationDirection.UP;
		};
	}

	public NavigationDirection getDirection(boolean positive) {
		return positive ? this.getPositiveDirection() : this.getNegativeDirection();
	}
}
