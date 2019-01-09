package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_368 {
	class_2960 field_2207 = new class_2960("textures/gui/toasts.png");
	Object field_2208 = new Object();

	class_368.class_369 method_1986(class_374 arg, long l);

	default Object method_1987() {
		return field_2208;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_369 {
		field_2210(class_3417.field_14561),
		field_2209(class_3417.field_14641);

		private final class_3414 field_2211;

		private class_369(class_3414 arg) {
			this.field_2211 = arg;
		}

		public void method_1988(class_1144 arg) {
			arg.method_4873(class_1109.method_4757(this.field_2211, 1.0F, 1.0F));
		}
	}
}
