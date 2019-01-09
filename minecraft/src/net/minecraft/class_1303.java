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
		super(class_1299.field_6044, arg);
		this.method_5835(0.5F, 0.5F);
		this.method_5814(d, e, f);
		this.field_6031 = (float)(Math.random() * 360.0);
		this.field_5967 = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
		this.field_5984 = (double)((float)(Math.random() * 0.2) * 2.0F);
		this.field_6006 = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
		this.field_6159 = i;
	}

	public class_1303(class_1937 arg) {
		super(class_1299.field_6044, arg);
		this.method_5835(0.25F, 0.25F);
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
			this.field_5984 -= 0.03F;
		}

		if (this.field_6002.method_8316(new class_2338(this)).method_15767(class_3486.field_15518)) {
			this.field_5984 = 0.2F;
			this.field_5967 = (double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F);
			this.field_6006 = (double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F);
			this.method_5783(class_3417.field_14821, 0.4F, 2.0F + this.field_5974.nextFloat() * 0.4F);
		}

		this.method_5632(this.field_5987, (this.method_5829().field_1322 + this.method_5829().field_1325) / 2.0, this.field_6035);
		double d = 8.0;
		if (this.field_6160 < this.field_6165 - 20 + this.method_5628() % 100) {
			if (this.field_6162 == null || this.field_6162.method_5858(this) > 64.0) {
				this.field_6162 = this.field_6002.method_8614(this, 8.0);
			}

			this.field_6160 = this.field_6165;
		}

		if (this.field_6162 != null && this.field_6162.method_7325()) {
			this.field_6162 = null;
		}

		if (this.field_6162 != null) {
			double e = (this.field_6162.field_5987 - this.field_5987) / 8.0;
			double f = (this.field_6162.field_6010 + (double)this.field_6162.method_5751() / 2.0 - this.field_6010) / 8.0;
			double g = (this.field_6162.field_6035 - this.field_6035) / 8.0;
			double h = Math.sqrt(e * e + f * f + g * g);
			double i = 1.0 - h;
			if (i > 0.0) {
				i *= i;
				this.field_5967 += e / h * i * 0.1;
				this.field_5984 += f / h * i * 0.1;
				this.field_6006 += g / h * i * 0.1;
			}
		}

		this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
		float j = 0.98F;
		if (this.field_5952) {
			j = this.field_6002
					.method_8320(
						new class_2338(
							class_3532.method_15357(this.field_5987), class_3532.method_15357(this.method_5829().field_1322) - 1, class_3532.method_15357(this.field_6035)
						)
					)
					.method_11614()
					.method_9499()
				* 0.98F;
		}

		this.field_5967 *= (double)j;
		this.field_5984 *= 0.98F;
		this.field_6006 *= (double)j;
		if (this.field_5952) {
			this.field_5984 *= -0.9F;
		}

		this.field_6165++;
		this.field_6164++;
		if (this.field_6164 >= 6000) {
			this.method_5650();
		}
	}

	private void method_5921() {
		this.field_5984 += 5.0E-4F;
		this.field_5984 = Math.min(this.field_5984, 0.06F);
		this.field_5967 *= 0.99F;
		this.field_6006 *= 0.99F;
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
}
