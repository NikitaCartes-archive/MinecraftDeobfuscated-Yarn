package net.minecraft;

import javax.annotation.concurrent.Immutable;

@Immutable
public class class_1266 {
	private final class_1267 field_5798;
	private final float field_5799;

	public class_1266(class_1267 arg, long l, long m, float f) {
		this.field_5798 = arg;
		this.field_5799 = this.method_5456(arg, l, m, f);
	}

	public class_1267 method_5454() {
		return this.field_5798;
	}

	public float method_5457() {
		return this.field_5799;
	}

	public boolean method_5455(float f) {
		return this.field_5799 > f;
	}

	public float method_5458() {
		if (this.field_5799 < 2.0F) {
			return 0.0F;
		} else {
			return this.field_5799 > 4.0F ? 1.0F : (this.field_5799 - 2.0F) / 2.0F;
		}
	}

	private float method_5456(class_1267 arg, long l, long m, float f) {
		if (arg == class_1267.field_5801) {
			return 0.0F;
		} else {
			boolean bl = arg == class_1267.field_5807;
			float g = 0.75F;
			float h = class_3532.method_15363(((float)l + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
			g += h;
			float i = 0.0F;
			i += class_3532.method_15363((float)m / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
			i += class_3532.method_15363(f * 0.25F, 0.0F, h);
			if (arg == class_1267.field_5805) {
				i *= 0.5F;
			}

			g += i;
			return (float)arg.method_5461() * g;
		}
	}
}
