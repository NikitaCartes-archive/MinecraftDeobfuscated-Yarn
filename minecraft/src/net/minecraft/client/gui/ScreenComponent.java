package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ScreenComponent extends DrawableHelper implements MultiInputListener {
	@Nullable
	private InputListener focused;
	private boolean field_2195;

	@Override
	public final boolean method_1970() {
		return this.field_2195;
	}

	@Override
	public final void method_1966(boolean bl) {
		this.field_2195 = bl;
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
