package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.InputListener;

@Environment(EnvType.CLIENT)
public interface class_4069 extends InputListener {
	List<? extends GuiEventListener> method_1968();

	boolean method_1970();

	void method_1966(boolean bl);

	void method_1967(@Nullable GuiEventListener guiEventListener);

	@Override
	default boolean mouseClicked(double d, double e, int i) {
		for (GuiEventListener guiEventListener : this.method_1968()) {
			boolean bl = guiEventListener.mouseClicked(d, e, i);
			if (bl) {
				this.method_18624(guiEventListener);
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
		return InputListener.super.keyPressed(i, j, k);
	}

	@Override
	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.method_1970() && i == 0 ? this.getFocused().mouseDragged(d, e, i, f, g) : false;
	}

	@Override
	default boolean mouseReleased(double d, double e, int i) {
		this.method_1966(false);
		return InputListener.super.mouseReleased(d, e, i);
	}

	default void method_18624(@Nullable GuiEventListener guiEventListener) {
		this.method_18623(guiEventListener, this.method_1968().indexOf(this.getFocused()));
	}

	default void method_18626() {
		int i = this.method_1968().indexOf(this.getFocused());
		int j = i == -1 ? 0 : (i + 1) % this.method_1968().size();
		this.method_18623(this.method_18625(j), i);
	}

	@Nullable
	default GuiEventListener method_18625(int i) {
		List<? extends GuiEventListener> list = this.method_1968();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			GuiEventListener guiEventListener = (GuiEventListener)list.get((i + k) % j);
			if (guiEventListener.hasFocus()) {
				return guiEventListener;
			}
		}

		return null;
	}

	default void method_18623(@Nullable GuiEventListener guiEventListener, int i) {
		GuiEventListener guiEventListener2 = i == -1 ? null : (GuiEventListener)this.method_1968().get(i);
		if (guiEventListener2 != guiEventListener) {
			if (guiEventListener2 != null) {
				guiEventListener2.setHasFocus(false);
			}

			if (guiEventListener != null) {
				guiEventListener.setHasFocus(true);
			}

			this.method_1967(guiEventListener);
		}
	}
}
