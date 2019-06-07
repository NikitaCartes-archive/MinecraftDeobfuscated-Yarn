package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.text.Text;

public interface Nameable {
	Text getName();

	default boolean hasCustomName() {
		return this.getCustomName() != null;
	}

	default Text getDisplayName() {
		return this.getName();
	}

	@Nullable
	default Text getCustomName() {
		return null;
	}
}
