package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2605 extends class_2586 implements class_1275, class_3000 {
	public int field_11961;
	public float field_11958;
	public float field_11960;
	public float field_11969;
	public float field_11967;
	public float field_11966;
	public float field_11965;
	public float field_11964;
	public float field_11963;
	public float field_11962;
	private static final Random field_11968 = new Random();
	private class_2561 field_11959;

	public class_2605() {
		super(class_2591.field_11912);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (this.method_16914()) {
			arg.method_10582("CustomName", class_2561.class_2562.method_10867(this.field_11959));
		}

		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		if (arg.method_10573("CustomName", 8)) {
			this.field_11959 = class_2561.class_2562.method_10877(arg.method_10558("CustomName"));
		}
	}

	@Override
	public void method_16896() {
		this.field_11965 = this.field_11966;
		this.field_11963 = this.field_11964;
		class_1657 lv = this.field_11863
			.method_8620(
				(double)((float)this.field_11867.method_10263() + 0.5F),
				(double)((float)this.field_11867.method_10264() + 0.5F),
				(double)((float)this.field_11867.method_10260() + 0.5F),
				3.0,
				false
			);
		if (lv != null) {
			double d = lv.field_5987 - (double)((float)this.field_11867.method_10263() + 0.5F);
			double e = lv.field_6035 - (double)((float)this.field_11867.method_10260() + 0.5F);
			this.field_11962 = (float)class_3532.method_15349(e, d);
			this.field_11966 += 0.1F;
			if (this.field_11966 < 0.5F || field_11968.nextInt(40) == 0) {
				float f = this.field_11969;

				do {
					this.field_11969 = this.field_11969 + (float)(field_11968.nextInt(4) - field_11968.nextInt(4));
				} while (f == this.field_11969);
			}
		} else {
			this.field_11962 += 0.02F;
			this.field_11966 -= 0.1F;
		}

		while (this.field_11964 >= (float) Math.PI) {
			this.field_11964 -= (float) (Math.PI * 2);
		}

		while (this.field_11964 < (float) -Math.PI) {
			this.field_11964 += (float) (Math.PI * 2);
		}

		while (this.field_11962 >= (float) Math.PI) {
			this.field_11962 -= (float) (Math.PI * 2);
		}

		while (this.field_11962 < (float) -Math.PI) {
			this.field_11962 += (float) (Math.PI * 2);
		}

		float g = this.field_11962 - this.field_11964;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		this.field_11964 += g * 0.4F;
		this.field_11966 = class_3532.method_15363(this.field_11966, 0.0F, 1.0F);
		this.field_11961++;
		this.field_11960 = this.field_11958;
		float h = (this.field_11969 - this.field_11958) * 0.4F;
		float i = 0.2F;
		h = class_3532.method_15363(h, -0.2F, 0.2F);
		this.field_11967 = this.field_11967 + (h - this.field_11967) * 0.9F;
		this.field_11958 = this.field_11958 + this.field_11967;
	}

	@Override
	public class_2561 method_5477() {
		return (class_2561)(this.field_11959 != null ? this.field_11959 : new class_2588("container.enchant"));
	}

	public void method_11179(@Nullable class_2561 arg) {
		this.field_11959 = arg;
	}

	@Nullable
	@Override
	public class_2561 method_5797() {
		return this.field_11959;
	}
}
