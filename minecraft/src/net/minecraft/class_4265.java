package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4265<E extends class_4265.class_4266<E>> extends class_350<E> {
	public class_4265(class_310 arg, int i, int j, int k, int l, int m) {
		super(arg, i, j, k, l, m);
	}

	@Override
	protected boolean method_20057(int i) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_4266<E extends class_4265.class_4266<E>> extends class_350.class_351<E> implements class_4069 {
		@Nullable
		private class_364 field_19077;
		private boolean field_19078;

		@Override
		public boolean isDragging() {
			return this.field_19078;
		}

		@Override
		public void setDragging(boolean bl) {
			this.field_19078 = bl;
		}

		@Override
		public void setFocused(@Nullable class_364 arg) {
			this.field_19077 = arg;
		}

		@Nullable
		@Override
		public class_364 getFocused() {
			return this.field_19077;
		}

		@Override
		public boolean isPartOfFocusCycle() {
			return true;
		}
	}
}
