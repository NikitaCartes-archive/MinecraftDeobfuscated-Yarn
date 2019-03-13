package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;

public interface Nameable {
	TextComponent method_5477();

	default boolean hasCustomName() {
		return this.method_5797() != null;
	}

	default TextComponent method_5476() {
		return this.method_5477();
	}

	@Nullable
	default TextComponent method_5797() {
		return null;
	}
}
