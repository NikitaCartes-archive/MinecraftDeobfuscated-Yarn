package net.minecraft.client.gui.screen.narration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A component of a {@linkplain NarrationMessageBuilder narration message}.
 * This enum is mostly used for grouping and ordering narrations in a narration
 * message.
 */
@Environment(EnvType.CLIENT)
public enum NarrationPart {
	/**
	 * The main narration for a narrated element.
	 */
	TITLE,
	/**
	 * The position of a narrated element in a container such as a list.
	 */
	POSITION,
	/**
	 * A hint for a narrated element, e.g. a button tooltip.
	 */
	HINT,
	/**
	 * Usage instructions for a narrated element.
	 */
	USAGE;
}
