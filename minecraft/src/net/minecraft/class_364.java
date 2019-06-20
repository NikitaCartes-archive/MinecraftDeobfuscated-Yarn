package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_364 {
	default void method_16014(double d, double e) {
	}

	default boolean mouseClicked(double d, double e, int i) {
		return false;
	}

	default boolean mouseReleased(double d, double e, int i) {
		return false;
	}

	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return false;
	}

	default boolean mouseScrolled(double d, double e, double f) {
		return false;
	}

	default boolean keyPressed(int i, int j, int k) {
		return false;
	}

	default boolean method_16803(int i, int j, int k) {
		return false;
	}

	default boolean charTyped(char c, int i) {
		return false;
	}

	default boolean changeFocus(boolean bl) {
		return false;
	}

	default boolean isMouseOver(double d, double e) {
		return false;
	}
}
