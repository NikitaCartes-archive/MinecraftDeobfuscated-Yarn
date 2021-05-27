package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Selectable extends Narratable {
	Selectable.SelectionType getType();

	default boolean method_37303() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static enum SelectionType {
		NONE,
		HOVERED,
		FOCUSED;

		public boolean isFocused() {
			return this == FOCUSED;
		}
	}
}
