package net.minecraft.screen;

import net.minecraft.text.Text;

public interface NamedScreenHandlerFactory extends ScreenHandlerFactory {
	/**
	 * Returns the title of this screen handler; will be a part of the open
	 * screen packet sent to the client.
	 */
	Text getDisplayName();
}
