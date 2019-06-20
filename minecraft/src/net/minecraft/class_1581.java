package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1581 extends class_1617 implements class_1603 {
	private int field_7296;
	private final class_243[][] field_7297;

	public class_1581(class_1299<? extends class_1581> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6194 = 5;
		this.field_7297 = new class_243[2][4];

		for (int i = 0; i < 4; i++) {
			this.field_7297[0][i] = new class_243(0.0, 0.0, 0.0);
			this.field_7297[1][i] = new class_243(0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1617.class_1619());
		this.field_6201.method_6277(4, new class_1581.class_1583());
		this.field_6201.method_6277(5, new class_1581.class_1582());
		this.field_6201.method_6277(6, new class_1380<>(this, 0.5, 20, 15.0F));
		this.field_6201.method_6277(8, new class_1379(this, 0.6));
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 3.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
		this.field_6185.method_6277(1, new class_1399(this, class_3763.class).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true).method_6330(300));
		this.field_6185.method_6277(3, new class_1400(this, class_3988.class, false).method_6330(300));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, false).method_6330(300));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.5);
		this.method_5996(class_1612.field_7365).method_6192(18.0);
		this.method_5996(class_1612.field_7359).method_6192(32.0);
	}

	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8102));
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_238 method_5830() {
		return this.method_5829().method_1009(3.0, 0.0, 3.0);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.field_6002.field_9236 && this.method_5767()) {
			this.field_7296--;
			if (this.field_7296 < 0) {
				this.field_7296 = 0;
			}

			if (this.field_6235 == 1 || this.field_6012 % 1200 == 0) {
				this.field_7296 = 3;
				float f = -6.0F;
				int i = 13;

				for (int j = 0; j < 4; j++) {
					this.field_7297[0][j] = this.field_7297[1][j];
					this.field_7297[1][j] = new class_243(
						(double)(-6.0F + (float)this.field_5974.nextInt(13)) * 0.5,
						(double)Math.max(0, this.field_5974.nextInt(6) - 4),
						(double)(-6.0F + (float)this.field_5974.nextInt(13)) * 0.5
					);
				}

				for (int j = 0; j < 16; j++) {
					this.field_6002
						.method_8406(
							class_2398.field_11204,
							this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681(),
							this.field_6010 + this.field_5974.nextDouble() * (double)this.method_17682(),
							this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681(),
							0.0,
							0.0,
							0.0
						);
				}

				this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, class_3417.field_14941, this.method_5634(), 1.0F, 1.0F, false);
			} else if (this.field_6235 == this.field_6254 - 1) {
				this.field_7296 = 3;

				for (int k = 0; k < 4; k++) {
					this.field_7297[0][k] = this.field_7297[1][k];
					this.field_7297[1][k] = new class_243(0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Override
	public class_3414 method_20033() {
		return class_3417.field_14644;
	}

	@Environment(EnvType.CLIENT)
	public class_243[] method_7065(float f) {
		if (this.field_7296 <= 0) {
			return this.field_7297[1];
		} else {
			double d = (double)(((float)this.field_7296 - f) / 3.0F);
			d = Math.pow(d, 0.25);
			class_243[] lvs = new class_243[4];

			for (int i = 0; i < 4; i++) {
				lvs[i] = this.field_7297[1][i].method_1021(1.0 - d).method_1019(this.field_7297[0][i].method_1021(d));
			}

			return lvs;
		}
	}

	@Override
	public boolean method_5722(class_1297 arg) {
		if (super.method_5722(arg)) {
			return true;
		} else {
			return arg instanceof class_1309 && ((class_1309)arg).method_6046() == class_1310.field_6291
				? this.method_5781() == null && arg.method_5781() == null
				: false;
		}
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14644;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15153;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15223;
	}

	@Override
	protected class_3414 method_7142() {
		return class_3417.field_14545;
	}

	@Override
	public void method_16484(int i, boolean bl) {
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		class_1799 lv = this.method_18808(this.method_5998(class_1675.method_18812(this, class_1802.field_8102)));
		class_1665 lv2 = class_1675.method_18813(this, lv, f);
		double d = arg.field_5987 - this.field_5987;
		double e = arg.method_5829().field_1322 + (double)(arg.method_17682() / 3.0F) - lv2.field_6010;
		double g = arg.field_6035 - this.field_6035;
		double h = (double)class_3532.method_15368(d * d + g * g);
		lv2.method_7485(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.field_6002.method_8407().method_5461() * 4));
		this.method_5783(class_3417.field_14633, 1.0F, 1.0F / (this.method_6051().nextFloat() * 0.4F + 0.8F));
		this.field_6002.method_8649(lv2);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1543.class_1544 method_6990() {
		if (this.method_7137()) {
			return class_1543.class_1544.field_7212;
		} else {
			return this.method_6510() ? class_1543.class_1544.field_7208 : class_1543.class_1544.field_7207;
		}
	}

	class class_1582 extends class_1617.class_1620 {
		private int field_7298;

		private class_1582() {
		}

		@Override
		public boolean method_6264() {
			if (!super.method_6264()) {
				return false;
			} else if (class_1581.this.method_5968() == null) {
				return false;
			} else {
				return class_1581.this.method_5968().method_5628() == this.field_7298
					? false
					: class_1581.this.field_6002.method_8404(new class_2338(class_1581.this)).method_5455((float)class_1267.field_5802.ordinal());
			}
		}

		@Override
		public void method_6269() {
			super.method_6269();
			this.field_7298 = class_1581.this.method_5968().method_5628();
		}

		@Override
		protected int method_7149() {
			return 20;
		}

		@Override
		protected int method_7151() {
			return 180;
		}

		@Override
		protected void method_7148() {
			class_1581.this.method_5968().method_6092(new class_1293(class_1294.field_5919, 400));
		}

		@Override
		protected class_3414 method_7150() {
			return class_3417.field_15019;
		}

		@Override
		protected class_1617.class_1618 method_7147() {
			return class_1617.class_1618.field_7378;
		}
	}

	class class_1583 extends class_1617.class_1620 {
		private class_1583() {
		}

		@Override
		public boolean method_6264() {
			return !super.method_6264() ? false : !class_1581.this.method_6059(class_1294.field_5905);
		}

		@Override
		protected int method_7149() {
			return 20;
		}

		@Override
		protected int method_7151() {
			return 340;
		}

		@Override
		protected void method_7148() {
			class_1581.this.method_6092(new class_1293(class_1294.field_5905, 1200));
		}

		@Nullable
		@Override
		protected class_3414 method_7150() {
			return class_3417.field_14738;
		}

		@Override
		protected class_1617.class_1618 method_7147() {
			return class_1617.class_1618.field_7382;
		}
	}
}
