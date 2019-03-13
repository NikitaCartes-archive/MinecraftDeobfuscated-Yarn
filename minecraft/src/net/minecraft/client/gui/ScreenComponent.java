package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ScreenComponent extends DrawableHelper implements MultiInputListener {
	@Nullable
	private InputListener field_2196;
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
	public InputListener method_19357() {
		return this.field_2196;
	}

	@Override
	public void method_1967(@Nullable InputListener inputListener) {
		this.field_2196 = inputListener;
	}
}
