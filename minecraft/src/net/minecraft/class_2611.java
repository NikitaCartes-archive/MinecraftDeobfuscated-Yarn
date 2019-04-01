package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_2618.class
	)})
public class class_2611 extends class_2586 implements class_2618, class_3000 {
	public float field_12007;
	public float field_12004;
	public int field_12006;
	private int field_12005;

	public class_2611() {
		super(class_2591.field_11901);
	}

	@Override
	public void method_16896() {
		if (++this.field_12005 % 20 * 4 == 0) {
			this.field_11863.method_8427(this.field_11867, class_2246.field_10443, 1, this.field_12006);
		}

		this.field_12004 = this.field_12007;
		int i = this.field_11867.method_10263();
		int j = this.field_11867.method_10264();
		int k = this.field_11867.method_10260();
		float f = 0.1F;
		if (this.field_12006 > 0 && this.field_12007 == 0.0F) {
			double d = (double)i + 0.5;
			double e = (double)k + 0.5;
			this.field_11863
				.method_8465(null, d, (double)j + 0.5, e, class_3417.field_14952, class_3419.field_15245, 0.5F, this.field_11863.field_9229.nextFloat() * 0.1F + 0.9F);
		}

		if (this.field_12006 == 0 && this.field_12007 > 0.0F || this.field_12006 > 0 && this.field_12007 < 1.0F) {
			float g = this.field_12007;
			if (this.field_12006 > 0) {
				this.field_12007 += 0.1F;
			} else {
				this.field_12007 -= 0.1F;
			}

			if (this.field_12007 > 1.0F) {
				this.field_12007 = 1.0F;
			}

			float h = 0.5F;
			if (this.field_12007 < 0.5F && g >= 0.5F) {
				double e = (double)i + 0.5;
				double l = (double)k + 0.5;
				this.field_11863
					.method_8465(null, e, (double)j + 0.5, l, class_3417.field_15206, class_3419.field_15245, 0.5F, this.field_11863.field_9229.nextFloat() * 0.1F + 0.9F);
			}

			if (this.field_12007 < 0.0F) {
				this.field_12007 = 0.0F;
			}
		}
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.field_12006 = j;
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public void method_11012() {
		this.method_11000();
		super.method_11012();
	}

	public void method_11219() {
		this.field_12006++;
		this.field_11863.method_8427(this.field_11867, class_2246.field_10443, 1, this.field_12006);
	}

	public void method_11220() {
		this.field_12006--;
		this.field_11863.method_8427(this.field_11867, class_2246.field_10443, 1, this.field_12006);
	}

	public boolean method_11218(class_1657 arg) {
		return this.field_11863.method_8321(this.field_11867) != this
			? false
			: !(
				arg.method_5649((double)this.field_11867.method_10263() + 0.5, (double)this.field_11867.method_10264() + 0.5, (double)this.field_11867.method_10260() + 0.5)
					> 64.0
			);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_11274(float f) {
		return class_3532.method_16439(f, this.field_12004, this.field_12007);
	}
}
