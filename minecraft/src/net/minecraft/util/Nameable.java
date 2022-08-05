package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.text.Text;

/**
 * Represents something that can be named, such as block entities or entities.
 */
public interface Nameable {
	/**
	 * {@return the name of this object}
	 * 
	 * <p>This should return {@linkplain #getCustomName the custom name} if it exists,
	 * otherwise the default name. This should not have styling applied.
	 */
	Text getName();

	/**
	 * {@return whether this object has a custom name}
	 */
	default boolean hasCustomName() {
		return this.getCustomName() != null;
	}

	/**
	 * {@return the display name of this object}
	 * 
	 * <p>By default, this returns the result of {@link #getName}. The return value can
	 * have styling applied.
	 */
	default Text getDisplayName() {
		return this.getName();
	}

	/**
	 * {@return the custom name of this object, or {@code null} if there is none}
	 */
	@Nullable
	default Text getCustomName() {
		return null;
	}
}
