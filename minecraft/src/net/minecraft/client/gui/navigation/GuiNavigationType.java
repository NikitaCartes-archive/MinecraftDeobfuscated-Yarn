package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * An enum representing the type of navigation input method.
 */
@Environment(EnvType.CLIENT)
public enum GuiNavigationType {
	NONE,
	MOUSE,
	KEYBOARD_ARROW,
	KEYBOARD_TAB;

	public boolean isMouse() {
		return this == MOUSE;
	}

	public boolean isKeyboard() {
		return this == KEYBOARD_ARROW || this == KEYBOARD_TAB;
	}
}
