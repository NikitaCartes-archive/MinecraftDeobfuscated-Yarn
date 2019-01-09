package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1584 extends class_3763 {
	private static final Predicate<class_1297> field_7301 = arg -> arg.method_5805() && !(arg instanceof class_1584);
	private int field_7303;
	private int field_7302;
	private int field_7305;

	public class_1584(class_1937 arg) {
		super(class_1299.field_6134, arg);
		this.method_5835(1.95F, 2.2F);
		this.field_6013 = 1.0F;
		this.field_6194 = 20;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(4, new class_1584.class_1585());
		this.field_6201.method_6277(5, new class_1394(this, 0.4));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
		this.field_6185.method_6277(2, new class_1399(this, class_1543.class).method_6318());
		this.field_6185.method_6277(3, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(4, new class_1400(this, class_1646.class, true));
		this.field_6185.method_6277(4, new class_1400(this, class_1439.class, true));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(100.0);
		this.method_5996(class_1612.field_7357).method_6192(0.3);
		this.method_5996(class_1612.field_7360).method_6192(0.5);
		this.method_5996(class_1612.field_7363).method_6192(12.0);
		this.method_5996(class_1612.field_7361).method_6192(1.5);
		this.method_5996(class_1612.field_7365).method_6192(32.0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("AttackTick", this.field_7303);
		arg.method_10569("StunTick", this.field_7302);
		arg.method_10569("RoarTick", this.field_7305);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7303 = arg.method_10550("AttackTick");
		this.field_7302 = arg.method_10550("StunTick");
		this.field_7305 = arg.method_10550("RoarTick");
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		return new class_1584.class_1586(this, arg);
	}

	@Override
	public int method_5986() {
		return 45;
	}

	@Override
	public double method_5621() {
		return 2.1;
	}

	@Override
	public boolean method_5956() {
		return true;
	}

	@Nullable
	@Override
	public class_1297 method_5642() {
		return this.method_5685().isEmpty() ? null : (class_1297)this.method_5685().get(0);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.method_6062()) {
			this.method_5996(class_1612.field_7357).method_6192(0.0);
		} else {
			double d = this.method_5968() != null ? 0.35 : 0.3;
			double e = this.method_5996(class_1612.field_7357).method_6201();
			this.method_5996(class_1612.field_7357).method_6192(class_3532.method_16436(0.1, e, d));
		}

		if (this.field_5976 && this.field_6002.method_8450().method_8355("mobGriefing")) {
			boolean bl = false;
			class_238 lv = this.method_5829().method_1014(0.2);

			for (class_2338.class_2339 lv2 : class_2338.method_10068(
				class_3532.method_15357(lv.field_1323),
				class_3532.method_15357(lv.field_1322),
				class_3532.method_15357(lv.field_1321),
				class_3532.method_15357(lv.field_1320),
				class_3532.method_15357(lv.field_1325),
				class_3532.method_15357(lv.field_1324)
			)) {
				class_2680 lv3 = this.field_6002.method_8320(lv2);
				class_2248 lv4 = lv3.method_11614();
				if (lv4 instanceof class_2397) {
					bl = this.field_6002.method_8651(lv2, true) || bl;
				}
			}

			if (!bl && this.field_5952) {
				this.method_6043();
			}
		}

		if (this.field_7305 > 0) {
			this.field_7305--;
			if (this.field_7305 == 10) {
				this.method_7071();
			}
		}

		if (this.field_7303 > 0) {
			this.field_7303--;
		}

		if (this.field_7302 > 0) {
			this.field_7302--;
			this.method_7073();
			if (this.field_7302 == 0) {
				this.method_5783(class_3417.field_14733, 1.0F, 1.0F);
				this.field_7305 = 20;
			}
		}
	}

	private void method_7073() {
		if (this.field_5974.nextInt(6) == 0) {
			double d = this.field_5987
				- (double)this.field_5998 * Math.sin((double)(this.field_6283 * (float) (Math.PI / 180.0)))
				+ (this.field_5974.nextDouble() * 0.6 - 0.3);
			double e = this.field_6010 + (double)this.field_6019 - 0.3;
			double f = this.field_6035
				+ (double)this.field_5998 * Math.cos((double)(this.field_6283 * (float) (Math.PI / 180.0)))
				+ (this.field_5974.nextDouble() * 0.6 - 0.3);
			this.field_6002.method_8406(class_2398.field_11226, d, e, f, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
		}
	}

	@Override
	protected boolean method_6062() {
		return super.method_6062() || this.field_7303 > 0 || this.field_7302 > 0 || this.field_7305 > 0;
	}

	@Override
	public boolean method_6057(class_1297 arg) {
		return this.field_7302 <= 0 && this.field_7305 <= 0 ? super.method_6057(arg) : false;
	}

	@Override
	protected void method_6060(class_1309 arg) {
		if (this.field_7305 == 0) {
			if (this.field_5974.nextDouble() < 0.5) {
				this.field_7302 = 40;
				this.method_5783(class_3417.field_14822, 1.0F, 1.0F);
				this.field_6002.method_8421(this, (byte)39);
				arg.method_5697(this);
			} else {
				this.method_7068(arg);
			}

			arg.field_6037 = true;
		}
	}

	private void method_7071() {
		if (this.method_5805()) {
			for (class_1297 lv : this.field_6002.method_8390(class_1309.class, this.method_5829().method_1014(4.0), field_7301)) {
				if (!(lv instanceof class_1543)) {
					lv.method_5643(class_1282.method_5511(this), 6.0F);
				}

				this.method_7068(lv);
			}

			class_243 lv2 = this.method_5829().method_1005();

			for (int i = 0; i < 40; i++) {
				double d = this.field_5974.nextGaussian() * 0.2;
				double e = this.field_5974.nextGaussian() * 0.2;
				double f = this.field_5974.nextGaussian() * 0.2;
				this.field_6002.method_8406(class_2398.field_11203, lv2.field_1352, lv2.field_1351, lv2.field_1350, d, e, f);
			}
		}
	}

	private void method_7068(class_1297 arg) {
		double d = arg.field_5987 - this.field_5987;
		double e = arg.field_6035 - this.field_6035;
		double f = d * d + e * e;
		arg.method_5762(d / f * 4.0, 0.2, e / f * 4.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 4) {
			this.field_7303 = 10;
			this.method_5783(class_3417.field_15240, 1.0F, 1.0F);
		} else if (b == 39) {
			this.field_7302 = 40;
		}

		super.method_5711(b);
	}

	@Environment(EnvType.CLIENT)
	public int method_7070() {
		return this.field_7303;
	}

	@Environment(EnvType.CLIENT)
	public int method_7074() {
		return this.field_7302;
	}

	@Environment(EnvType.CLIENT)
	public int method_7072() {
		return this.field_7305;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		this.field_7303 = 10;
		this.field_6002.method_8421(this, (byte)4);
		this.method_5783(class_3417.field_15240, 1.0F, 1.0F);
		return super.method_6121(arg);
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14639;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15007;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15146;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14929, 0.15F, 1.0F);
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		return !arg.method_8599(this.method_5829()) && arg.method_8587(this, this.method_5829());
	}

	@Override
	public void method_16484(int i, boolean bl) {
	}

	@Override
	public boolean method_16485() {
		return false;
	}

	class class_1585 extends class_1366 {
		public class_1585() {
			super(class_1584.this, 1.0, true);
		}

		@Override
		protected double method_6289(class_1309 arg) {
			float f = class_1584.this.field_5998 - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + arg.field_5998);
		}
	}

	static class class_1586 extends class_1409 {
		public class_1586(class_1308 arg, class_1937 arg2) {
			super(arg, arg2);
		}

		@Override
		protected class_13 method_6336() {
			this.field_6678 = new class_1584.class_1587();
			return new class_13(this.field_6678);
		}
	}

	static class class_1587 extends class_14 {
		private class_1587() {
		}

		@Override
		protected class_7 method_61(class_1922 arg, boolean bl, boolean bl2, class_2338 arg2, class_7 arg3) {
			return arg3 == class_7.field_6 ? class_7.field_7 : super.method_61(arg, bl, bl2, arg2, arg3);
		}
	}
}
