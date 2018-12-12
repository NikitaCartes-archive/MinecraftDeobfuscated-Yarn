package net.minecraft.client.gui;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class DrawableContainer extends Drawable implements InputListener {
	@Nullable
	private GuiEventListener focused;
	private boolean active;

	protected abstract List<? extends GuiEventListener> getEntries();

	private final boolean isActive() {
		return this.active;
	}

	protected final void setActive(boolean bl) {
		this.active = bl;
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.focused;
	}

	protected void setFocused(@Nullable GuiEventListener guiEventListener) {
		this.focused = guiEventListener;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		for (GuiEventListener guiEventListener : this.getEntries()) {
			boolean bl = guiEventListener.mouseClicked(d, e, i);
			if (bl) {
				this.focusOn(guiEventListener);
				if (i == 0) {
					this.setActive(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return InputListener.super.keyPressed(i, j, k);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.isActive() && i == 0 ? this.getFocused().mouseDragged(d, e, i, f, g) : false;
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		this.setActive(false);
		return InputListener.super.mouseReleased(d, e, i);
	}

	public void focusOn(@Nullable GuiEventListener guiEventListener) {
		this.switchFocus(guiEventListener, this.getEntries().indexOf(this.getFocused()));
	}

	public void focusNext() {
		int i = this.getEntries().indexOf(this.getFocused());
		int j = i == -1 ? 0 : (i + 1) % this.getEntries().size();
		this.switchFocus(this.getNextFocusable(j), i);
	}

	@Nullable
	private GuiEventListener getNextFocusable(int i) {
		List<? extends GuiEventListener> list = this.getEntries();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			GuiEventListener guiEventListener = (GuiEventListener)list.get((i + k) % j);
			if (guiEventListener.hasFocus()) {
				return guiEventListener;
			}
		}

		return null;
	}

	private void switchFocus(@Nullable GuiEventListener guiEventListener, int i) {
		GuiEventListener guiEventListener2 = i == -1 ? null : (GuiEventListener)this.getEntries().get(i);
		if (guiEventListener2 != guiEventListener) {
			if (guiEventListener2 != null) {
				guiEventListener2.setHasFocus(false);
			}

			if (guiEventListener != null) {
				guiEventListener.setHasFocus(true);
			}

			this.setFocused(guiEventListener);
		}
	}
}
