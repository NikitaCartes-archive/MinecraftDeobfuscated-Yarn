package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_362 extends class_332 implements class_4069 {
	@Nullable
	private class_364 focused;
	private boolean isDragging;

	@Override
	public final boolean isDragging() {
		return this.isDragging;
	}

	@Override
	public final void setDragging(boolean bl) {
		this.isDragging = bl;
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable class_364 arg) {
		this.focused = arg;
	}
}
