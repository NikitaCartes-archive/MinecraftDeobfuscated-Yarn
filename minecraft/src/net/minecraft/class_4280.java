package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4280<E extends class_350.class_351<E>> extends class_350<E> {
	private boolean inFocus;

	public class_4280(class_310 arg, int i, int j, int k, int l, int m) {
		super(arg, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean bl) {
		if (!this.inFocus && this.getItemCount() == 0) {
			return false;
		} else {
			this.inFocus = !this.inFocus;
			if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
				this.moveSelection(1);
			} else if (this.inFocus && this.getSelected() != null) {
				this.moveSelection(0);
			}

			return this.inFocus;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_4281<E extends class_4280.class_4281<E>> extends class_350.class_351<E> {
		@Override
		public boolean changeFocus(boolean bl) {
			return false;
		}
	}
}
