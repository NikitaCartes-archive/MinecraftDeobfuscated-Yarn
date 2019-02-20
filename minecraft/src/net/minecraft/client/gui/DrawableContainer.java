package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4069;

@Environment(EnvType.CLIENT)
public abstract class DrawableContainer extends Drawable implements class_4069 {
	@Nullable
	private GuiEventListener focused;
	private boolean active;

	@Override
	public final boolean method_1970() {
		return this.active;
	}

	@Override
	public final void method_1966(boolean bl) {
		this.active = bl;
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.focused;
	}

	@Override
	public void method_1967(@Nullable GuiEventListener guiEventListener) {
		this.focused = guiEventListener;
	}
}
