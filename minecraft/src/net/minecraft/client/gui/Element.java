package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Element {
	default void mouseMoved(double mouseX, double mouseY) {
	}

	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		return false;
	}

	default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return false;
	}

	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean charTyped(char chr, int keyCode) {
		return false;
	}

	default boolean changeFocus(boolean lookForwards) {
		return false;
	}

	default boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}
}
