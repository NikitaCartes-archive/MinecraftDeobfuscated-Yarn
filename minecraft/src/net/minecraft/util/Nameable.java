package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;

public interface Nameable {
	TextComponent getName();

	default boolean hasCustomName() {
		return this.getCustomName() != null;
	}

	default TextComponent getDisplayName() {
		return this.getName();
	}

	@Nullable
	default TextComponent getCustomName() {
		return null;
	}
}
