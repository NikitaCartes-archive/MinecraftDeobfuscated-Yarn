package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_3221 implements class_1938 {
	private final MinecraftServer field_13962;
	private final class_3218 field_13961;

	public class_3221(MinecraftServer minecraftServer, class_3218 arg) {
		this.field_13962 = minecraftServer;
		this.field_13961 = arg;
	}

	@Override
	public void method_8568(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_8563(class_2394 arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_8561(class_1297 arg) {
		this.field_13961.method_14180().method_14066(arg);
		if (arg instanceof class_3222) {
			this.field_13961.field_9247.method_12457((class_3222)arg);
		}
	}

	@Override
	public void method_8566(class_1297 arg) {
		this.field_13961.method_14180().method_14068(arg);
		this.field_13961.method_14170().method_1150(arg);
		if (arg instanceof class_3222) {
			this.field_13961.field_9247.method_12458((class_3222)arg);
		}
	}

	@Override
	public void method_8572(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, double d, double e, double f, float g, float h) {
		this.field_13962
			.method_3760()
			.method_14605(arg, d, e, f, g > 1.0F ? (double)(16.0F * g) : 16.0, this.field_13961.field_9247.method_12460(), new class_2767(arg2, arg3, d, e, f, g, h));
	}

	@Override
	public void method_8565(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, class_1297 arg4, float f, float g) {
		this.field_13962
			.method_3760()
			.method_14605(
				arg,
				arg4.field_5987,
				arg4.field_6010,
				arg4.field_6035,
				f > 1.0F ? (double)(16.0F * f) : 16.0,
				this.field_13961.field_9247.method_12460(),
				new class_2765(arg2, arg3, arg4, f, g)
			);
	}

	@Override
	public void method_8570(class_1922 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4, int i) {
		this.field_13961.method_14178().method_14128(arg2);
	}

	@Override
	public void method_8562(class_3414 arg, class_2338 arg2) {
	}

	@Override
	public void method_8567(class_1657 arg, int i, class_2338 arg2, int j) {
		this.field_13962
			.method_3760()
			.method_14605(
				arg,
				(double)arg2.method_10263(),
				(double)arg2.method_10264(),
				(double)arg2.method_10260(),
				64.0,
				this.field_13961.field_9247.method_12460(),
				new class_2673(i, arg2, j, false)
			);
	}

	@Override
	public void method_8564(int i, class_2338 arg, int j) {
		this.field_13962.method_3760().method_14581(new class_2673(i, arg, j, true));
	}

	@Override
	public void method_8569(int i, class_2338 arg, int j) {
		for (class_3222 lv : this.field_13962.method_3760().method_14571()) {
			if (lv != null && lv.field_6002 == this.field_13961 && lv.method_5628() != i) {
				double d = (double)arg.method_10263() - lv.field_5987;
				double e = (double)arg.method_10264() - lv.field_6010;
				double f = (double)arg.method_10260() - lv.field_6035;
				if (d * d + e * e + f * f < 1024.0) {
					lv.field_13987.method_14364(new class_2620(i, arg, j));
				}
			}
		}
	}
}
