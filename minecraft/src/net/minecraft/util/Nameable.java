package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.text.Text;

public interface Nameable {
	Text method_5477();

	default boolean hasCustomName() {
		return this.method_5797() != null;
	}

	default Text method_5476() {
		return this.method_5477();
	}

	@Nullable
	default Text method_5797() {
		return null;
	}
}
