package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1532 extends class_1530 {
	public class_1532(class_1937 arg) {
		super(class_1299.field_6138, arg);
	}

	public class_1532(class_1937 arg, class_2338 arg2) {
		super(class_1299.field_6138, arg, arg2);
		this.method_5814((double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 0.5, (double)arg2.method_10260() + 0.5);
		float f = 0.125F;
		float g = 0.1875F;
		float h = 0.25F;
		this.method_5857(
			new class_238(
				this.field_5987 - 0.1875,
				this.field_6010 - 0.25 + 0.125,
				this.field_6035 - 0.1875,
				this.field_5987 + 0.1875,
				this.field_6010 + 0.25 + 0.125,
				this.field_6035 + 0.1875
			)
		);
		this.field_5983 = true;
	}

	@Override
	public void method_5814(double d, double e, double f) {
		super.method_5814((double)class_3532.method_15357(d) + 0.5, (double)class_3532.method_15357(e) + 0.5, (double)class_3532.method_15357(f) + 0.5);
	}

	@Override
	protected void method_6895() {
		this.field_5987 = (double)this.field_7100.method_10263() + 0.5;
		this.field_6010 = (double)this.field_7100.method_10264() + 0.5;
		this.field_6035 = (double)this.field_7100.method_10260() + 0.5;
	}

	@Override
	public void method_6892(class_2350 arg) {
	}

	@Override
	public int method_6897() {
		return 9;
	}

	@Override
	public int method_6891() {
		return 9;
	}

	@Override
	public float method_5751() {
		return -0.0625F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		return d < 1024.0;
	}

	@Override
	public void method_6889(@Nullable class_1297 arg) {
		this.method_5783(class_3417.field_15184, 1.0F, 1.0F);
	}

	@Override
	public void method_5652(class_2487 arg) {
	}

	@Override
	public void method_5749(class_2487 arg) {
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		if (this.field_6002.field_9236) {
			return true;
		} else {
			boolean bl = false;
			double d = 7.0;
			List<class_1308> list = this.field_6002
				.method_8403(
					class_1308.class,
					new class_238(this.field_5987 - 7.0, this.field_6010 - 7.0, this.field_6035 - 7.0, this.field_5987 + 7.0, this.field_6010 + 7.0, this.field_6035 + 7.0)
				);

			for (class_1308 lv : list) {
				if (lv.method_5934() && lv.method_5933() == arg) {
					lv.method_5954(this, true);
					bl = true;
				}
			}

			if (!bl) {
				this.method_5650();
				if (arg.field_7503.field_7477) {
					for (class_1308 lvx : list) {
						if (lvx.method_5934() && lvx.method_5933() == this) {
							lvx.method_5932(true, false);
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean method_6888() {
		return this.field_6002.method_8320(this.field_7100).method_11614().method_9525(class_3481.field_16584);
	}

	public static class_1532 method_6931(class_1937 arg, class_2338 arg2) {
		class_1532 lv = new class_1532(arg, arg2);
		arg.method_8649(lv);
		lv.method_6894();
		return lv;
	}

	@Nullable
	public static class_1532 method_6932(class_1937 arg, class_2338 arg2) {
		int i = arg2.method_10263();
		int j = arg2.method_10264();
		int k = arg2.method_10260();

		for (class_1532 lv : arg.method_8403(
			class_1532.class, new class_238((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
		)) {
			if (lv.method_6896().equals(arg2)) {
				return lv;
			}
		}

		return null;
	}

	@Override
	public void method_6894() {
		this.method_5783(class_3417.field_15062, 1.0F, 1.0F);
	}
}
