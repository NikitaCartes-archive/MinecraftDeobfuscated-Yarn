package net.minecraft.client.gui;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public interface MultiInputListener extends FocusedInputListener {
	List<? extends InputListener> getInputListeners();

	boolean isActive();

	void setActive(boolean bl);

	void setFocused(@Nullable InputListener inputListener);

	@Nullable
	@Override
	default InputListener method_19355(double d, double e) {
		for (InputListener inputListener : this.getInputListeners()) {
			if (inputListener.isMouseOver(d, e)) {
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
				this.focusOn(inputListener);
				if (i == 0) {
					this.setActive(true);
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
		return this.getFocused() != null && this.isActive() && i == 0 ? this.getFocused().mouseDragged(d, e, i, f, g) : false;
	}

	@Override
	default boolean mouseReleased(double d, double e, int i) {
		this.setActive(false);
		return FocusedInputListener.super.mouseReleased(d, e, i);
	}

	default void focusOn(@Nullable InputListener inputListener) {
		this.setFocused(inputListener, this.getInputListeners().indexOf(this.getFocused()));
	}

	default void focusNext() {
		int i = this.getInputListeners().indexOf(this.getFocused());
		int j = i == -1 ? 0 : (i + 1) % this.getInputListeners().size();
		this.setFocused(this.getFocused(j), i);
	}

	default void focusPrevious() {
		int i = this.getInputListeners().indexOf(this.getFocused());
		int j = i == -1 ? this.getInputListeners().size() - 1 : MathHelper.floorMod(i - 1, this.getInputListeners().size());
		this.setFocused(this.method_19353(j), i);
	}

	@Nullable
	default InputListener getFocused(int i) {
		List<? extends InputListener> list = this.getInputListeners();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			InputListener inputListener = (InputListener)list.get((i + k) % j);
			if (inputListener.isPartOfFocusCycle()) {
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
			if (inputListener.isPartOfFocusCycle()) {
				return inputListener;
			}
		}

		return null;
	}

	default void setFocused(@Nullable InputListener inputListener, int i) {
		InputListener inputListener2 = i == -1 ? null : (InputListener)this.getInputListeners().get(i);
		if (inputListener2 != inputListener) {
			if (inputListener2 != null) {
				inputListener2.onFocusChanged(false);
			}

			if (inputListener != null) {
				inputListener.onFocusChanged(true);
			}

			this.setFocused(inputListener);
		}
	}
}
