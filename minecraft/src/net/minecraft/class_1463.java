package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1463 extends class_1429 {
	private static final class_2940<Integer> field_6852 = class_2945.method_12791(class_1463.class, class_2943.field_13327);
	private static final class_2960 field_6846 = new class_2960("killer_bunny");
	private int field_6851;
	private int field_6849;
	private boolean field_6850;
	private int field_6848;
	private int field_6847;

	public class_1463(class_1299<? extends class_1463> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6204 = new class_1463.class_1467(this);
		this.field_6207 = new class_1463.class_1468(this);
		this.method_6606(0.0);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(1, new class_1463.class_1469(this, 2.2));
		this.field_6201.method_6277(2, new class_1341(this, 0.8));
		this.field_6201
			.method_6277(3, new class_1391(this, 1.0, class_1856.method_8091(class_1802.field_8179, class_1802.field_8071, class_2246.field_10182), false));
		this.field_6201.method_6277(4, new class_1463.class_1465(this, class_1657.class, 8.0F, 2.2, 2.2));
		this.field_6201.method_6277(4, new class_1463.class_1465(this, class_1493.class, 10.0F, 2.2, 2.2));
		this.field_6201.method_6277(4, new class_1463.class_1465(this, class_1588.class, 4.0F, 2.2, 2.2));
		this.field_6201.method_6277(5, new class_1463.class_1470(this));
		this.field_6201.method_6277(6, new class_1394(this, 0.6));
		this.field_6201.method_6277(11, new class_1361(this, class_1657.class, 10.0F));
	}

	@Override
	protected float method_6106() {
		if (!this.field_5976 && (!this.field_6207.method_6241() || !(this.field_6207.method_6235() > this.field_6010 + 0.5))) {
			class_11 lv = this.field_6189.method_6345();
			if (lv != null && lv.method_39() < lv.method_38()) {
				class_243 lv2 = lv.method_49(this);
				if (lv2.field_1351 > this.field_6010 + 0.5) {
					return 0.5F;
				}
			}

			return this.field_6207.method_6242() <= 0.6 ? 0.2F : 0.3F;
		} else {
			return 0.5F;
		}
	}

	@Override
	protected void method_6043() {
		super.method_6043();
		double d = this.field_6207.method_6242();
		if (d > 0.0) {
			double e = method_17996(this.method_18798());
			if (e < 0.01) {
				this.method_5724(0.1F, new class_243(0.0, 0.0, 1.0));
			}
		}

		if (!this.field_6002.field_9236) {
			this.field_6002.method_8421(this, (byte)1);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6605(float f) {
		return this.field_6849 == 0 ? 0.0F : ((float)this.field_6851 + f) / (float)this.field_6849;
	}

	public void method_6606(double d) {
		this.method_5942().method_6344(d);
		this.field_6207.method_6239(this.field_6207.method_6236(), this.field_6207.method_6235(), this.field_6207.method_6237(), d);
	}

	@Override
	public void method_6100(boolean bl) {
		super.method_6100(bl);
		if (bl) {
			this.method_5783(this.method_6615(), this.method_6107(), ((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	public void method_6618() {
		this.method_6100(true);
		this.field_6849 = 10;
		this.field_6851 = 0;
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6852, 0);
	}

	@Override
	public void method_5958() {
		if (this.field_6848 > 0) {
			this.field_6848--;
		}

		if (this.field_6847 > 0) {
			this.field_6847 = this.field_6847 - this.field_5974.nextInt(3);
			if (this.field_6847 < 0) {
				this.field_6847 = 0;
			}
		}

		if (this.field_5952) {
			if (!this.field_6850) {
				this.method_6100(false);
				this.method_6619();
			}

			if (this.method_6610() == 99 && this.field_6848 == 0) {
				class_1309 lv = this.method_5968();
				if (lv != null && this.method_5858(lv) < 16.0) {
					this.method_6616(lv.field_5987, lv.field_6035);
					this.field_6207.method_6239(lv.field_5987, lv.field_6010, lv.field_6035, this.field_6207.method_6242());
					this.method_6618();
					this.field_6850 = true;
				}
			}

			class_1463.class_1467 lv2 = (class_1463.class_1467)this.field_6204;
			if (!lv2.method_6624()) {
				if (this.field_6207.method_6241() && this.field_6848 == 0) {
					class_11 lv3 = this.field_6189.method_6345();
					class_243 lv4 = new class_243(this.field_6207.method_6236(), this.field_6207.method_6235(), this.field_6207.method_6237());
					if (lv3 != null && lv3.method_39() < lv3.method_38()) {
						lv4 = lv3.method_49(this);
					}

					this.method_6616(lv4.field_1352, lv4.field_1350);
					this.method_6618();
				}
			} else if (!lv2.method_6625()) {
				this.method_6611();
			}
		}

		this.field_6850 = this.field_5952;
	}

	@Override
	public void method_5666() {
	}

	private void method_6616(double d, double e) {
		this.field_6031 = (float)(class_3532.method_15349(e - this.field_6035, d - this.field_5987) * 180.0F / (float)Math.PI) - 90.0F;
	}

	private void method_6611() {
		((class_1463.class_1467)this.field_6204).method_6623(true);
	}

	private void method_6621() {
		((class_1463.class_1467)this.field_6204).method_6623(false);
	}

	private void method_6608() {
		if (this.field_6207.method_6242() < 2.2) {
			this.field_6848 = 10;
		} else {
			this.field_6848 = 1;
		}
	}

	private void method_6619() {
		this.method_6608();
		this.method_6621();
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.field_6851 != this.field_6849) {
			this.field_6851++;
		} else if (this.field_6849 != 0) {
			this.field_6851 = 0;
			this.field_6849 = 0;
			this.method_6100(false);
		}
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(3.0);
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("RabbitType", this.method_6610());
		arg.method_10569("MoreCarrotTicks", this.field_6847);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6617(arg.method_10550("RabbitType"));
		this.field_6847 = arg.method_10550("MoreCarrotTicks");
	}

	protected class_3414 method_6615() {
		return class_3417.field_15091;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14693;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15164;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14872;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		if (this.method_6610() == 99) {
			this.method_5783(class_3417.field_15147, 1.0F, (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
			return arg.method_5643(class_1282.method_5511(this), 8.0F);
		} else {
			return arg.method_5643(class_1282.method_5511(this), 3.0F);
		}
	}

	@Override
	public class_3419 method_5634() {
		return this.method_6610() == 99 ? class_3419.field_15251 : class_3419.field_15254;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return this.method_5679(arg) ? false : super.method_5643(arg, f);
	}

	private boolean method_6614(class_1792 arg) {
		return arg == class_1802.field_8179 || arg == class_1802.field_8071 || arg == class_2246.field_10182.method_8389();
	}

	public class_1463 method_6620(class_1296 arg) {
		class_1463 lv = class_1299.field_6140.method_5883(this.field_6002);
		int i = this.method_6622(this.field_6002);
		if (this.field_5974.nextInt(20) != 0) {
			if (arg instanceof class_1463 && this.field_5974.nextBoolean()) {
				i = ((class_1463)arg).method_6610();
			} else {
				i = this.method_6610();
			}
		}

		lv.method_6617(i);
		return lv;
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return this.method_6614(arg.method_7909());
	}

	public int method_6610() {
		return this.field_6011.method_12789(field_6852);
	}

	public void method_6617(int i) {
		if (i == 99) {
			this.method_5996(class_1612.field_7358).method_6192(8.0);
			this.field_6201.method_6277(4, new class_1463.class_1464(this));
			this.field_6185.method_6277(1, new class_1399(this).method_6318());
			this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
			this.field_6185.method_6277(2, new class_1400(this, class_1493.class, true));
			if (!this.method_16914()) {
				this.method_5665(new class_2588(class_156.method_646("entity", field_6846)));
			}
		}

		this.field_6011.method_12778(field_6852, i);
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		int i = this.method_6622(arg);
		boolean bl = false;
		if (arg4 instanceof class_1463.class_1466) {
			i = ((class_1463.class_1466)arg4).field_6854;
			bl = true;
		} else {
			arg4 = new class_1463.class_1466(i);
		}

		this.method_6617(i);
		if (bl) {
			this.method_5614(-24000);
		}

		return arg4;
	}

	private int method_6622(class_1936 arg) {
		class_1959 lv = arg.method_8310(new class_2338(this));
		int i = this.field_5974.nextInt(100);
		if (lv.method_8694() == class_1959.class_1963.field_9383) {
			return i < 80 ? 1 : 3;
		} else if (lv.method_8688() == class_1959.class_1961.field_9368) {
			return 4;
		} else {
			return i < 50 ? 0 : (i < 90 ? 5 : 2);
		}
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		int i = class_3532.method_15357(this.field_5987);
		int j = class_3532.method_15357(this.method_5829().field_1322);
		int k = class_3532.method_15357(this.field_6035);
		class_2338 lv = new class_2338(i, j, k);
		class_2248 lv2 = arg.method_8320(lv.method_10074()).method_11614();
		return lv2 != class_2246.field_10479 && lv2 != class_2246.field_10477 && lv2 != class_2246.field_10102 ? super.method_5979(arg, arg2) : true;
	}

	private boolean method_6607() {
		return this.field_6847 == 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 1) {
			this.method_5839();
			this.field_6849 = 10;
			this.field_6851 = 0;
		} else {
			super.method_5711(b);
		}
	}

	static class class_1464 extends class_1366 {
		public class_1464(class_1463 arg) {
			super(arg, 1.4, true);
		}

		@Override
		protected double method_6289(class_1309 arg) {
			return (double)(4.0F + arg.method_17681());
		}
	}

	static class class_1465<T extends class_1309> extends class_1338<T> {
		private final class_1463 field_6853;

		public class_1465(class_1463 arg, Class<T> class_, float f, double d, double e) {
			super(arg, class_, f, d, e);
			this.field_6853 = arg;
		}

		@Override
		public boolean method_6264() {
			return this.field_6853.method_6610() != 99 && super.method_6264();
		}
	}

	public static class class_1466 implements class_1315 {
		public final int field_6854;

		public class_1466(int i) {
			this.field_6854 = i;
		}
	}

	public class class_1467 extends class_1334 {
		private final class_1463 field_6855;
		private boolean field_6856;

		public class_1467(class_1463 arg2) {
			super(arg2);
			this.field_6855 = arg2;
		}

		public boolean method_6624() {
			return this.field_6365;
		}

		public boolean method_6625() {
			return this.field_6856;
		}

		public void method_6623(boolean bl) {
			this.field_6856 = bl;
		}

		@Override
		public void method_6234() {
			if (this.field_6365) {
				this.field_6855.method_6618();
				this.field_6365 = false;
			}
		}
	}

	static class class_1468 extends class_1335 {
		private final class_1463 field_6859;
		private double field_6858;

		public class_1468(class_1463 arg) {
			super(arg);
			this.field_6859 = arg;
		}

		@Override
		public void method_6240() {
			if (this.field_6859.field_5952 && !this.field_6859.field_6282 && !((class_1463.class_1467)this.field_6859.field_6204).method_6624()) {
				this.field_6859.method_6606(0.0);
			} else if (this.method_6241()) {
				this.field_6859.method_6606(this.field_6858);
			}

			super.method_6240();
		}

		@Override
		public void method_6239(double d, double e, double f, double g) {
			if (this.field_6859.method_5799()) {
				g = 1.5;
			}

			super.method_6239(d, e, f, g);
			if (g > 0.0) {
				this.field_6858 = g;
			}
		}
	}

	static class class_1469 extends class_1374 {
		private final class_1463 field_6860;

		public class_1469(class_1463 arg, double d) {
			super(arg, d);
			this.field_6860 = arg;
		}

		@Override
		public void method_6268() {
			super.method_6268();
			this.field_6860.method_6606(this.field_6548);
		}
	}

	static class class_1470 extends class_1367 {
		private final class_1463 field_6863;
		private boolean field_6862;
		private boolean field_6861;

		public class_1470(class_1463 arg) {
			super(arg, 0.7F, 16);
			this.field_6863 = arg;
		}

		@Override
		public boolean method_6264() {
			if (this.field_6518 <= 0) {
				if (!this.field_6863.field_6002.method_8450().method_8355("mobGriefing")) {
					return false;
				}

				this.field_6861 = false;
				this.field_6862 = this.field_6863.method_6607();
				this.field_6862 = true;
			}

			return super.method_6264();
		}

		@Override
		public boolean method_6266() {
			return this.field_6861 && super.method_6266();
		}

		@Override
		public void method_6268() {
			super.method_6268();
			this.field_6863
				.method_5988()
				.method_6230(
					(double)this.field_6512.method_10263() + 0.5,
					(double)(this.field_6512.method_10264() + 1),
					(double)this.field_6512.method_10260() + 0.5,
					10.0F,
					(float)this.field_6863.method_5978()
				);
			if (this.method_6295()) {
				class_1937 lv = this.field_6863.field_6002;
				class_2338 lv2 = this.field_6512.method_10084();
				class_2680 lv3 = lv.method_8320(lv2);
				class_2248 lv4 = lv3.method_11614();
				if (this.field_6861 && lv4 instanceof class_2271) {
					Integer integer = lv3.method_11654(class_2271.field_10835);
					if (integer == 0) {
						lv.method_8652(lv2, class_2246.field_10124.method_9564(), 2);
						lv.method_8651(lv2, true);
					} else {
						lv.method_8652(lv2, lv3.method_11657(class_2271.field_10835, Integer.valueOf(integer - 1)), 2);
						lv.method_8535(2001, lv2, class_2248.method_9507(lv3));
					}

					this.field_6863.field_6847 = 40;
				}

				this.field_6861 = false;
				this.field_6518 = 10;
			}
		}

		@Override
		protected boolean method_6296(class_1941 arg, class_2338 arg2) {
			class_2248 lv = arg.method_8320(arg2).method_11614();
			if (lv == class_2246.field_10362 && this.field_6862 && !this.field_6861) {
				arg2 = arg2.method_10084();
				class_2680 lv2 = arg.method_8320(arg2);
				lv = lv2.method_11614();
				if (lv instanceof class_2271 && ((class_2271)lv).method_9825(lv2)) {
					this.field_6861 = true;
					return true;
				}
			}

			return false;
		}
	}
}
