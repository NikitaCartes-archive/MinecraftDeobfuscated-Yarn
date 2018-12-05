package net.minecraft.util;

import net.minecraft.text.TextComponent;

public interface Nameable {
	TextComponent getName();

	boolean hasCustomName();

	default TextComponent getDisplayName() {
		return this.getName();
	}
}
