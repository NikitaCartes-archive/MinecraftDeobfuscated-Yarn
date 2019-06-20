package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_2618.class
	)})
public class class_2595 extends class_2621 implements class_2618, class_3000 {
	private class_2371<class_1799> field_11927 = class_2371.method_10213(27, class_1799.field_8037);
	protected float field_11929;
	protected float field_11926;
	protected int field_11928;
	private int field_11930;

	protected class_2595(class_2591<?> arg) {
		super(arg);
	}

	public class_2595() {
		this(class_2591.field_11914);
	}

	@Override
	public int method_5439() {
		return 27;
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_11927) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected class_2561 method_17823() {
		return new class_2588("container.chest");
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11927 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		if (!this.method_11283(arg)) {
			class_1262.method_5429(arg, this.field_11927);
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (!this.method_11286(arg)) {
			class_1262.method_5426(arg, this.field_11927);
		}

		return arg;
	}

	@Override
	public void method_16896() {
		int i = this.field_11867.method_10263();
		int j = this.field_11867.method_10264();
		int k = this.field_11867.method_10260();
		this.field_11930++;
		this.field_11928 = method_20364(this.field_11863, this, this.field_11930, i, j, k, this.field_11928);
		this.field_11926 = this.field_11929;
		float f = 0.1F;
		if (this.field_11928 > 0 && this.field_11929 == 0.0F) {
			this.method_11050(class_3417.field_14982);
		}

		if (this.field_11928 == 0 && this.field_11929 > 0.0F || this.field_11928 > 0 && this.field_11929 < 1.0F) {
			float g = this.field_11929;
			if (this.field_11928 > 0) {
				this.field_11929 += 0.1F;
			} else {
				this.field_11929 -= 0.1F;
			}

			if (this.field_11929 > 1.0F) {
				this.field_11929 = 1.0F;
			}

			float h = 0.5F;
			if (this.field_11929 < 0.5F && g >= 0.5F) {
				this.method_11050(class_3417.field_14823);
			}

			if (this.field_11929 < 0.0F) {
				this.field_11929 = 0.0F;
			}
		}
	}

	public static int method_20364(class_1937 arg, class_2624 arg2, int i, int j, int k, int l, int m) {
		if (!arg.field_9236 && m != 0 && (i + j + k + l) % 200 == 0) {
			m = method_17765(arg, arg2, j, k, l);
		}

		return m;
	}

	public static int method_17765(class_1937 arg, class_2624 arg2, int i, int j, int k) {
		int l = 0;
		float f = 5.0F;

		for (class_1657 lv : arg.method_18467(
			class_1657.class,
			new class_238(
				(double)((float)i - 5.0F),
				(double)((float)j - 5.0F),
				(double)((float)k - 5.0F),
				(double)((float)(i + 1) + 5.0F),
				(double)((float)(j + 1) + 5.0F),
				(double)((float)(k + 1) + 5.0F)
			)
		)) {
			if (lv.field_7512 instanceof class_1707) {
				class_1263 lv2 = ((class_1707)lv.field_7512).method_7629();
				if (lv2 == arg2 || lv2 instanceof class_1258 && ((class_1258)lv2).method_5405(arg2)) {
					l++;
				}
			}
		}

		return l;
	}

	private void method_11050(class_3414 arg) {
		class_2745 lv = this.method_11010().method_11654(class_2281.field_10770);
		if (lv != class_2745.field_12574) {
			double d = (double)this.field_11867.method_10263() + 0.5;
			double e = (double)this.field_11867.method_10264() + 0.5;
			double f = (double)this.field_11867.method_10260() + 0.5;
			if (lv == class_2745.field_12571) {
				class_2350 lv2 = class_2281.method_9758(this.method_11010());
				d += (double)lv2.method_10148() * 0.5;
				f += (double)lv2.method_10165() * 0.5;
			}

			this.field_11863.method_8465(null, d, e, f, arg, class_3419.field_15245, 0.5F, this.field_11863.field_9229.nextFloat() * 0.1F + 0.9F);
		}
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.field_11928 = j;
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public void method_5435(class_1657 arg) {
		if (!arg.method_7325()) {
			if (this.field_11928 < 0) {
				this.field_11928 = 0;
			}

			this.field_11928++;
			this.method_11049();
		}
	}

	@Override
	public void method_5432(class_1657 arg) {
		if (!arg.method_7325()) {
			this.field_11928--;
			this.method_11049();
		}
	}

	protected void method_11049() {
		class_2248 lv = this.method_11010().method_11614();
		if (lv instanceof class_2281) {
			this.field_11863.method_8427(this.field_11867, lv, 1, this.field_11928);
			this.field_11863.method_8452(this.field_11867, lv);
		}
	}

	@Override
	protected class_2371<class_1799> method_11282() {
		return this.field_11927;
	}

	@Override
	protected void method_11281(class_2371<class_1799> arg) {
		this.field_11927 = arg;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_11274(float f) {
		return class_3532.method_16439(f, this.field_11926, this.field_11929);
	}

	public static int method_11048(class_1922 arg, class_2338 arg2) {
		class_2680 lv = arg.method_8320(arg2);
		if (lv.method_11614().method_9570()) {
			class_2586 lv2 = arg.method_8321(arg2);
			if (lv2 instanceof class_2595) {
				return ((class_2595)lv2).field_11928;
			}
		}

		return 0;
	}

	public static void method_11047(class_2595 arg, class_2595 arg2) {
		class_2371<class_1799> lv = arg.method_11282();
		arg.method_11281(arg2.method_11282());
		arg2.method_11281(lv);
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return class_1707.method_19245(i, arg, this);
	}
}
