package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_363 extends class_364 {
	@Nullable
	class_364 getFocused();

	@Override
	default boolean method_16807(double d, double e, int i) {
		return this.getFocused() != null && this.getFocused().method_16807(d, e, i);
	}

	@Override
	default boolean method_16804(double d, double e, int i) {
		return this.getFocused() != null && this.getFocused().method_16804(d, e, i);
	}

	@Override
	default boolean method_16801(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.getFocused().method_16801(d, e, i, f, g);
	}

	@Override
	default boolean method_16802(double d) {
		return this.getFocused() != null && this.getFocused().method_16802(d);
	}

	@Override
	default boolean method_16805(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().method_16805(i, j, k);
	}

	@Override
	default boolean method_16803(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().method_16803(i, j, k);
	}

	@Override
	default boolean method_16806(char c, int i) {
		return this.getFocused() != null && this.getFocused().method_16806(c, i);
	}
}
