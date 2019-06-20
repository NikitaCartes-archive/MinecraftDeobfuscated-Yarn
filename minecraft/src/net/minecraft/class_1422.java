package net.minecraft;

import java.util.Random;

public abstract class class_1422 extends class_1480 {
	private static final class_2940<Boolean> field_6730 = class_2945.method_12791(class_1422.class, class_2943.field_13323);

	public class_1422(class_1299<? extends class_1422> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6207 = new class_1422.class_1423(this);
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.65F;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(3.0);
	}

	@Override
	public boolean method_17326() {
		return this.method_6453();
	}

	public static boolean method_20662(class_1299<? extends class_1422> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		return arg2.method_8320(arg4).method_11614() == class_2246.field_10382 && arg2.method_8320(arg4.method_10084()).method_11614() == class_2246.field_10382;
	}

	@Override
	public boolean method_5974(double d) {
		return !this.method_6453() && !this.method_16914();
	}

	@Override
	public int method_5945() {
		return 8;
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6730, false);
	}

	private boolean method_6453() {
		return this.field_6011.method_12789(field_6730);
	}

	public void method_6454(boolean bl) {
		this.field_6011.method_12778(field_6730, bl);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("FromBucket", this.method_6453());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6454(arg.method_10577("FromBucket"));
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1374(this, 1.25));
		this.field_6201.method_6277(2, new class_1338(this, class_1657.class, 8.0F, 1.6, 1.4, class_1301.field_6155::test));
		this.field_6201.method_6277(4, new class_1422.class_1424(this));
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		return new class_1412(this, arg);
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_6034() && this.method_5799()) {
			this.method_5724(0.01F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.9));
			if (this.method_5968() == null) {
				this.method_18799(this.method_18798().method_1031(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(arg);
		}
	}

	@Override
	public void method_6007() {
		if (!this.method_5799() && this.field_5952 && this.field_5992) {
			this.method_18799(
				this.method_18798()
					.method_1031((double)((this.field_5974.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4F, (double)((this.field_5974.nextFloat() * 2.0F - 1.0F) * 0.05F))
			);
			this.field_5952 = false;
			this.field_6007 = true;
			this.method_5783(this.method_6457(), this.method_6107(), this.method_6017());
		}

		super.method_6007();
	}

	@Override
	protected boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8705 && this.method_5805()) {
			this.method_5783(class_3417.field_14568, 1.0F, 1.0F);
			lv.method_7934(1);
			class_1799 lv2 = this.method_6452();
			this.method_6455(lv2);
			if (!this.field_6002.field_9236) {
				class_174.field_1208.method_8932((class_3222)arg, lv2);
			}

			if (lv.method_7960()) {
				arg.method_6122(arg2, lv2);
			} else if (!arg.field_7514.method_7394(lv2)) {
				arg.method_7328(lv2, false);
			}

			this.method_5650();
			return true;
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	protected void method_6455(class_1799 arg) {
		if (this.method_16914()) {
			arg.method_7977(this.method_5797());
		}
	}

	protected abstract class_1799 method_6452();

	protected boolean method_6456() {
		return true;
	}

	protected abstract class_3414 method_6457();

	@Override
	protected class_3414 method_5737() {
		return class_3417.field_14591;
	}

	static class class_1423 extends class_1335 {
		private final class_1422 field_6731;

		class_1423(class_1422 arg) {
			super(arg);
			this.field_6731 = arg;
		}

		@Override
		public void method_6240() {
			if (this.field_6731.method_5777(class_3486.field_15517)) {
				this.field_6731.method_18799(this.field_6731.method_18798().method_1031(0.0, 0.005, 0.0));
			}

			if (this.field_6374 == class_1335.class_1336.field_6378 && !this.field_6731.method_5942().method_6357()) {
				double d = this.field_6370 - this.field_6731.field_5987;
				double e = this.field_6369 - this.field_6731.field_6010;
				double f = this.field_6367 - this.field_6731.field_6035;
				double g = (double)class_3532.method_15368(d * d + e * e + f * f);
				e /= g;
				float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_6731.field_6031 = this.method_6238(this.field_6731.field_6031, h, 90.0F);
				this.field_6731.field_6283 = this.field_6731.field_6031;
				float i = (float)(this.field_6372 * this.field_6731.method_5996(class_1612.field_7357).method_6194());
				this.field_6731.method_6125(class_3532.method_16439(0.125F, this.field_6731.method_6029(), i));
				this.field_6731.method_18799(this.field_6731.method_18798().method_1031(0.0, (double)this.field_6731.method_6029() * e * 0.1, 0.0));
			} else {
				this.field_6731.method_6125(0.0F);
			}
		}
	}

	static class class_1424 extends class_1378 {
		private final class_1422 field_6732;

		public class_1424(class_1422 arg) {
			super(arg, 1.0, 40);
			this.field_6732 = arg;
		}

		@Override
		public boolean method_6264() {
			return this.field_6732.method_6456() && super.method_6264();
		}
	}
}
