package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.Navigable;

@Environment(EnvType.CLIENT)
public interface Selectable extends Navigable, Narratable {
	Selectable.SelectionType getType();

	default boolean isNarratable() {
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
