package net.minecraft;

import javax.annotation.Nullable;

public class class_1621 extends class_1308 implements class_1569 {
	private static final class_2940<Integer> field_7390 = class_2945.method_12791(class_1621.class, class_2943.field_13327);
	public float field_7389;
	public float field_7388;
	public float field_7387;
	private boolean field_7391;

	protected class_1621(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6207 = new class_1621.class_1625(this);
	}

	public class_1621(class_1937 arg) {
		this(class_1299.field_6069, arg);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1621.class_1623(this));
		this.field_6201.method_6277(2, new class_1621.class_1622(this));
		this.field_6201.method_6277(3, new class_1621.class_1626(this));
		this.field_6201.method_6277(5, new class_1621.class_1624(this));
		this.field_6185.method_6277(1, new class_1402(this));
		this.field_6185.method_6277(3, new class_1398(this, class_1439.class));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7390, 1);
	}

	protected void method_7161(int i, boolean bl) {
		this.field_6011.method_12778(field_7390, i);
		this.method_5835(0.51000005F * (float)i, 0.51000005F * (float)i);
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		this.method_5996(class_1612.field_7359).method_6192((double)(i * i));
		this.method_5996(class_1612.field_7357).method_6192((double)(0.2F + 0.1F * (float)i));
		if (bl) {
			this.method_6033(this.method_6063());
		}

		this.field_6194 = i;
	}

	public int method_7152() {
		return this.field_6011.method_12789(field_7390);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Size", this.method_7152() - 1);
		arg.method_10556("wasOnGround", this.field_7391);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		int i = arg.method_10550("Size");
		if (i < 0) {
			i = 0;
		}

		this.method_7161(i + 1, false);
		this.field_7391 = arg.method_10577("wasOnGround");
	}

	public boolean method_7157() {
		return this.method_7152() <= 1;
	}

	protected class_2394 method_7162() {
		return class_2398.field_11246;
	}

	@Override
	public void method_5773() {
		if (!this.field_6002.field_9236 && this.field_6002.method_8407() == class_1267.field_5801 && this.method_7152() > 0) {
			this.field_5988 = true;
		}

		this.field_7388 = this.field_7388 + (this.field_7389 - this.field_7388) * 0.5F;
		this.field_7387 = this.field_7388;
		super.method_5773();
		if (this.field_5952 && !this.field_7391) {
			int i = this.method_7152();

			for (int j = 0; j < i * 8; j++) {
				float f = this.field_5974.nextFloat() * (float) (Math.PI * 2);
				float g = this.field_5974.nextFloat() * 0.5F + 0.5F;
				float h = class_3532.method_15374(f) * (float)i * 0.5F * g;
				float k = class_3532.method_15362(f) * (float)i * 0.5F * g;
				class_1937 var10000 = this.field_6002;
				class_2394 var10001 = this.method_7162();
				double var10002 = this.field_5987 + (double)h;
				double var10004 = this.field_6035 + (double)k;
				var10000.method_8406(var10001, var10002, this.method_5829().field_1322, var10004, 0.0, 0.0, 0.0);
			}

			this.method_5783(this.method_7160(), this.method_6107(), ((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.field_7389 = -0.5F;
		} else if (!this.field_5952 && this.field_7391) {
			this.field_7389 = 1.0F;
		}

		this.field_7391 = this.field_5952;
		this.method_7156();
	}

	protected void method_7156() {
		this.field_7389 *= 0.6F;
	}

	protected int method_7154() {
		return this.field_5974.nextInt(20) + 10;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7390.equals(arg)) {
			int i = this.method_7152();
			this.method_5835(0.51000005F * (float)i, 0.51000005F * (float)i);
			this.field_6031 = this.field_6241;
			this.field_6283 = this.field_6241;
			if (this.method_5799() && this.field_5974.nextInt(20) == 0) {
				this.method_5746();
			}
		}

		super.method_5674(arg);
	}

	@Override
	public class_1299<? extends class_1621> method_5864() {
		return (class_1299<? extends class_1621>)super.method_5864();
	}

	@Override
	public void method_5650() {
		int i = this.method_7152();
		if (!this.field_6002.field_9236 && i > 1 && this.method_6032() <= 0.0F) {
			int j = 2 + this.field_5974.nextInt(3);

			for (int k = 0; k < j; k++) {
				float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
				float g = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
				class_1621 lv = this.method_5864().method_5883(this.field_6002);
				if (this.method_16914()) {
					lv.method_5665(this.method_5797());
				}

				if (this.method_5947()) {
					lv.method_5971();
				}

				lv.method_7161(i / 2, true);
				lv.method_5808(this.field_5987 + (double)f, this.field_6010 + 0.5, this.field_6035 + (double)g, this.field_5974.nextFloat() * 360.0F, 0.0F);
				this.field_6002.method_8649(lv);
			}
		}

		super.method_5650();
	}

	@Override
	public void method_5697(class_1297 arg) {
		super.method_5697(arg);
		if (arg instanceof class_1439 && this.method_7163()) {
			this.method_7155((class_1309)arg);
		}
	}

	@Override
	public void method_5694(class_1657 arg) {
		if (this.method_7163()) {
			this.method_7155(arg);
		}
	}

	protected void method_7155(class_1309 arg) {
		int i = this.method_7152();
		if (this.method_6057(arg)
			&& this.method_5858(arg) < 0.6 * (double)i * 0.6 * (double)i
			&& arg.method_5643(class_1282.method_5511(this), (float)this.method_7158())) {
			this.method_5783(class_3417.field_14863, 1.0F, (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
			this.method_5723(this, arg);
		}
	}

	@Override
	public float method_5751() {
		return 0.625F * this.field_6019;
	}

	protected boolean method_7163() {
		return !this.method_7157() && this.method_6034();
	}

	protected int method_7158() {
		return this.method_7152();
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_7157() ? class_3417.field_14620 : class_3417.field_15014;
	}

	@Override
	protected class_3414 method_6002() {
		return this.method_7157() ? class_3417.field_14849 : class_3417.field_14763;
	}

	protected class_3414 method_7160() {
		return this.method_7157() ? class_3417.field_15148 : class_3417.field_15095;
	}

	@Override
	protected class_2960 method_5991() {
		return this.method_7152() == 1 ? this.method_5864().method_16351() : class_39.field_844;
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		class_2338 lv = new class_2338(class_3532.method_15357(this.field_5987), 0, class_3532.method_15357(this.field_6035));
		if (arg.method_8401().method_153() == class_1942.field_9277 && this.field_5974.nextInt(4) != 1) {
			return false;
		} else {
			if (arg.method_8407() != class_1267.field_5801) {
				class_1959 lv2 = arg.method_8310(lv);
				if (lv2 == class_1972.field_9471
					&& this.field_6010 > 50.0
					&& this.field_6010 < 70.0
					&& this.field_5974.nextFloat() < 0.5F
					&& this.field_5974.nextFloat() < arg.method_8391()
					&& arg.method_8602(new class_2338(this)) <= this.field_5974.nextInt(8)) {
					return super.method_5979(arg, arg2);
				}

				class_1923 lv3 = new class_1923(lv);
				boolean bl = class_2919.method_12662(lv3.field_9181, lv3.field_9180, arg.method_8412(), 987234911L).nextInt(10) == 0;
				if (this.field_5974.nextInt(10) == 0 && bl && this.field_6010 < 40.0) {
					return super.method_5979(arg, arg2);
				}
			}

			return false;
		}
	}

	@Override
	protected float method_6107() {
		return 0.4F * (float)this.method_7152();
	}

	@Override
	public int method_5978() {
		return 0;
	}

	protected boolean method_7159() {
		return this.method_7152() > 0;
	}

	@Override
	protected void method_6043() {
		this.field_5984 = 0.42F;
		this.field_6007 = true;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		int i = this.field_5974.nextInt(3);
		if (i < 2 && this.field_5974.nextFloat() < 0.5F * arg2.method_5458()) {
			i++;
		}

		int j = 1 << i;
		this.method_7161(j, true);
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	protected class_3414 method_7153() {
		return this.method_7157() ? class_3417.field_14694 : class_3417.field_14919;
	}

	static class class_1622 extends class_1352 {
		private final class_1621 field_7393;
		private int field_7392;

		public class_1622(class_1621 arg) {
			this.field_7393 = arg;
			this.method_6265(2);
		}

		@Override
		public boolean method_6264() {
			class_1309 lv = this.field_7393.method_5968();
			if (lv == null) {
				return false;
			} else if (!lv.method_5805()) {
				return false;
			} else {
				return lv instanceof class_1657 && ((class_1657)lv).field_7503.field_7480 ? false : this.field_7393.method_5962() instanceof class_1621.class_1625;
			}
		}

		@Override
		public void method_6269() {
			this.field_7392 = 300;
			super.method_6269();
		}

		@Override
		public boolean method_6266() {
			class_1309 lv = this.field_7393.method_5968();
			if (lv == null) {
				return false;
			} else if (!lv.method_5805()) {
				return false;
			} else {
				return lv instanceof class_1657 && ((class_1657)lv).field_7503.field_7480 ? false : --this.field_7392 > 0;
			}
		}

		@Override
		public void method_6268() {
			this.field_7393.method_5951(this.field_7393.method_5968(), 10.0F, 10.0F);
			((class_1621.class_1625)this.field_7393.method_5962()).method_7165(this.field_7393.field_6031, this.field_7393.method_7163());
		}
	}

	static class class_1623 extends class_1352 {
		private final class_1621 field_7394;

		public class_1623(class_1621 arg) {
			this.field_7394 = arg;
			this.method_6265(5);
			arg.method_5942().method_6354(true);
		}

		@Override
		public boolean method_6264() {
			return this.field_7394.method_5799() || this.field_7394.method_5771();
		}

		@Override
		public void method_6268() {
			if (this.field_7394.method_6051().nextFloat() < 0.8F) {
				this.field_7394.method_5993().method_6233();
			}

			((class_1621.class_1625)this.field_7394.method_5962()).method_7164(1.2);
		}
	}

	static class class_1624 extends class_1352 {
		private final class_1621 field_7395;

		public class_1624(class_1621 arg) {
			this.field_7395 = arg;
			this.method_6265(5);
		}

		@Override
		public boolean method_6264() {
			return !this.field_7395.method_5765();
		}

		@Override
		public void method_6268() {
			((class_1621.class_1625)this.field_7395.method_5962()).method_7164(1.0);
		}
	}

	static class class_1625 extends class_1335 {
		private float field_7397;
		private int field_7399;
		private final class_1621 field_7396;
		private boolean field_7398;

		public class_1625(class_1621 arg) {
			super(arg);
			this.field_7396 = arg;
			this.field_7397 = 180.0F * arg.field_6031 / (float) Math.PI;
		}

		public void method_7165(float f, boolean bl) {
			this.field_7397 = f;
			this.field_7398 = bl;
		}

		public void method_7164(double d) {
			this.field_6372 = d;
			this.field_6374 = class_1335.class_1336.field_6378;
		}

		@Override
		public void method_6240() {
			this.field_6371.field_6031 = this.method_6238(this.field_6371.field_6031, this.field_7397, 90.0F);
			this.field_6371.field_6241 = this.field_6371.field_6031;
			this.field_6371.field_6283 = this.field_6371.field_6031;
			if (this.field_6374 != class_1335.class_1336.field_6378) {
				this.field_6371.method_5930(0.0F);
			} else {
				this.field_6374 = class_1335.class_1336.field_6377;
				if (this.field_6371.field_5952) {
					this.field_6371.method_6125((float)(this.field_6372 * this.field_6371.method_5996(class_1612.field_7357).method_6194()));
					if (this.field_7399-- <= 0) {
						this.field_7399 = this.field_7396.method_7154();
						if (this.field_7398) {
							this.field_7399 /= 3;
						}

						this.field_7396.method_5993().method_6233();
						if (this.field_7396.method_7159()) {
							this.field_7396
								.method_5783(
									this.field_7396.method_7153(),
									this.field_7396.method_6107(),
									((this.field_7396.method_6051().nextFloat() - this.field_7396.method_6051().nextFloat()) * 0.2F + 1.0F) * 0.8F
								);
						}
					} else {
						this.field_7396.field_6212 = 0.0F;
						this.field_7396.field_6250 = 0.0F;
						this.field_6371.method_6125(0.0F);
					}
				} else {
					this.field_6371.method_6125((float)(this.field_6372 * this.field_6371.method_5996(class_1612.field_7357).method_6194()));
				}
			}
		}
	}

	static class class_1626 extends class_1352 {
		private final class_1621 field_7402;
		private float field_7400;
		private int field_7401;

		public class_1626(class_1621 arg) {
			this.field_7402 = arg;
			this.method_6265(2);
		}

		@Override
		public boolean method_6264() {
			return this.field_7402.method_5968() == null
				&& (this.field_7402.field_5952 || this.field_7402.method_5799() || this.field_7402.method_5771() || this.field_7402.method_6059(class_1294.field_5902));
		}

		@Override
		public void method_6268() {
			if (--this.field_7401 <= 0) {
				this.field_7401 = 40 + this.field_7402.method_6051().nextInt(60);
				this.field_7400 = (float)this.field_7402.method_6051().nextInt(360);
			}

			((class_1621.class_1625)this.field_7402.method_5962()).method_7165(this.field_7400, false);
		}
	}
}
