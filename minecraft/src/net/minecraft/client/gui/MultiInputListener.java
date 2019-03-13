package net.minecraft.client.gui;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public interface MultiInputListener extends FocusedInputListener {
	List<? extends InputListener> getInputListeners();

	boolean method_1970();

	void method_1966(boolean bl);

	void method_1967(@Nullable InputListener inputListener);

	@Nullable
	@Override
	default InputListener method_19355(double d, double e) {
		for (InputListener inputListener : this.getInputListeners()) {
			if (inputListener.method_19356(d, e)) {
				return inputListener;
			}
		}

		return null;
	}

	@Override
	default boolean mouseClicked(double d, double e, int i) {
		for (InputListener inputListener : this.getInputListeners()) {
			boolean bl = inputListener.mouseClicked(d, e, i);
			if (bl) {
				this.method_18624(inputListener);
				if (i == 0) {
					this.method_1966(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	default boolean keyPressed(int i, int j, int k) {
		return FocusedInputListener.super.keyPressed(i, j, k);
	}

	@Override
	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.method_19357() != null && this.method_1970() && i == 0 ? this.method_19357().mouseDragged(d, e, i, f, g) : false;
	}

	@Override
	default boolean mouseReleased(double d, double e, int i) {
		this.method_1966(false);
		return FocusedInputListener.super.mouseReleased(d, e, i);
	}

	default void method_18624(@Nullable InputListener inputListener) {
		this.method_18623(inputListener, this.getInputListeners().indexOf(this.method_19357()));
	}

	default void focusNext() {
		int i = this.getInputListeners().indexOf(this.method_19357());
		int j = i == -1 ? 0 : (i + 1) % this.getInputListeners().size();
		this.method_18623(this.method_18625(j), i);
	}

	default void method_19354() {
		int i = this.getInputListeners().indexOf(this.method_19357());
		int j = i == -1 ? this.getInputListeners().size() - 1 : MathHelper.floorMod(i - 1, this.getInputListeners().size());
		this.method_18623(this.method_19353(j), i);
	}

	@Nullable
	default InputListener method_18625(int i) {
		List<? extends InputListener> list = this.getInputListeners();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			InputListener inputListener = (InputListener)list.get((i + k) % j);
			if (inputListener.hasFocus()) {
				return inputListener;
			}
		}

		return null;
	}

	@Nullable
	default InputListener method_19353(int i) {
		List<? extends InputListener> list = this.getInputListeners();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			InputListener inputListener = (InputListener)list.get(MathHelper.floorMod(i - k, j));
			if (inputListener.hasFocus()) {
				return inputListener;
			}
		}

		return null;
	}

	default void method_18623(@Nullable InputListener inputListener, int i) {
		InputListener inputListener2 = i == -1 ? null : (InputListener)this.getInputListeners().get(i);
		if (inputListener2 != inputListener) {
			if (inputListener2 != null) {
				inputListener2.setHasFocus(false);
			}

			if (inputListener != null) {
				inputListener.setHasFocus(true);
			}

			this.method_1967(inputListener);
		}
	}
}
