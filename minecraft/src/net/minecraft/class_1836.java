package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_1836 {
	boolean method_8035();

	@Environment(EnvType.CLIENT)
	public static enum class_1837 implements class_1836 {
		field_8934(false),
		field_8935(true);

		private final boolean field_8936;

		private class_1837(boolean bl) {
			this.field_8936 = bl;
		}

		@Override
		public boolean method_8035() {
			return this.field_8936;
		}
	}
}
