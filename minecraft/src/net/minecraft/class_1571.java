package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1571 extends class_1307 implements class_1569 {
	private static final class_2940<Boolean> field_7273 = class_2945.method_12791(class_1571.class, class_2943.field_13323);
	private int field_7272 = 1;

	public class_1571(class_1937 arg) {
		super(class_1299.field_6107, arg);
		this.method_5835(4.0F, 4.0F);
		this.field_5977 = true;
		this.field_6194 = 5;
		this.field_6207 = new class_1571.class_1573(this);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(5, new class_1571.class_1575(this));
		this.field_6201.method_6277(7, new class_1571.class_1572(this));
		this.field_6201.method_6277(7, new class_1571.class_1574(this));
		this.field_6185.method_6277(1, new class_1402(this));
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7050() {
		return this.field_6011.method_12789(field_7273);
	}

	public void method_7048(boolean bl) {
		this.field_6011.method_12778(field_7273, bl);
	}

	public int method_7049() {
		return this.field_7272;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (!this.field_6002.field_9236 && this.field_6002.method_8407() == class_1267.field_5801) {
			this.method_5650();
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (arg.method_5526() instanceof class_1674 && arg.method_5529() instanceof class_1657) {
			super.method_5643(arg, 1000.0F);
			return true;
		} else {
			return super.method_5643(arg, f);
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7273, false);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7365).method_6192(100.0);
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15251;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14566;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15054;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14648;
	}

	@Override
	protected float method_6107() {
		return 10.0F;
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return this.field_5974.nextInt(20) == 0 && super.method_5979(arg, arg2) && arg.method_8407() != class_1267.field_5801;
	}

	@Override
	public int method_5945() {
		return 1;
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("ExplosionPower", this.field_7272);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("ExplosionPower", 99)) {
			this.field_7272 = arg.method_10550("ExplosionPower");
		}
	}

	@Override
	public float method_5751() {
		return 2.6F;
	}

	static class class_1572 extends class_1352 {
		private final class_1571 field_7274;

		public class_1572(class_1571 arg) {
			this.field_7274 = arg;
			this.method_6265(2);
		}

		@Override
		public boolean method_6264() {
			return true;
		}

		@Override
		public void method_6268() {
			if (this.field_7274.method_5968() == null) {
				this.field_7274.field_6031 = -((float)class_3532.method_15349(this.field_7274.field_5967, this.field_7274.field_6006)) * (180.0F / (float)Math.PI);
				this.field_7274.field_6283 = this.field_7274.field_6031;
			} else {
				class_1309 lv = this.field_7274.method_5968();
				double d = 64.0;
				if (lv.method_5858(this.field_7274) < 4096.0) {
					double e = lv.field_5987 - this.field_7274.field_5987;
					double f = lv.field_6035 - this.field_7274.field_6035;
					this.field_7274.field_6031 = -((float)class_3532.method_15349(e, f)) * (180.0F / (float)Math.PI);
					this.field_7274.field_6283 = this.field_7274.field_6031;
				}
			}
		}
	}

	static class class_1573 extends class_1335 {
		private final class_1571 field_7275;
		private int field_7276;

		public class_1573(class_1571 arg) {
			super(arg);
			this.field_7275 = arg;
		}

		@Override
		public void method_6240() {
			if (this.field_6374 == class_1335.class_1336.field_6378) {
				double d = this.field_6370 - this.field_7275.field_5987;
				double e = this.field_6369 - this.field_7275.field_6010;
				double f = this.field_6367 - this.field_7275.field_6035;
				double g = d * d + e * e + f * f;
				if (this.field_7276-- <= 0) {
					this.field_7276 = this.field_7276 + this.field_7275.method_6051().nextInt(5) + 2;
					g = (double)class_3532.method_15368(g);
					if (this.method_7051(this.field_6370, this.field_6369, this.field_6367, g)) {
						this.field_7275.field_5967 += d / g * 0.1;
						this.field_7275.field_5984 += e / g * 0.1;
						this.field_7275.field_6006 += f / g * 0.1;
					} else {
						this.field_6374 = class_1335.class_1336.field_6377;
					}
				}
			}
		}

		private boolean method_7051(double d, double e, double f, double g) {
			double h = (d - this.field_7275.field_5987) / g;
			double i = (e - this.field_7275.field_6010) / g;
			double j = (f - this.field_7275.field_6035) / g;
			class_238 lv = this.field_7275.method_5829();

			for (int k = 1; (double)k < g; k++) {
				lv = lv.method_989(h, i, j);
				if (!this.field_7275.field_6002.method_8587(this.field_7275, lv)) {
					return false;
				}
			}

			return true;
		}
	}

	static class class_1574 extends class_1352 {
		private final class_1571 field_7277;
		public int field_7278;

		public class_1574(class_1571 arg) {
			this.field_7277 = arg;
		}

		@Override
		public boolean method_6264() {
			return this.field_7277.method_5968() != null;
		}

		@Override
		public void method_6269() {
			this.field_7278 = 0;
		}

		@Override
		public void method_6270() {
			this.field_7277.method_7048(false);
		}

		@Override
		public void method_6268() {
			class_1309 lv = this.field_7277.method_5968();
			double d = 64.0;
			if (lv.method_5858(this.field_7277) < 4096.0 && this.field_7277.method_6057(lv)) {
				class_1937 lv2 = this.field_7277.field_6002;
				this.field_7278++;
				if (this.field_7278 == 10) {
					lv2.method_8444(null, 1015, new class_2338(this.field_7277), 0);
				}

				if (this.field_7278 == 20) {
					double e = 4.0;
					class_243 lv3 = this.field_7277.method_5828(1.0F);
					double f = lv.field_5987 - (this.field_7277.field_5987 + lv3.field_1352 * 4.0);
					double g = lv.method_5829().field_1322 + (double)(lv.field_6019 / 2.0F) - (0.5 + this.field_7277.field_6010 + (double)(this.field_7277.field_6019 / 2.0F));
					double h = lv.field_6035 - (this.field_7277.field_6035 + lv3.field_1350 * 4.0);
					lv2.method_8444(null, 1016, new class_2338(this.field_7277), 0);
					class_1674 lv4 = new class_1674(lv2, this.field_7277, f, g, h);
					lv4.field_7624 = this.field_7277.method_7049();
					lv4.field_5987 = this.field_7277.field_5987 + lv3.field_1352 * 4.0;
					lv4.field_6010 = this.field_7277.field_6010 + (double)(this.field_7277.field_6019 / 2.0F) + 0.5;
					lv4.field_6035 = this.field_7277.field_6035 + lv3.field_1350 * 4.0;
					lv2.method_8649(lv4);
					this.field_7278 = -40;
				}
			} else if (this.field_7278 > 0) {
				this.field_7278--;
			}

			this.field_7277.method_7048(this.field_7278 > 10);
		}
	}

	static class class_1575 extends class_1352 {
		private final class_1571 field_7279;

		public class_1575(class_1571 arg) {
			this.field_7279 = arg;
			this.method_6265(1);
		}

		@Override
		public boolean method_6264() {
			class_1335 lv = this.field_7279.method_5962();
			if (!lv.method_6241()) {
				return true;
			} else {
				double d = lv.method_6236() - this.field_7279.field_5987;
				double e = lv.method_6235() - this.field_7279.field_6010;
				double f = lv.method_6237() - this.field_7279.field_6035;
				double g = d * d + e * e + f * f;
				return g < 1.0 || g > 3600.0;
			}
		}

		@Override
		public boolean method_6266() {
			return false;
		}

		@Override
		public void method_6269() {
			Random random = this.field_7279.method_6051();
			double d = this.field_7279.field_5987 + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double e = this.field_7279.field_6010 + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double f = this.field_7279.field_6035 + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.field_7279.method_5962().method_6239(d, e, f, 1.0);
		}
	}
}
