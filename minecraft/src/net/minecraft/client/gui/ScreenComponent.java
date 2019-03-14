package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ScreenComponent extends DrawableHelper implements MultiInputListener {
	@Nullable
	private InputListener focused;
	private boolean active;

	@Override
	public final boolean isActive() {
		return this.active;
	}

	@Override
	public final void setActive(boolean bl) {
		this.active = bl;
	}

	@Nullable
	@Override
	public InputListener getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable InputListener inputListener) {
		this.focused = inputListener;
	}
}
