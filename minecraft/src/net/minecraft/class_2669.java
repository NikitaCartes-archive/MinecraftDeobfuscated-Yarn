package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2669 extends class_2586 implements class_3000 {
	private class_2680 field_12204;
	private class_2350 field_12201;
	private boolean field_12203;
	private boolean field_12202;
	private static final ThreadLocal<class_2350> field_12205 = new ThreadLocal<class_2350>() {
		protected class_2350 method_11516() {
			return null;
		}
	};
	private float field_12207;
	private float field_12206;
	private long field_12208;

	public class_2669() {
		super(class_2591.field_11897);
	}

	public class_2669(class_2680 arg, class_2350 arg2, boolean bl, boolean bl2) {
		this();
		this.field_12204 = arg;
		this.field_12201 = arg2;
		this.field_12203 = bl;
		this.field_12202 = bl2;
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	public boolean method_11501() {
		return this.field_12203;
	}

	public class_2350 method_11498() {
		return this.field_12201;
	}

	public boolean method_11515() {
		return this.field_12202;
	}

	public float method_11499(float f) {
		if (f > 1.0F) {
			f = 1.0F;
		}

		return class_3532.method_16439(f, this.field_12206, this.field_12207);
	}

	@Environment(EnvType.CLIENT)
	public float method_11494(float f) {
		return (float)this.field_12201.method_10148() * this.method_11504(this.method_11499(f));
	}

	@Environment(EnvType.CLIENT)
	public float method_11511(float f) {
		return (float)this.field_12201.method_10164() * this.method_11504(this.method_11499(f));
	}

	@Environment(EnvType.CLIENT)
	public float method_11507(float f) {
		return (float)this.field_12201.method_10165() * this.method_11504(this.method_11499(f));
	}

	private float method_11504(float f) {
		return this.field_12203 ? f - 1.0F : 1.0F - f;
	}

	private class_2680 method_11496() {
		return !this.method_11501() && this.method_11515()
			? class_2246.field_10379
				.method_9564()
				.method_11657(class_2671.field_12224, this.field_12204.method_11614() == class_2246.field_10615 ? class_2764.field_12634 : class_2764.field_12637)
				.method_11657(class_2671.field_10927, this.field_12204.method_11654(class_2665.field_10927))
			: this.field_12204;
	}

	private void method_11503(float f) {
		class_2350 lv = this.method_11506();
		double d = (double)(f - this.field_12207);
		class_265 lv2 = this.method_11496().method_11628(this.field_11863, this.method_11016());
		if (!lv2.method_1110()) {
			List<class_238> list = lv2.method_1090();
			class_238 lv3 = this.method_11500(this.method_11509(list));
			List<class_1297> list2 = this.field_11863.method_8335(null, this.method_11502(lv3, lv, d).method_991(lv3));
			if (!list2.isEmpty()) {
				boolean bl = this.field_12204.method_11614() == class_2246.field_10030;

				for (int i = 0; i < list2.size(); i++) {
					class_1297 lv4 = (class_1297)list2.get(i);
					if (lv4.method_5657() != class_3619.field_15975) {
						if (bl) {
							switch (lv.method_10166()) {
								case field_11048:
									lv4.field_5967 = (double)lv.method_10148();
									break;
								case field_11052:
									lv4.field_5984 = (double)lv.method_10164();
									break;
								case field_11051:
									lv4.field_6006 = (double)lv.method_10165();
							}
						}

						double e = 0.0;

						for (int j = 0; j < list.size(); j++) {
							class_238 lv5 = this.method_11502(this.method_11500((class_238)list.get(j)), lv, d);
							class_238 lv6 = lv4.method_5829();
							if (lv5.method_994(lv6)) {
								e = Math.max(e, this.method_11497(lv5, lv, lv6));
								if (e >= d) {
									break;
								}
							}
						}

						if (!(e <= 0.0)) {
							e = Math.min(e, d) + 0.01;
							field_12205.set(lv);
							lv4.method_5784(class_1313.field_6310, e * (double)lv.method_10148(), e * (double)lv.method_10164(), e * (double)lv.method_10165());
							field_12205.set(null);
							if (!this.field_12203 && this.field_12202) {
								this.method_11514(lv4, lv, d);
							}
						}
					}
				}
			}
		}
	}

	public class_2350 method_11506() {
		return this.field_12203 ? this.field_12201 : this.field_12201.method_10153();
	}

	private class_238 method_11509(List<class_238> list) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		double g = 1.0;
		double h = 1.0;
		double i = 1.0;

		for (class_238 lv : list) {
			d = Math.min(lv.field_1323, d);
			e = Math.min(lv.field_1322, e);
			f = Math.min(lv.field_1321, f);
			g = Math.max(lv.field_1320, g);
			h = Math.max(lv.field_1325, h);
			i = Math.max(lv.field_1324, i);
		}

		return new class_238(d, e, f, g, h, i);
	}

	private double method_11497(class_238 arg, class_2350 arg2, class_238 arg3) {
		switch (arg2.method_10166()) {
			case field_11048:
				return method_11493(arg, arg2, arg3);
			case field_11052:
			default:
				return method_11510(arg, arg2, arg3);
			case field_11051:
				return method_11505(arg, arg2, arg3);
		}
	}

	private class_238 method_11500(class_238 arg) {
		double d = (double)this.method_11504(this.field_12207);
		return arg.method_989(
			(double)this.field_11867.method_10263() + d * (double)this.field_12201.method_10148(),
			(double)this.field_11867.method_10264() + d * (double)this.field_12201.method_10164(),
			(double)this.field_11867.method_10260() + d * (double)this.field_12201.method_10165()
		);
	}

	private class_238 method_11502(class_238 arg, class_2350 arg2, double d) {
		double e = d * (double)arg2.method_10171().method_10181();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (arg2) {
			case field_11039:
				return new class_238(arg.field_1323 + f, arg.field_1322, arg.field_1321, arg.field_1323 + g, arg.field_1325, arg.field_1324);
			case field_11034:
				return new class_238(arg.field_1320 + f, arg.field_1322, arg.field_1321, arg.field_1320 + g, arg.field_1325, arg.field_1324);
			case field_11033:
				return new class_238(arg.field_1323, arg.field_1322 + f, arg.field_1321, arg.field_1320, arg.field_1322 + g, arg.field_1324);
			case field_11036:
			default:
				return new class_238(arg.field_1323, arg.field_1325 + f, arg.field_1321, arg.field_1320, arg.field_1325 + g, arg.field_1324);
			case field_11043:
				return new class_238(arg.field_1323, arg.field_1322, arg.field_1321 + f, arg.field_1320, arg.field_1325, arg.field_1321 + g);
			case field_11035:
				return new class_238(arg.field_1323, arg.field_1322, arg.field_1324 + f, arg.field_1320, arg.field_1325, arg.field_1324 + g);
		}
	}

	private void method_11514(class_1297 arg, class_2350 arg2, double d) {
		class_238 lv = arg.method_5829();
		class_238 lv2 = class_259.method_1077().method_1107().method_996(this.field_11867);
		if (lv.method_994(lv2)) {
			class_2350 lv3 = arg2.method_10153();
			double e = this.method_11497(lv2, lv3, lv) + 0.01;
			double f = this.method_11497(lv2, lv3, lv.method_999(lv2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				field_12205.set(arg2);
				arg.method_5784(class_1313.field_6310, e * (double)lv3.method_10148(), e * (double)lv3.method_10164(), e * (double)lv3.method_10165());
				field_12205.set(null);
			}
		}
	}

	private static double method_11493(class_238 arg, class_2350 arg2, class_238 arg3) {
		return arg2.method_10171() == class_2350.class_2352.field_11056 ? arg.field_1320 - arg3.field_1323 : arg3.field_1320 - arg.field_1323;
	}

	private static double method_11510(class_238 arg, class_2350 arg2, class_238 arg3) {
		return arg2.method_10171() == class_2350.class_2352.field_11056 ? arg.field_1325 - arg3.field_1322 : arg3.field_1325 - arg.field_1322;
	}

	private static double method_11505(class_238 arg, class_2350 arg2, class_238 arg3) {
		return arg2.method_10171() == class_2350.class_2352.field_11056 ? arg.field_1324 - arg3.field_1321 : arg3.field_1324 - arg.field_1321;
	}

	public class_2680 method_11495() {
		return this.field_12204;
	}

	public void method_11513() {
		if (this.field_12206 < 1.0F && this.field_11863 != null) {
			this.field_12207 = 1.0F;
			this.field_12206 = this.field_12207;
			this.field_11863.method_8544(this.field_11867);
			this.method_11012();
			if (this.field_11863.method_8320(this.field_11867).method_11614() == class_2246.field_10008) {
				class_2680 lv;
				if (this.field_12202) {
					lv = class_2246.field_10124.method_9564();
				} else {
					lv = class_2248.method_9510(this.field_12204, this.field_11863, this.field_11867);
				}

				this.field_11863.method_8652(this.field_11867, lv, 3);
				this.field_11863.method_8492(this.field_11867, lv.method_11614(), this.field_11867);
			}
		}
	}

	@Override
	public void method_16896() {
		this.field_12208 = this.field_11863.method_8510();
		this.field_12206 = this.field_12207;
		if (this.field_12206 >= 1.0F) {
			this.field_11863.method_8544(this.field_11867);
			this.method_11012();
			if (this.field_12204 != null && this.field_11863.method_8320(this.field_11867).method_11614() == class_2246.field_10008) {
				class_2680 lv = class_2248.method_9510(this.field_12204, this.field_11863, this.field_11867);
				if (lv.method_11588()) {
					this.field_11863.method_8652(this.field_11867, this.field_12204, 84);
					class_2248.method_9611(this.field_12204, lv, this.field_11863, this.field_11867, 3);
				} else {
					if (lv.method_11570(class_2741.field_12508) && (Boolean)lv.method_11654(class_2741.field_12508)) {
						lv = lv.method_11657(class_2741.field_12508, Boolean.valueOf(false));
					}

					this.field_11863.method_8652(this.field_11867, lv, 67);
					this.field_11863.method_8492(this.field_11867, lv.method_11614(), this.field_11867);
				}
			}
		} else {
			float f = this.field_12207 + 0.5F;
			this.method_11503(f);
			this.field_12207 = f;
			if (this.field_12207 >= 1.0F) {
				this.field_12207 = 1.0F;
			}
		}
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_12204 = class_2512.method_10681(arg.method_10562("blockState"));
		this.field_12201 = class_2350.method_10143(arg.method_10550("facing"));
		this.field_12207 = arg.method_10583("progress");
		this.field_12206 = this.field_12207;
		this.field_12203 = arg.method_10577("extending");
		this.field_12202 = arg.method_10577("source");
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10566("blockState", class_2512.method_10686(this.field_12204));
		arg.method_10569("facing", this.field_12201.method_10146());
		arg.method_10548("progress", this.field_12206);
		arg.method_10556("extending", this.field_12203);
		arg.method_10556("source", this.field_12202);
		return arg;
	}

	public class_265 method_11512(class_1922 arg, class_2338 arg2) {
		class_265 lv;
		if (!this.field_12203 && this.field_12202) {
			lv = this.field_12204.method_11657(class_2665.field_12191, Boolean.valueOf(true)).method_11628(arg, arg2);
		} else {
			lv = class_259.method_1073();
		}

		class_2350 lv2 = (class_2350)field_12205.get();
		if ((double)this.field_12207 < 1.0 && lv2 == this.method_11506()) {
			return lv;
		} else {
			class_2680 lv3;
			if (this.method_11515()) {
				lv3 = class_2246.field_10379
					.method_9564()
					.method_11657(class_2671.field_10927, this.field_12201)
					.method_11657(class_2671.field_12227, Boolean.valueOf(this.field_12203 != 1.0F - this.field_12207 < 4.0F));
			} else {
				lv3 = this.field_12204;
			}

			float f = this.method_11504(this.field_12207);
			double d = (double)((float)this.field_12201.method_10148() * f);
			double e = (double)((float)this.field_12201.method_10164() * f);
			double g = (double)((float)this.field_12201.method_10165() * f);
			return class_259.method_1084(lv, lv3.method_11628(arg, arg2).method_1096(d, e, g));
		}
	}

	public long method_11508() {
		return this.field_12208;
	}
}
