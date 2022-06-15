package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The types of cursor movement.
 */
@Environment(EnvType.CLIENT)
public enum CursorMovement {
	/**
	 * Cursor is moved using an absolute position.
	 */
	ABSOLUTE,
	/**
	 * Cursor is moved using a relative position.
	 */
	RELATIVE,
	/**
	 * Cursor is moved to the end of the text.
	 */
	END;
}
