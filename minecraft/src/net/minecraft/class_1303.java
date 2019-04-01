package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1303 extends class_1297 {
	public int field_6165;
	public int field_6164;
	public int field_6163;
	private int field_6161 = 5;
	private int field_6159;
	private class_1657 field_6162;
	private int field_6160;

	public class_1303(class_1937 arg, double d, double e, double f, int i) {
		this(class_1299.field_6044, arg);
		this.method_5814(d, e, f);
		this.field_6031 = (float)(this.field_5974.nextDouble() * 360.0);
		this.method_18800(
			(this.field_5974.nextDouble() * 0.2F - 0.1F) * 2.0, this.field_5974.nextDouble() * 0.2 * 2.0, (this.field_5974.nextDouble() * 0.2F - 0.1F) * 2.0
		);
		this.field_6159 = i;
	}

	public class_1303(class_1299<? extends class_1303> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void method_5693() {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		float f = 0.5F;
		f = class_3532.method_15363(f, 0.0F, 1.0F);
		int i = super.method_5635();
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6163 > 0) {
			this.field_6163--;
		}

		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		if (this.method_5777(class_3486.field_15517)) {
			this.method_5921();
		} else if (!this.method_5740()) {
			this.method_18799(this.method_18798().method_1031(0.0, -0.03, 0.0));
		}

		if (this.field_6002.method_8316(new class_2338(this)).method_15767(class_3486.field_15518)) {
			this.method_18800(
				(double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F),
				0.2F,
				(double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F)
			);
			this.method_5783(class_3417.field_14821, 0.4F, 2.0F + this.field_5974.nextFloat() * 0.4F);
		}

		if (!this.field_6002.method_18026(this.method_5829())) {
			this.method_5632(this.field_5987, (this.method_5829().field_1322 + this.method_5829().field_1325) / 2.0, this.field_6035);
		}

		double d = 8.0;
		if (this.field_6160 < this.field_6165 - 20 + this.method_5628() % 100) {
			if (this.field_6162 == null || this.field_6162.method_5858(this) > 64.0) {
				this.field_6162 = this.field_6002.method_18460(this, 8.0);
			}

			this.field_6160 = this.field_6165;
		}

		if (this.field_6162 != null && this.field_6162.method_7325()) {
			this.field_6162 = null;
		}

		if (this.field_6162 != null) {
			class_243 lv = new class_243(
				this.field_6162.field_5987 - this.field_5987,
				this.field_6162.field_6010 + (double)this.field_6162.method_5751() / 2.0 - this.field_6010,
				this.field_6162.field_6035 - this.field_6035
			);
			double e = lv.method_1027();
			if (e < 64.0) {
				double f = 1.0 - Math.sqrt(e) / 8.0;
				this.method_18799(this.method_18798().method_1019(lv.method_1029().method_1021(f * f * 0.1)));
			}
		}

		this.method_5784(class_1313.field_6308, this.method_18798());
		float g = 0.98F;
		if (this.field_5952) {
			g = this.field_6002.method_8320(new class_2338(this.field_5987, this.method_5829().field_1322 - 1.0, this.field_6035)).method_11614().method_9499() * 0.98F;
		}

		this.method_18799(this.method_18798().method_18805((double)g, 0.98, (double)g));
		if (this.field_5952) {
			this.method_18799(this.method_18798().method_18805(1.0, -0.9, 1.0));
		}

		this.field_6165++;
		this.field_6164++;
		if (this.field_6164 >= 6000) {
			this.method_5650();
		}
	}

	private void method_5921() {
		class_243 lv = this.method_18798();
		this.method_18800(lv.field_1352 * 0.99F, Math.min(lv.field_1351 + 5.0E-4F, 0.06F), lv.field_1350 * 0.99F);
	}

	@Override
	protected void method_5746() {
	}

	@Override
	protected void method_5714(int i) {
		this.method_5643(class_1282.field_5867, (float)i);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			this.method_5785();
			this.field_6161 = (int)((float)this.field_6161 - f);
			if (this.field_6161 <= 0) {
				this.method_5650();
			}

			return false;
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10575("Health", (short)this.field_6161);
		arg.method_10575("Age", (short)this.field_6164);
		arg.method_10575("Value", (short)this.field_6159);
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_6161 = arg.method_10568("Health");
		this.field_6164 = arg.method_10568("Age");
		this.field_6159 = arg.method_10568("Value");
	}

	@Override
	public void method_5694(class_1657 arg) {
		if (!this.field_6002.field_9236) {
			if (this.field_6163 == 0 && arg.field_7504 == 0) {
				arg.field_7504 = 2;
				arg.method_6103(this, 1);
				class_1799 lv = class_1890.method_8204(class_1893.field_9101, arg);
				if (!lv.method_7960() && lv.method_7986()) {
					int i = Math.min(this.method_5917(this.field_6159), lv.method_7919());
					this.field_6159 = this.field_6159 - this.method_5922(i);
					lv.method_7974(lv.method_7919() - i);
				}

				if (this.field_6159 > 0) {
					arg.method_7255(this.field_6159);
				}

				this.method_5650();
			}
		}
	}

	private int method_5922(int i) {
		return i / 2;
	}

	private int method_5917(int i) {
		return i * 2;
	}

	public int method_5919() {
		return this.field_6159;
	}

	@Environment(EnvType.CLIENT)
	public int method_5920() {
		if (this.field_6159 >= 2477) {
			return 10;
		} else if (this.field_6159 >= 1237) {
			return 9;
		} else if (this.field_6159 >= 617) {
			return 8;
		} else if (this.field_6159 >= 307) {
			return 7;
		} else if (this.field_6159 >= 149) {
			return 6;
		} else if (this.field_6159 >= 73) {
			return 5;
		} else if (this.field_6159 >= 37) {
			return 4;
		} else if (this.field_6159 >= 17) {
			return 3;
		} else if (this.field_6159 >= 7) {
			return 2;
		} else {
			return this.field_6159 >= 3 ? 1 : 0;
		}
	}

	public static int method_5918(int i) {
		if (i >= 2477) {
			return 2477;
		} else if (i >= 1237) {
			return 1237;
		} else if (i >= 617) {
			return 617;
		} else if (i >= 307) {
			return 307;
		} else if (i >= 149) {
			return 149;
		} else if (i >= 73) {
			return 73;
		} else if (i >= 37) {
			return 37;
		} else if (i >= 17) {
			return 17;
		} else if (i >= 7) {
			return 7;
		} else {
			return i >= 3 ? 3 : 1;
		}
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2606(this);
	}
}
