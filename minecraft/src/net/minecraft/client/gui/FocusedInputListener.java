package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface FocusedInputListener extends InputListener {
	@Nullable
	InputListener getFocused();

	@Override
	default boolean mouseClicked(double d, double e, int i) {
		return this.getFocused() != null && this.getFocused().mouseClicked(d, e, i);
	}

	@Override
	default boolean mouseReleased(double d, double e, int i) {
		return this.getFocused() != null && this.getFocused().mouseReleased(d, e, i);
	}

	@Override
	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.getFocused().mouseDragged(d, e, i, f, g);
	}

	@Override
	default boolean mouseScrolled(double d) {
		return this.getFocused() != null && this.getFocused().mouseScrolled(d);
	}

	@Override
	default boolean keyPressed(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().keyPressed(i, j, k);
	}

	@Override
	default boolean keyReleased(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().keyReleased(i, j, k);
	}

	@Override
	default boolean charTyped(char c, int i) {
		return this.getFocused() != null && this.getFocused().charTyped(c, i);
	}
}
