package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface FocusedInputListener extends InputListener {
	@Nullable
	InputListener method_19357();

	@Override
	default boolean mouseClicked(double d, double e, int i) {
		InputListener inputListener = this.method_19355(d, e);
		return inputListener != null && inputListener.mouseClicked(d, e, i);
	}

	@Override
	default boolean mouseReleased(double d, double e, int i) {
		InputListener inputListener = this.method_19355(d, e);
		return inputListener != null && inputListener.mouseReleased(d, e, i);
	}

	@Override
	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		InputListener inputListener = this.method_19355(d, e);
		return inputListener != null && inputListener.mouseDragged(d, e, i, f, g);
	}

	@Override
	default boolean mouseScrolled(double d, double e, double f) {
		InputListener inputListener = this.method_19355(d, e);
		return inputListener != null && inputListener.mouseScrolled(d, e, f);
	}

	@Override
	default boolean keyPressed(int i, int j, int k) {
		return this.method_19357() != null && this.method_19357().keyPressed(i, j, k);
	}

	@Override
	default boolean keyReleased(int i, int j, int k) {
		return this.method_19357() != null && this.method_19357().keyReleased(i, j, k);
	}

	@Override
	default boolean charTyped(char c, int i) {
		return this.method_19357() != null && this.method_19357().charTyped(c, i);
	}
}
