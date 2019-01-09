package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1501 extends class_1492 implements class_1603 {
	private static final class_2940<Integer> field_6998 = class_2945.method_12791(class_1501.class, class_2943.field_13327);
	private static final class_2940<Integer> field_6995 = class_2945.method_12791(class_1501.class, class_2943.field_13327);
	private static final class_2940<Integer> field_6996 = class_2945.method_12791(class_1501.class, class_2943.field_13327);
	private boolean field_6999;
	@Nullable
	private class_1501 field_7000;
	@Nullable
	private class_1501 field_6997;

	public class_1501(class_1937 arg) {
		super(class_1299.field_6074, arg);
		this.method_5835(0.9F, 1.87F);
	}

	private void method_6802(int i) {
		this.field_6011.method_12778(field_6998, Math.max(1, Math.min(5, i)));
	}

	private void method_6796() {
		int i = this.field_5974.nextFloat() < 0.04F ? 5 : 3;
		this.method_6802(1 + this.field_5974.nextInt(i));
	}

	public int method_6803() {
		return this.field_6011.method_12789(field_6998);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Variant", this.method_6809());
		arg.method_10569("Strength", this.method_6803());
		if (!this.field_6962.method_5438(1).method_7960()) {
			arg.method_10566("DecorItem", this.field_6962.method_5438(1).method_7953(new class_2487()));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.method_6802(arg.method_10550("Strength"));
		super.method_5749(arg);
		this.method_6798(arg.method_10550("Variant"));
		if (arg.method_10573("DecorItem", 10)) {
			this.field_6962.method_5447(1, class_1799.method_7915(arg.method_10562("DecorItem")));
		}

		this.method_6731();
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1387(this, 1.2));
		this.field_6201.method_6277(2, new class_1362(this, 2.1F));
		this.field_6201.method_6277(3, new class_1381(this, 1.25, 40, 20.0F));
		this.field_6201.method_6277(3, new class_1374(this, 1.2));
		this.field_6201.method_6277(4, new class_1341(this, 1.0));
		this.field_6201.method_6277(5, new class_1353(this, 1.0));
		this.field_6201.method_6277(6, new class_1394(this, 0.7));
		this.field_6201.method_6277(7, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.field_6185.method_6277(1, new class_1501.class_1504(this));
		this.field_6185.method_6277(2, new class_1501.class_1502(this));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7365).method_6192(40.0);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6998, 0);
		this.field_6011.method_12784(field_6995, -1);
		this.field_6011.method_12784(field_6996, 0);
	}

	public int method_6809() {
		return class_3532.method_15340(this.field_6011.method_12789(field_6996), 0, 3);
	}

	public void method_6798(int i) {
		this.field_6011.method_12778(field_6996, i);
	}

	@Override
	protected int method_6750() {
		return this.method_6703() ? 2 + 3 * this.method_6702() : super.method_6750();
	}

	@Override
	public void method_5865(class_1297 arg) {
		if (this.method_5626(arg)) {
			float f = class_3532.method_15362(this.field_6283 * (float) (Math.PI / 180.0));
			float g = class_3532.method_15374(this.field_6283 * (float) (Math.PI / 180.0));
			float h = 0.3F;
			arg.method_5814(this.field_5987 + (double)(0.3F * g), this.field_6010 + this.method_5621() + arg.method_5678(), this.field_6035 - (double)(0.3F * f));
		}
	}

	@Override
	public double method_5621() {
		return (double)this.field_6019 * 0.67;
	}

	@Override
	public boolean method_5956() {
		return false;
	}

	@Override
	protected boolean method_6742(class_1657 arg, class_1799 arg2) {
		int i = 0;
		int j = 0;
		float f = 0.0F;
		boolean bl = false;
		class_1792 lv = arg2.method_7909();
		if (lv == class_1802.field_8861) {
			i = 10;
			j = 3;
			f = 2.0F;
		} else if (lv == class_2246.field_10359.method_8389()) {
			i = 90;
			j = 6;
			f = 10.0F;
			if (this.method_6727() && this.method_5618() == 0 && this.method_6482()) {
				bl = true;
				this.method_6480(arg);
			}
		}

		if (this.method_6032() < this.method_6063() && f > 0.0F) {
			this.method_6025(f);
			bl = true;
		}

		if (this.method_6109() && i > 0) {
			this.field_6002
				.method_8406(
					class_2398.field_11211,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.field_6019),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					0.0,
					0.0,
					0.0
				);
			if (!this.field_6002.field_9236) {
				this.method_5615(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.method_6727()) && this.method_6729() < this.method_6755()) {
			bl = true;
			if (!this.field_6002.field_9236) {
				this.method_6745(j);
			}
		}

		if (bl && !this.method_5701()) {
			this.field_6002
				.method_8465(
					null,
					this.field_5987,
					this.field_6010,
					this.field_6035,
					class_3417.field_14884,
					this.method_5634(),
					1.0F,
					1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F
				);
		}

		return bl;
	}

	@Override
	protected boolean method_6062() {
		return this.method_6032() <= 0.0F || this.method_6724();
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		this.method_6796();
		int i;
		if (arg4 instanceof class_1501.class_1503) {
			i = ((class_1501.class_1503)arg4).field_7001;
		} else {
			i = this.field_5974.nextInt(4);
			arg4 = new class_1501.class_1503(i);
		}

		this.method_6798(i);
		return arg4;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6807() {
		return this.method_6800() != null;
	}

	@Override
	protected class_3414 method_6747() {
		return class_3417.field_14586;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14682;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15031;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15189;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14795, 0.15F, 1.0F);
	}

	@Override
	protected void method_6705() {
		this.method_5783(class_3417.field_15097, 1.0F, (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public void method_6757() {
		class_3414 lv = this.method_6747();
		if (lv != null) {
			this.method_5783(lv, this.method_6107(), this.method_6017());
		}
	}

	@Override
	public int method_6702() {
		return this.method_6803();
	}

	@Override
	public boolean method_6735() {
		return true;
	}

	@Override
	public boolean method_6773(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		return class_3489.field_15542.method_15141(lv);
	}

	@Override
	public boolean method_6765() {
		return false;
	}

	@Override
	public void method_5453(class_1263 arg) {
		class_1767 lv = this.method_6800();
		super.method_5453(arg);
		class_1767 lv2 = this.method_6800();
		if (this.field_6012 > 20 && lv2 != null && lv2 != lv) {
			this.method_5783(class_3417.field_14554, 0.5F, 1.0F);
		}
	}

	@Override
	protected void method_6731() {
		if (!this.field_6002.field_9236) {
			super.method_6731();
			this.method_6799(method_6794(this.field_6962.method_5438(1)));
		}
	}

	private void method_6799(@Nullable class_1767 arg) {
		this.field_6011.method_12778(field_6995, arg == null ? -1 : arg.method_7789());
	}

	@Nullable
	private static class_1767 method_6794(class_1799 arg) {
		class_2248 lv = class_2248.method_9503(arg.method_7909());
		return lv instanceof class_2577 ? ((class_2577)lv).method_10925() : null;
	}

	@Nullable
	public class_1767 method_6800() {
		int i = this.field_6011.method_12789(field_6995);
		return i == -1 ? null : class_1767.method_7791(i);
	}

	@Override
	public int method_6755() {
		return 30;
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		return arg != this && arg instanceof class_1501 && this.method_6734() && ((class_1501)arg).method_6734();
	}

	public class_1501 method_6804(class_1296 arg) {
		class_1501 lv = new class_1501(this.field_6002);
		this.method_6743(arg, lv);
		class_1501 lv2 = (class_1501)arg;
		int i = this.field_5974.nextInt(Math.max(this.method_6803(), lv2.method_6803())) + 1;
		if (this.field_5974.nextFloat() < 0.03F) {
			i++;
		}

		lv.method_6802(i);
		lv.method_6798(this.field_5974.nextBoolean() ? this.method_6809() : lv2.method_6809());
		return lv;
	}

	private void method_6792(class_1309 arg) {
		class_1673 lv = new class_1673(this.field_6002, this);
		double d = arg.field_5987 - this.field_5987;
		double e = arg.method_5829().field_1322 + (double)(arg.field_6019 / 3.0F) - lv.field_6010;
		double f = arg.field_6035 - this.field_6035;
		float g = class_3532.method_15368(d * d + f * f) * 0.2F;
		lv.method_7485(d, e + (double)g, f, 1.5F, 10.0F);
		this.field_6002
			.method_8465(
				null,
				this.field_5987,
				this.field_6010,
				this.field_6035,
				class_3417.field_14789,
				this.method_5634(),
				1.0F,
				1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F
			);
		this.field_6002.method_8649(lv);
		this.field_6999 = true;
	}

	private void method_6808(boolean bl) {
		this.field_6999 = bl;
	}

	@Override
	public void method_5747(float f, float g) {
		int i = class_3532.method_15386((f * 0.5F - 3.0F) * g);
		if (i > 0) {
			if (f >= 6.0F) {
				this.method_5643(class_1282.field_5868, (float)i);
				if (this.method_5782()) {
					for (class_1297 lv : this.method_5736()) {
						lv.method_5643(class_1282.field_5868, (float)i);
					}
				}
			}

			class_2680 lv2 = this.field_6002.method_8320(new class_2338(this.field_5987, this.field_6010 - 0.2 - (double)this.field_5982, this.field_6035));
			if (!lv2.method_11588() && !this.method_5701()) {
				class_2498 lv3 = lv2.method_11638();
				this.field_6002
					.method_8465(
						null, this.field_5987, this.field_6010, this.field_6035, lv3.method_10594(), this.method_5634(), lv3.method_10597() * 0.5F, lv3.method_10599() * 0.75F
					);
			}
		}
	}

	public void method_6797() {
		if (this.field_7000 != null) {
			this.field_7000.field_6997 = null;
		}

		this.field_7000 = null;
	}

	public void method_6791(class_1501 arg) {
		this.field_7000 = arg;
		this.field_7000.field_6997 = this;
	}

	public boolean method_6793() {
		return this.field_6997 != null;
	}

	public boolean method_6805() {
		return this.field_7000 != null;
	}

	@Nullable
	public class_1501 method_6806() {
		return this.field_7000;
	}

	@Override
	protected double method_6148() {
		return 2.0;
	}

	@Override
	protected void method_6746() {
		if (!this.method_6805() && this.method_6109()) {
			super.method_6746();
		}
	}

	@Override
	public boolean method_6762() {
		return false;
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		this.method_6792(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_6999() {
		return false;
	}

	@Override
	public void method_7106(boolean bl) {
	}

	static class class_1502 extends class_1400<class_1493> {
		public class_1502(class_1501 arg) {
			super(arg, class_1493.class, 16, false, true, null);
		}

		@Override
		public boolean method_6264() {
			if (super.method_6264() && this.field_6644 != null && !this.field_6644.method_6181()) {
				return true;
			} else {
				this.field_6660.method_5980(null);
				return false;
			}
		}

		@Override
		protected double method_6326() {
			return super.method_6326() * 0.25;
		}
	}

	static class class_1503 implements class_1315 {
		public final int field_7001;

		private class_1503(int i) {
			this.field_7001 = i;
		}
	}

	static class class_1504 extends class_1399 {
		public class_1504(class_1501 arg) {
			super(arg);
		}

		@Override
		public boolean method_6266() {
			if (this.field_6660 instanceof class_1501) {
				class_1501 lv = (class_1501)this.field_6660;
				if (lv.field_6999) {
					lv.method_6808(false);
					return false;
				}
			}

			return super.method_6266();
		}
	}
}
