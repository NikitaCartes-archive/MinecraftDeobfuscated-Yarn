package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a directional navigation initiated by keyboard.
 */
@Environment(EnvType.CLIENT)
public interface GuiNavigation {
	NavigationDirection getDirection();

	@Environment(EnvType.CLIENT)
	public static record Arrow(NavigationDirection direction) implements GuiNavigation {
		@Override
		public NavigationDirection getDirection() {
			return this.direction.getAxis() == NavigationAxis.VERTICAL ? this.direction : NavigationDirection.DOWN;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Down implements GuiNavigation {
		@Override
		public NavigationDirection getDirection() {
			return NavigationDirection.DOWN;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Tab(boolean forward) implements GuiNavigation {
		@Override
		public NavigationDirection getDirection() {
			return this.forward ? NavigationDirection.DOWN : NavigationDirection.UP;
		}
	}
}
