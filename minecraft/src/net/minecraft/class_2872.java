package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2872 extends class_2869 {
	public class_2872(class_1937 arg, class_2874 arg2) {
		super(arg, arg2);
		this.field_13057 = true;
		this.field_13056 = true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_243 method_12445(float f, float g) {
		return new class_243(0.2F, 0.03F, 0.03F);
	}

	@Override
	protected void method_12447() {
		float f = 0.1F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.field_13053[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 0.9F + 0.1F;
		}
	}

	@Override
	public class_2794<?> method_12443() {
		class_2900 lv = class_2798.field_12765.method_12117();
		lv.method_12571(class_2246.field_10515.method_9564());
		lv.method_12572(class_2246.field_10164.method_9564());
		return class_2798.field_12765
			.create(this.field_13058, class_1969.field_9401.method_8772(class_1969.field_9401.method_8774().method_8782(class_1972.field_9461)), lv);
	}

	@Override
	public boolean method_12462() {
		return false;
	}

	@Nullable
	@Override
	public class_2338 method_12452(class_1923 arg, boolean bl) {
		return null;
	}

	@Nullable
	@Override
	public class_2338 method_12444(int i, int j, boolean bl) {
		return null;
	}

	@Override
	public float method_12464(long l, float f) {
		return 0.5F;
	}

	@Override
	public boolean method_12448() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_12453(int i, int j) {
		return true;
	}

	@Override
	public class_2784 method_12463() {
		return new class_2784() {
			@Override
			public double method_11964() {
				return super.method_11964() / 8.0;
			}

			@Override
			public double method_11980() {
				return super.method_11980() / 8.0;
			}
		};
	}

	@Override
	public class_2874 method_12460() {
		return class_2874.field_13076;
	}
}
