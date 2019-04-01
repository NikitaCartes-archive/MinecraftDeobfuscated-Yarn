package net.minecraft;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

public class class_1551 extends class_1642 implements class_1603 {
	private boolean field_7233;
	protected final class_1412 field_7234;
	protected final class_1409 field_7232;

	public class_1551(class_1299<? extends class_1551> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6013 = 1.0F;
		this.field_6207 = new class_1551.class_1556(this);
		this.method_5941(class_7.field_18, 0.0F);
		this.field_7234 = new class_1412(this, arg2);
		this.field_7232 = new class_1409(this, arg2);
	}

	@Override
	protected void method_7208() {
		this.field_6201.method_6277(1, new class_1551.class_1555(this, 1.0));
		this.field_6201.method_6277(2, new class_1551.class_1558(this, 1.0, 40, 10.0F));
		this.field_6201.method_6277(2, new class_1551.class_1552(this, 1.0, false));
		this.field_6201.method_6277(5, new class_1551.class_1554(this, 1.0));
		this.field_6201.method_6277(6, new class_1551.class_1557(this, 1.0, this.field_6002.method_8615()));
		this.field_6201.method_6277(7, new class_1379(this, 1.0));
		this.field_6185.method_6277(1, new class_1399(this, class_1551.class).method_6318(class_1590.class));
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, 10, true, false, this::method_7012));
		this.field_6185.method_6277(3, new class_1400(this, class_3988.class, false));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, true));
		this.field_6185.method_6277(5, new class_1400(this, class_1481.class, 10, true, false, class_1481.field_6921));
	}

	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (this.method_6118(class_1304.field_6171).method_7960() && this.field_5974.nextFloat() < 0.03F) {
			this.method_5673(class_1304.field_6171, new class_1799(class_1802.field_8864));
			this.field_6187[class_1304.field_6171.method_5927()] = 2.0F;
		}

		return arg4;
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		class_1959 lv = arg.method_8310(new class_2338(this.field_5987, this.field_6010, this.field_6035));
		return lv != class_1972.field_9438 && lv != class_1972.field_9463
			? this.field_5974.nextInt(40) == 0 && this.method_7015() && super.method_5979(arg, arg2)
			: this.field_5974.nextInt(15) == 0 && super.method_5979(arg, arg2);
	}

	private boolean method_7015() {
		return this.method_5829().field_1322 < (double)(this.field_6002.method_8615() - 5);
	}

	@Override
	protected boolean method_7212() {
		return false;
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_5799() ? class_3417.field_14980 : class_3417.field_15030;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_5799() ? class_3417.field_14651 : class_3417.field_14571;
	}

	@Override
	protected class_3414 method_6002() {
		return this.method_5799() ? class_3417.field_15162 : class_3417.field_15066;
	}

	@Override
	protected class_3414 method_7207() {
		return class_3417.field_14835;
	}

	@Override
	protected class_3414 method_5737() {
		return class_3417.field_14913;
	}

	@Override
	protected class_1799 method_7215() {
		return class_1799.field_8037;
	}

	@Override
	protected void method_5964(class_1266 arg) {
		if ((double)this.field_5974.nextFloat() > 0.9) {
			int i = this.field_5974.nextInt(16);
			if (i < 10) {
				this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8547));
			} else {
				this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8378));
			}
		}
	}

	@Override
	protected boolean method_5955(class_1799 arg, class_1799 arg2, class_1304 arg3) {
		if (arg2.method_7909() == class_1802.field_8864) {
			return false;
		} else if (arg2.method_7909() == class_1802.field_8547) {
			return arg.method_7909() == class_1802.field_8547 ? arg.method_7919() < arg2.method_7919() : false;
		} else {
			return arg.method_7909() == class_1802.field_8547 ? true : super.method_5955(arg, arg2, arg3);
		}
	}

	@Override
	protected boolean method_7209() {
		return false;
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		return arg.method_8606(this);
	}

	public boolean method_7012(@Nullable class_1309 arg) {
		return arg != null ? !this.field_6002.method_8530() || arg.method_5799() : false;
	}

	@Override
	public boolean method_5675() {
		return !this.method_5681();
	}

	private boolean method_7018() {
		if (this.field_7233) {
			return true;
		} else {
			class_1309 lv = this.method_5968();
			return lv != null && lv.method_5799();
		}
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_6034() && this.method_5799() && this.method_7018()) {
			this.method_5724(0.01F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.9));
		} else {
			super.method_6091(arg);
		}
	}

	@Override
	public void method_5790() {
		if (!this.field_6002.field_9236) {
			if (this.method_6034() && this.method_5799() && this.method_7018()) {
				this.field_6189 = this.field_7234;
				this.method_5796(true);
			} else {
				this.field_6189 = this.field_7232;
				this.method_5796(false);
			}
		}
	}

	protected boolean method_7016() {
		class_11 lv = this.method_5942().method_6345();
		if (lv != null) {
			class_9 lv2 = lv.method_48();
			if (lv2 != null) {
				double d = this.method_5649((double)lv2.field_40, (double)lv2.field_39, (double)lv2.field_38);
				if (d < 4.0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		class_1685 lv = new class_1685(this.field_6002, this, new class_1799(class_1802.field_8547));
		double d = arg.field_5987 - this.field_5987;
		double e = arg.method_5829().field_1322 + (double)(arg.method_17682() / 3.0F) - lv.field_6010;
		double g = arg.field_6035 - this.field_6035;
		double h = (double)class_3532.method_15368(d * d + g * g);
		lv.method_7485(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.field_6002.method_8407().method_5461() * 4));
		this.method_5783(class_3417.field_14753, 1.0F, 1.0F / (this.method_6051().nextFloat() * 0.4F + 0.8F));
		this.field_6002.method_8649(lv);
	}

	public void method_7013(boolean bl) {
		this.field_7233 = bl;
	}

	static class class_1552 extends class_1396 {
		private final class_1551 field_7235;

		public class_1552(class_1551 arg, double d, boolean bl) {
			super(arg, d, bl);
			this.field_7235 = arg;
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && this.field_7235.method_7012(this.field_7235.method_5968());
		}

		@Override
		public boolean method_6266() {
			return super.method_6266() && this.field_7235.method_7012(this.field_7235.method_5968());
		}
	}

	static class class_1554 extends class_1367 {
		private final class_1551 field_7237;

		public class_1554(class_1551 arg, double d) {
			super(arg, d, 8, 2);
			this.field_7237 = arg;
		}

		@Override
		public boolean method_6264() {
			return super.method_6264()
				&& !this.field_7237.field_6002.method_8530()
				&& this.field_7237.method_5799()
				&& this.field_7237.field_6010 >= (double)(this.field_7237.field_6002.method_8615() - 3);
		}

		@Override
		public boolean method_6266() {
			return super.method_6266();
		}

		@Override
		protected boolean method_6296(class_1941 arg, class_2338 arg2) {
			class_2338 lv = arg2.method_10084();
			return arg.method_8623(lv) && arg.method_8623(lv.method_10084()) ? arg.method_8320(arg2).method_11631(arg, arg2, this.field_7237) : false;
		}

		@Override
		public void method_6269() {
			this.field_7237.method_7013(false);
			this.field_7237.field_6189 = this.field_7237.field_7232;
			super.method_6269();
		}

		@Override
		public void method_6270() {
			super.method_6270();
		}
	}

	static class class_1555 extends class_1352 {
		private final class_1314 field_7242;
		private double field_7240;
		private double field_7239;
		private double field_7238;
		private final double field_7243;
		private final class_1937 field_7241;

		public class_1555(class_1314 arg, double d) {
			this.field_7242 = arg;
			this.field_7243 = d;
			this.field_7241 = arg.field_6002;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			if (!this.field_7241.method_8530()) {
				return false;
			} else if (this.field_7242.method_5799()) {
				return false;
			} else {
				class_243 lv = this.method_7021();
				if (lv == null) {
					return false;
				} else {
					this.field_7240 = lv.field_1352;
					this.field_7239 = lv.field_1351;
					this.field_7238 = lv.field_1350;
					return true;
				}
			}
		}

		@Override
		public boolean method_6266() {
			return !this.field_7242.method_5942().method_6357();
		}

		@Override
		public void method_6269() {
			this.field_7242.method_5942().method_6337(this.field_7240, this.field_7239, this.field_7238, this.field_7243);
		}

		@Nullable
		private class_243 method_7021() {
			Random random = this.field_7242.method_6051();
			class_2338 lv = new class_2338(this.field_7242.field_5987, this.field_7242.method_5829().field_1322, this.field_7242.field_6035);

			for (int i = 0; i < 10; i++) {
				class_2338 lv2 = lv.method_10069(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
				if (this.field_7241.method_8320(lv2).method_11614() == class_2246.field_10382) {
					return new class_243((double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260());
				}
			}

			return null;
		}
	}

	static class class_1556 extends class_1335 {
		private final class_1551 field_7244;

		public class_1556(class_1551 arg) {
			super(arg);
			this.field_7244 = arg;
		}

		@Override
		public void method_6240() {
			class_1309 lv = this.field_7244.method_5968();
			if (this.field_7244.method_7018() && this.field_7244.method_5799()) {
				if (lv != null && lv.field_6010 > this.field_7244.field_6010 || this.field_7244.field_7233) {
					this.field_7244.method_18799(this.field_7244.method_18798().method_1031(0.0, 0.002, 0.0));
				}

				if (this.field_6374 != class_1335.class_1336.field_6378 || this.field_7244.method_5942().method_6357()) {
					this.field_7244.method_6125(0.0F);
					return;
				}

				double d = this.field_6370 - this.field_7244.field_5987;
				double e = this.field_6369 - this.field_7244.field_6010;
				double f = this.field_6367 - this.field_7244.field_6035;
				double g = (double)class_3532.method_15368(d * d + e * e + f * f);
				e /= g;
				float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_7244.field_6031 = this.method_6238(this.field_7244.field_6031, h, 90.0F);
				this.field_7244.field_6283 = this.field_7244.field_6031;
				float i = (float)(this.field_6372 * this.field_7244.method_5996(class_1612.field_7357).method_6194());
				float j = class_3532.method_16439(0.125F, this.field_7244.method_6029(), i);
				this.field_7244.method_6125(j);
				this.field_7244.method_18799(this.field_7244.method_18798().method_1031((double)j * d * 0.005, (double)j * e * 0.1, (double)j * f * 0.005));
			} else {
				if (!this.field_7244.field_5952) {
					this.field_7244.method_18799(this.field_7244.method_18798().method_1031(0.0, -0.008, 0.0));
				}

				super.method_6240();
			}
		}
	}

	static class class_1557 extends class_1352 {
		private final class_1551 field_7246;
		private final double field_7245;
		private final int field_7247;
		private boolean field_7248;

		public class_1557(class_1551 arg, double d, int i) {
			this.field_7246 = arg;
			this.field_7245 = d;
			this.field_7247 = i;
		}

		@Override
		public boolean method_6264() {
			return !this.field_7246.field_6002.method_8530() && this.field_7246.method_5799() && this.field_7246.field_6010 < (double)(this.field_7247 - 2);
		}

		@Override
		public boolean method_6266() {
			return this.method_6264() && !this.field_7248;
		}

		@Override
		public void method_6268() {
			if (this.field_7246.field_6010 < (double)(this.field_7247 - 1) && (this.field_7246.method_5942().method_6357() || this.field_7246.method_7016())) {
				class_243 lv = class_1414.method_6373(
					this.field_7246, 4, 8, new class_243(this.field_7246.field_5987, (double)(this.field_7247 - 1), this.field_7246.field_6035)
				);
				if (lv == null) {
					this.field_7248 = true;
					return;
				}

				this.field_7246.method_5942().method_6337(lv.field_1352, lv.field_1351, lv.field_1350, this.field_7245);
			}
		}

		@Override
		public void method_6269() {
			this.field_7246.method_7013(true);
			this.field_7248 = false;
		}

		@Override
		public void method_6270() {
			this.field_7246.method_7013(false);
		}
	}

	static class class_1558 extends class_1381 {
		private final class_1551 field_7249;

		public class_1558(class_1603 arg, double d, int i, float f) {
			super(arg, d, i, f);
			this.field_7249 = (class_1551)arg;
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && this.field_7249.method_6047().method_7909() == class_1802.field_8547;
		}

		@Override
		public void method_6269() {
			super.method_6269();
			this.field_7249.method_19540(true);
			this.field_7249.method_6019(class_1268.field_5808);
		}

		@Override
		public void method_6270() {
			super.method_6270();
			this.field_7249.method_6021();
			this.field_7249.method_19540(false);
		}
	}
}
