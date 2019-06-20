package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3701 extends class_1429 {
	private static final class_1856 field_16299 = class_1856.method_8091(class_1802.field_8429, class_1802.field_8209);
	private static final class_2940<Boolean> field_16301 = class_2945.method_12791(class_3701.class, class_2943.field_13323);
	private class_3701.class_3702<class_1657> field_16300;
	private class_3701.class_3703 field_16302;

	public class_3701(class_1299<? extends class_3701> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_16103();
	}

	private boolean method_16099() {
		return this.field_6011.method_12789(field_16301);
	}

	private void method_16102(boolean bl) {
		this.field_6011.method_12778(field_16301, bl);
		this.method_16103();
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("Trusting", this.method_16099());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_16102(arg.method_10577("Trusting"));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_16301, false);
	}

	@Override
	protected void method_5959() {
		this.field_16302 = new class_3701.class_3703(this, 0.6, field_16299, true);
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(3, this.field_16302);
		this.field_6201.method_6277(7, new class_1359(this, 0.3F));
		this.field_6201.method_6277(8, new class_1371(this));
		this.field_6201.method_6277(9, new class_1341(this, 0.8));
		this.field_6201.method_6277(10, new class_1394(this, 0.8, 1.0000001E-5F));
		this.field_6201.method_6277(11, new class_1361(this, class_1657.class, 10.0F));
		this.field_6185.method_6277(1, new class_1400(this, class_1428.class, false));
		this.field_6185.method_6277(1, new class_1400(this, class_1481.class, 10, false, false, class_1481.field_6921));
	}

	@Override
	public void method_5958() {
		if (this.method_5962().method_6241()) {
			double d = this.method_5962().method_6242();
			if (d == 0.6) {
				this.method_5660(true);
				this.method_5728(false);
			} else if (d == 1.33) {
				this.method_5660(false);
				this.method_5728(true);
			} else {
				this.method_5660(false);
				this.method_5728(false);
			}
		} else {
			this.method_5660(false);
			this.method_5728(false);
		}
	}

	@Override
	public boolean method_5974(double d) {
		return !this.method_16099() && this.field_6012 > 2400;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		return class_3417.field_16437;
	}

	@Override
	public int method_5970() {
		return 900;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_16441;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_16442;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		return arg.method_5643(class_1282.method_5511(this), 3.0F);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return this.method_5679(arg) ? false : super.method_5643(arg, f);
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if ((this.field_16302 == null || this.field_16302.method_6313()) && !this.method_16099() && this.method_6481(lv) && arg.method_5858(this) < 9.0) {
			this.method_6475(arg, lv);
			if (!this.field_6002.field_9236) {
				if (this.field_5974.nextInt(3) == 0) {
					this.method_16102(true);
					this.method_16100(true);
					this.field_6002.method_8421(this, (byte)41);
				} else {
					this.method_16100(false);
					this.field_6002.method_8421(this, (byte)40);
				}
			}

			return true;
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 41) {
			this.method_16100(true);
		} else if (b == 40) {
			this.method_16100(false);
		} else {
			super.method_5711(b);
		}
	}

	private void method_16100(boolean bl) {
		class_2394 lv = class_2398.field_11201;
		if (!bl) {
			lv = class_2398.field_11251;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.field_5974.nextGaussian() * 0.02;
			double e = this.field_5974.nextGaussian() * 0.02;
			double f = this.field_5974.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					lv,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.method_17682()),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					d,
					e,
					f
				);
		}
	}

	protected void method_16103() {
		if (this.field_16300 == null) {
			this.field_16300 = new class_3701.class_3702<>(this, class_1657.class, 16.0F, 0.8, 1.33);
		}

		this.field_6201.method_6280(this.field_16300);
		if (!this.method_16099()) {
			this.field_6201.method_6277(4, this.field_16300);
		}
	}

	public class_3701 method_16104(class_1296 arg) {
		return class_1299.field_6081.method_5883(this.field_6002);
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return field_16299.method_8093(arg);
	}

	public static boolean method_20666(class_1299<class_3701> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		return random.nextInt(3) != 0;
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		if (arg.method_8606(this) && !arg.method_8599(this.method_5829())) {
			class_2338 lv = new class_2338(this.field_5987, this.method_5829().field_1322, this.field_6035);
			if (lv.method_10264() < arg.method_8615()) {
				return false;
			}

			class_2680 lv2 = arg.method_8320(lv.method_10074());
			class_2248 lv3 = lv2.method_11614();
			if (lv3 == class_2246.field_10219 || lv2.method_11602(class_3481.field_15503)) {
				return true;
			}
		}

		return false;
	}

	protected void method_16105() {
		for (int i = 0; i < 2; i++) {
			class_3701 lv = class_1299.field_6081.method_5883(this.field_6002);
			lv.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, 0.0F);
			lv.method_5614(-24000);
			this.field_6002.method_8649(lv);
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (arg.method_8409().nextInt(7) == 0) {
			this.method_16105();
		}

		return arg4;
	}

	static class class_3702<T extends class_1309> extends class_1338<T> {
		private final class_3701 field_16303;

		public class_3702(class_3701 arg, Class<T> class_, float f, double d, double e) {
			super(arg, class_, f, d, e, class_1301.field_6156::test);
			this.field_16303 = arg;
		}

		@Override
		public boolean method_6264() {
			return !this.field_16303.method_16099() && super.method_6264();
		}

		@Override
		public boolean method_6266() {
			return !this.field_16303.method_16099() && super.method_6266();
		}
	}

	static class class_3703 extends class_1391 {
		private final class_3701 field_16304;

		public class_3703(class_3701 arg, double d, class_1856 arg2, boolean bl) {
			super(arg, d, arg2, bl);
			this.field_16304 = arg;
		}

		@Override
		protected boolean method_16081() {
			return super.method_16081() && !this.field_16304.method_16099();
		}
	}
}
