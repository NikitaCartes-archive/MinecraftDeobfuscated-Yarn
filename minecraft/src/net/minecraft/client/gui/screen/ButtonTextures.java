package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ButtonTextures(Identifier enabled, Identifier disabled, Identifier enabledFocused, Identifier disabledFocused) {
	public ButtonTextures(Identifier unfocused, Identifier focused) {
		this(unfocused, unfocused, focused, focused);
	}

	public ButtonTextures(Identifier enabled, Identifier focused, Identifier disabled) {
		this(enabled, focused, disabled, focused);
	}

	public Identifier get(boolean enabled, boolean focused) {
		if (enabled) {
			return focused ? this.enabledFocused : this.enabled;
		} else {
			return focused ? this.disabledFocused : this.disabled;
		}
	}
}
