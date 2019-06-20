package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1477 extends class_1480 {
	public float field_6907;
	public float field_6905;
	public float field_6903;
	public float field_6906;
	public float field_6908;
	public float field_6902;
	public float field_6904;
	public float field_6900;
	private float field_6901;
	private float field_6912;
	private float field_6913;
	private float field_6910;
	private float field_6911;
	private float field_6909;

	public class_1477(class_1299<? extends class_1477> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_5974.setSeed((long)this.method_5628());
		this.field_6912 = 1.0F / (this.field_5974.nextFloat() + 1.0F) * 0.2F;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1477.class_1479(this));
		this.field_6201.method_6277(1, new class_1477.class_1478());
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.5F;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15034;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15212;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15124;
	}

	@Override
	protected float method_6107() {
		return 0.4F;
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public void method_6007() {
		super.method_6007();
		this.field_6905 = this.field_6907;
		this.field_6906 = this.field_6903;
		this.field_6902 = this.field_6908;
		this.field_6900 = this.field_6904;
		this.field_6908 = this.field_6908 + this.field_6912;
		if ((double)this.field_6908 > Math.PI * 2) {
			if (this.field_6002.field_9236) {
				this.field_6908 = (float) (Math.PI * 2);
			} else {
				this.field_6908 = (float)((double)this.field_6908 - (Math.PI * 2));
				if (this.field_5974.nextInt(10) == 0) {
					this.field_6912 = 1.0F / (this.field_5974.nextFloat() + 1.0F) * 0.2F;
				}

				this.field_6002.method_8421(this, (byte)19);
			}
		}

		if (this.method_5816()) {
			if (this.field_6908 < (float) Math.PI) {
				float f = this.field_6908 / (float) Math.PI;
				this.field_6904 = class_3532.method_15374(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
				if ((double)f > 0.75) {
					this.field_6901 = 1.0F;
					this.field_6913 = 1.0F;
				} else {
					this.field_6913 *= 0.8F;
				}
			} else {
				this.field_6904 = 0.0F;
				this.field_6901 *= 0.9F;
				this.field_6913 *= 0.99F;
			}

			if (!this.field_6002.field_9236) {
				this.method_18800((double)(this.field_6910 * this.field_6901), (double)(this.field_6911 * this.field_6901), (double)(this.field_6909 * this.field_6901));
			}

			class_243 lv = this.method_18798();
			float g = class_3532.method_15368(method_17996(lv));
			this.field_6283 = this.field_6283 + (-((float)class_3532.method_15349(lv.field_1352, lv.field_1350)) * (180.0F / (float)Math.PI) - this.field_6283) * 0.1F;
			this.field_6031 = this.field_6283;
			this.field_6903 = (float)((double)this.field_6903 + Math.PI * (double)this.field_6913 * 1.5);
			this.field_6907 = this.field_6907 + (-((float)class_3532.method_15349((double)g, lv.field_1351)) * (180.0F / (float)Math.PI) - this.field_6907) * 0.1F;
		} else {
			this.field_6904 = class_3532.method_15379(class_3532.method_15374(this.field_6908)) * (float) Math.PI * 0.25F;
			if (!this.field_6002.field_9236) {
				double d = this.method_18798().field_1351;
				if (this.method_6059(class_1294.field_5902)) {
					d = 0.05 * (double)(this.method_6112(class_1294.field_5902).method_5578() + 1);
				} else if (!this.method_5740()) {
					d -= 0.08;
				}

				this.method_18800(0.0, d * 0.98F, 0.0);
			}

			this.field_6907 = (float)((double)this.field_6907 + (double)(-90.0F - this.field_6907) * 0.02);
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (super.method_5643(arg, f) && this.method_6065() != null) {
			this.method_6669();
			return true;
		} else {
			return false;
		}
	}

	private class_243 method_6671(class_243 arg) {
		class_243 lv = arg.method_1037(this.field_6905 * (float) (Math.PI / 180.0));
		return lv.method_1024(-this.field_6220 * (float) (Math.PI / 180.0));
	}

	private void method_6669() {
		this.method_5783(class_3417.field_15121, this.method_6107(), this.method_6017());
		class_243 lv = this.method_6671(new class_243(0.0, -1.0, 0.0)).method_1031(this.field_5987, this.field_6010, this.field_6035);

		for (int i = 0; i < 30; i++) {
			class_243 lv2 = this.method_6671(new class_243((double)this.field_5974.nextFloat() * 0.6 - 0.3, -1.0, (double)this.field_5974.nextFloat() * 0.6 - 0.3));
			class_243 lv3 = lv2.method_1021(0.3 + (double)(this.field_5974.nextFloat() * 2.0F));
			((class_3218)this.field_6002)
				.method_14199(class_2398.field_11233, lv.field_1352, lv.field_1351 + 0.5, lv.field_1350, 0, lv3.field_1352, lv3.field_1351, lv3.field_1350, 0.1F);
		}
	}

	@Override
	public void method_6091(class_243 arg) {
		this.method_5784(class_1313.field_6308, this.method_18798());
	}

	public static boolean method_20670(class_1299<class_1477> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		return arg4.method_10264() > 45 && arg4.method_10264() < arg2.method_8615();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 19) {
			this.field_6908 = 0.0F;
		} else {
			super.method_5711(b);
		}
	}

	public void method_6670(float f, float g, float h) {
		this.field_6910 = f;
		this.field_6911 = g;
		this.field_6909 = h;
	}

	public boolean method_6672() {
		return this.field_6910 != 0.0F || this.field_6911 != 0.0F || this.field_6909 != 0.0F;
	}

	class class_1478 extends class_1352 {
		private int field_6915;

		private class_1478() {
		}

		@Override
		public boolean method_6264() {
			class_1309 lv = class_1477.this.method_6065();
			return class_1477.this.method_5799() && lv != null ? class_1477.this.method_5858(lv) < 100.0 : false;
		}

		@Override
		public void method_6269() {
			this.field_6915 = 0;
		}

		@Override
		public void method_6268() {
			this.field_6915++;
			class_1309 lv = class_1477.this.method_6065();
			if (lv != null) {
				class_243 lv2 = new class_243(
					class_1477.this.field_5987 - lv.field_5987, class_1477.this.field_6010 - lv.field_6010, class_1477.this.field_6035 - lv.field_6035
				);
				class_2680 lv3 = class_1477.this.field_6002
					.method_8320(
						new class_2338(class_1477.this.field_5987 + lv2.field_1352, class_1477.this.field_6010 + lv2.field_1351, class_1477.this.field_6035 + lv2.field_1350)
					);
				class_3610 lv4 = class_1477.this.field_6002
					.method_8316(
						new class_2338(class_1477.this.field_5987 + lv2.field_1352, class_1477.this.field_6010 + lv2.field_1351, class_1477.this.field_6035 + lv2.field_1350)
					);
				if (lv4.method_15767(class_3486.field_15517) || lv3.method_11588()) {
					double d = lv2.method_1033();
					if (d > 0.0) {
						lv2.method_1029();
						float f = 3.0F;
						if (d > 5.0) {
							f = (float)((double)f - (d - 5.0) / 5.0);
						}

						if (f > 0.0F) {
							lv2 = lv2.method_1021((double)f);
						}
					}

					if (lv3.method_11588()) {
						lv2 = lv2.method_1023(0.0, lv2.field_1351, 0.0);
					}

					class_1477.this.method_6670((float)lv2.field_1352 / 20.0F, (float)lv2.field_1351 / 20.0F, (float)lv2.field_1350 / 20.0F);
				}

				if (this.field_6915 % 10 == 5) {
					class_1477.this.field_6002
						.method_8406(class_2398.field_11247, class_1477.this.field_5987, class_1477.this.field_6010, class_1477.this.field_6035, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	class class_1479 extends class_1352 {
		private final class_1477 field_6917;

		public class_1479(class_1477 arg2) {
			this.field_6917 = arg2;
		}

		@Override
		public boolean method_6264() {
			return true;
		}

		@Override
		public void method_6268() {
			int i = this.field_6917.method_6131();
			if (i > 100) {
				this.field_6917.method_6670(0.0F, 0.0F, 0.0F);
			} else if (this.field_6917.method_6051().nextInt(50) == 0 || !this.field_6917.field_5957 || !this.field_6917.method_6672()) {
				float f = this.field_6917.method_6051().nextFloat() * (float) (Math.PI * 2);
				float g = class_3532.method_15362(f) * 0.2F;
				float h = -0.1F + this.field_6917.method_6051().nextFloat() * 0.2F;
				float j = class_3532.method_15374(f) * 0.2F;
				this.field_6917.method_6670(g, h, j);
			}
		}
	}
}
