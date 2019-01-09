package net.minecraft;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1536 extends class_1297 {
	private static final class_2940<Integer> field_7170 = class_2945.method_12791(class_1536.class, class_2943.field_13327);
	private boolean field_7176;
	private int field_7167;
	private class_1657 field_7177;
	private int field_7166;
	private int field_7173;
	private int field_7174;
	private int field_7172;
	private float field_7169;
	public class_1297 field_7165;
	private class_1536.class_1537 field_7175 = class_1536.class_1537.field_7180;
	private int field_7171;
	private int field_7168;

	private class_1536(class_1937 arg) {
		super(class_1299.field_6103, arg);
	}

	@Environment(EnvType.CLIENT)
	public class_1536(class_1937 arg, class_1657 arg2, double d, double e, double f) {
		this(arg);
		this.method_6950(arg2);
		this.method_5814(d, e, f);
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
	}

	public class_1536(class_1937 arg, class_1657 arg2) {
		this(arg);
		this.method_6950(arg2);
		this.method_6953();
	}

	private void method_6950(class_1657 arg) {
		this.method_5835(0.25F, 0.25F);
		this.field_5985 = true;
		this.field_7177 = arg;
		this.field_7177.field_7513 = this;
	}

	public void method_6955(int i) {
		this.field_7168 = i;
	}

	public void method_6956(int i) {
		this.field_7171 = i;
	}

	private void method_6953() {
		float f = this.field_7177.field_5965;
		float g = this.field_7177.field_6031;
		float h = class_3532.method_15362(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = class_3532.method_15374(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -class_3532.method_15362(-f * (float) (Math.PI / 180.0));
		float k = class_3532.method_15374(-f * (float) (Math.PI / 180.0));
		double d = this.field_7177.field_5987 - (double)i * 0.3;
		double e = this.field_7177.field_6010 + (double)this.field_7177.method_5751();
		double l = this.field_7177.field_6035 - (double)h * 0.3;
		this.method_5808(d, e, l, g, f);
		this.field_5967 = (double)(-i);
		this.field_5984 = (double)class_3532.method_15363(-(k / j), -5.0F, 5.0F);
		this.field_6006 = (double)(-h);
		float m = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_5984 * this.field_5984 + this.field_6006 * this.field_6006);
		this.field_5967 = this.field_5967 * (0.6 / (double)m + 0.5 + this.field_5974.nextGaussian() * 0.0045);
		this.field_5984 = this.field_5984 * (0.6 / (double)m + 0.5 + this.field_5974.nextGaussian() * 0.0045);
		this.field_6006 = this.field_6006 * (0.6 / (double)m + 0.5 + this.field_5974.nextGaussian() * 0.0045);
		float n = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		this.field_6031 = (float)(class_3532.method_15349(this.field_5967, this.field_6006) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(this.field_5984, (double)n) * 180.0F / (float)Math.PI);
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_7170, 0);
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7170.equals(arg)) {
			int i = this.method_5841().method_12789(field_7170);
			this.field_7165 = i > 0 ? this.field_6002.method_8469(i - 1) : null;
		}

		super.method_5674(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = 64.0;
		return d < 4096.0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_7177 == null) {
			this.method_5650();
		} else if (this.field_6002.field_9236 || !this.method_6959()) {
			if (this.field_7176) {
				this.field_7167++;
				if (this.field_7167 >= 1200) {
					this.method_5650();
					return;
				}
			}

			float f = 0.0F;
			class_2338 lv = new class_2338(this);
			class_3610 lv2 = this.field_6002.method_8316(lv);
			if (lv2.method_15767(class_3486.field_15517)) {
				f = lv2.method_15763();
			}

			if (this.field_7175 == class_1536.class_1537.field_7180) {
				if (this.field_7165 != null) {
					this.field_5967 = 0.0;
					this.field_5984 = 0.0;
					this.field_6006 = 0.0;
					this.field_7175 = class_1536.class_1537.field_7178;
					return;
				}

				if (f > 0.0F) {
					this.field_5967 *= 0.3;
					this.field_5984 *= 0.2;
					this.field_6006 *= 0.3;
					this.field_7175 = class_1536.class_1537.field_7179;
					return;
				}

				if (!this.field_6002.field_9236) {
					this.method_6958();
				}

				if (!this.field_7176 && !this.field_5952 && !this.field_5976) {
					this.field_7166++;
				} else {
					this.field_7166 = 0;
					this.field_5967 = 0.0;
					this.field_5984 = 0.0;
					this.field_6006 = 0.0;
				}
			} else {
				if (this.field_7175 == class_1536.class_1537.field_7178) {
					if (this.field_7165 != null) {
						if (this.field_7165.field_5988) {
							this.field_7165 = null;
							this.field_7175 = class_1536.class_1537.field_7180;
						} else {
							this.field_5987 = this.field_7165.field_5987;
							double var10002 = (double)this.field_7165.field_6019;
							this.field_6010 = this.field_7165.method_5829().field_1322 + var10002 * 0.8;
							this.field_6035 = this.field_7165.field_6035;
							this.method_5814(this.field_5987, this.field_6010, this.field_6035);
						}
					}

					return;
				}

				if (this.field_7175 == class_1536.class_1537.field_7179) {
					this.field_5967 *= 0.9;
					this.field_6006 *= 0.9;
					double d = this.field_6010 + this.field_5984 - (double)lv.method_10264() - (double)f;
					if (Math.abs(d) < 0.01) {
						d += Math.signum(d) * 0.1;
					}

					this.field_5984 = this.field_5984 - d * (double)this.field_5974.nextFloat() * 0.2;
					if (!this.field_6002.field_9236 && f > 0.0F) {
						this.method_6949(lv);
					}
				}
			}

			if (!lv2.method_15767(class_3486.field_15517)) {
				this.field_5984 -= 0.03;
			}

			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
			this.method_6952();
			double dx = 0.92;
			this.field_5967 *= 0.92;
			this.field_5984 *= 0.92;
			this.field_6006 *= 0.92;
			this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		}
	}

	private boolean method_6959() {
		class_1799 lv = this.field_7177.method_6047();
		class_1799 lv2 = this.field_7177.method_6079();
		boolean bl = lv.method_7909() == class_1802.field_8378;
		boolean bl2 = lv2.method_7909() == class_1802.field_8378;
		if (!this.field_7177.field_5988 && this.field_7177.method_5805() && (bl || bl2) && !(this.method_5858(this.field_7177) > 1024.0)) {
			return false;
		} else {
			this.method_5650();
			return true;
		}
	}

	private void method_6952() {
		float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		this.field_6031 = (float)(class_3532.method_15349(this.field_5967, this.field_6006) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(this.field_5984, (double)f) * 180.0F / (float)Math.PI);

		while (this.field_5965 - this.field_6004 < -180.0F) {
			this.field_6004 -= 360.0F;
		}

		while (this.field_5965 - this.field_6004 >= 180.0F) {
			this.field_6004 += 360.0F;
		}

		while (this.field_6031 - this.field_5982 < -180.0F) {
			this.field_5982 -= 360.0F;
		}

		while (this.field_6031 - this.field_5982 >= 180.0F) {
			this.field_5982 += 360.0F;
		}

		this.field_5965 = class_3532.method_16439(0.2F, this.field_6004, this.field_5965);
		this.field_6031 = class_3532.method_16439(0.2F, this.field_5982, this.field_6031);
	}

	private void method_6958() {
		class_243 lv = new class_243(this.field_5987, this.field_6010, this.field_6035);
		class_243 lv2 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		class_239 lv3 = this.field_6002.method_8531(lv, lv2, class_242.field_1348, true, false);
		lv = new class_243(this.field_5987, this.field_6010, this.field_6035);
		lv2 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		if (lv3 != null) {
			lv2 = new class_243(lv3.field_1329.field_1352, lv3.field_1329.field_1351, lv3.field_1329.field_1350);
		}

		class_1297 lv4 = null;
		List<class_1297> list = this.field_6002.method_8335(this, this.method_5829().method_1012(this.field_5967, this.field_5984, this.field_6006).method_1014(1.0));
		double d = 0.0;

		for (class_1297 lv5 : list) {
			if (this.method_6948(lv5) && (lv5 != this.field_7177 || this.field_7166 >= 5)) {
				class_238 lv6 = lv5.method_5829().method_1014(0.3F);
				class_239 lv7 = lv6.method_1004(lv, lv2);
				if (lv7 != null) {
					double e = lv.method_1025(lv7.field_1329);
					if (e < d || d == 0.0) {
						lv4 = lv5;
						d = e;
					}
				}
			}
		}

		if (lv4 != null) {
			lv3 = new class_239(lv4);
		}

		if (lv3 != null && lv3.field_1330 != class_239.class_240.field_1333) {
			if (lv3.field_1330 == class_239.class_240.field_1331) {
				this.field_7165 = lv3.field_1326;
				this.method_6951();
			} else {
				this.field_7176 = true;
			}
		}
	}

	private void method_6951() {
		this.method_5841().method_12778(field_7170, this.field_7165.method_5628() + 1);
	}

	private void method_6949(class_2338 arg) {
		class_3218 lv = (class_3218)this.field_6002;
		int i = 1;
		class_2338 lv2 = arg.method_10084();
		if (this.field_5974.nextFloat() < 0.25F && this.field_6002.method_8520(lv2)) {
			i++;
		}

		if (this.field_5974.nextFloat() < 0.5F && !this.field_6002.method_8311(lv2)) {
			i--;
		}

		if (this.field_7173 > 0) {
			this.field_7173--;
			if (this.field_7173 <= 0) {
				this.field_7174 = 0;
				this.field_7172 = 0;
			} else {
				this.field_5984 = this.field_5984 - 0.2 * (double)this.field_5974.nextFloat() * (double)this.field_5974.nextFloat();
			}
		} else if (this.field_7172 > 0) {
			this.field_7172 -= i;
			if (this.field_7172 > 0) {
				this.field_7169 = (float)((double)this.field_7169 + this.field_5974.nextGaussian() * 4.0);
				float f = this.field_7169 * (float) (Math.PI / 180.0);
				float g = class_3532.method_15374(f);
				float h = class_3532.method_15362(f);
				double d = this.field_5987 + (double)(g * (float)this.field_7172 * 0.1F);
				double e = (double)((float)class_3532.method_15357(this.method_5829().field_1322) + 1.0F);
				double j = this.field_6035 + (double)(h * (float)this.field_7172 * 0.1F);
				class_2248 lv3 = lv.method_8320(new class_2338(d, e - 1.0, j)).method_11614();
				if (lv3 == class_2246.field_10382) {
					if (this.field_5974.nextFloat() < 0.15F) {
						lv.method_14199(class_2398.field_11247, d, e - 0.1F, j, 1, (double)g, 0.1, (double)h, 0.0);
					}

					float k = g * 0.04F;
					float l = h * 0.04F;
					lv.method_14199(class_2398.field_11244, d, e, j, 0, (double)l, 0.01, (double)(-k), 1.0);
					lv.method_14199(class_2398.field_11244, d, e, j, 0, (double)(-l), 0.01, (double)k, 1.0);
				}
			} else {
				this.field_5984 = (double)(-0.4F * class_3532.method_15344(this.field_5974, 0.6F, 1.0F));
				this.method_5783(class_3417.field_14660, 0.25F, 1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.4F);
				double m = this.method_5829().field_1322 + 0.5;
				lv.method_14199(
					class_2398.field_11247,
					this.field_5987,
					m,
					this.field_6035,
					(int)(1.0F + this.field_5998 * 20.0F),
					(double)this.field_5998,
					0.0,
					(double)this.field_5998,
					0.2F
				);
				lv.method_14199(
					class_2398.field_11244,
					this.field_5987,
					m,
					this.field_6035,
					(int)(1.0F + this.field_5998 * 20.0F),
					(double)this.field_5998,
					0.0,
					(double)this.field_5998,
					0.2F
				);
				this.field_7173 = class_3532.method_15395(this.field_5974, 20, 40);
			}
		} else if (this.field_7174 > 0) {
			this.field_7174 -= i;
			float f = 0.15F;
			if (this.field_7174 < 20) {
				f = (float)((double)f + (double)(20 - this.field_7174) * 0.05);
			} else if (this.field_7174 < 40) {
				f = (float)((double)f + (double)(40 - this.field_7174) * 0.02);
			} else if (this.field_7174 < 60) {
				f = (float)((double)f + (double)(60 - this.field_7174) * 0.01);
			}

			if (this.field_5974.nextFloat() < f) {
				float g = class_3532.method_15344(this.field_5974, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
				float h = class_3532.method_15344(this.field_5974, 25.0F, 60.0F);
				double d = this.field_5987 + (double)(class_3532.method_15374(g) * h * 0.1F);
				double e = (double)((float)class_3532.method_15357(this.method_5829().field_1322) + 1.0F);
				double j = this.field_6035 + (double)(class_3532.method_15362(g) * h * 0.1F);
				class_2248 lv3 = lv.method_8320(new class_2338((int)d, (int)e - 1, (int)j)).method_11614();
				if (lv3 == class_2246.field_10382) {
					lv.method_14199(class_2398.field_11202, d, e, j, 2 + this.field_5974.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
				}
			}

			if (this.field_7174 <= 0) {
				this.field_7169 = class_3532.method_15344(this.field_5974, 0.0F, 360.0F);
				this.field_7172 = class_3532.method_15395(this.field_5974, 20, 80);
			}
		} else {
			this.field_7174 = class_3532.method_15395(this.field_5974, 100, 600);
			this.field_7174 = this.field_7174 - this.field_7168 * 20 * 5;
		}
	}

	protected boolean method_6948(class_1297 arg) {
		return arg.method_5863() || arg instanceof class_1542;
	}

	@Override
	public void method_5652(class_2487 arg) {
	}

	@Override
	public void method_5749(class_2487 arg) {
	}

	public int method_6957(class_1799 arg) {
		if (!this.field_6002.field_9236 && this.field_7177 != null) {
			int i = 0;
			if (this.field_7165 != null) {
				this.method_6954();
				class_174.field_1203.method_8939((class_3222)this.field_7177, arg, this, Collections.emptyList());
				this.field_6002.method_8421(this, (byte)31);
				i = this.field_7165 instanceof class_1542 ? 3 : 5;
			} else if (this.field_7173 > 0) {
				class_47.class_48 lv = new class_47.class_48((class_3218)this.field_6002)
					.method_312(class_181.field_1232, new class_2338(this))
					.method_312(class_181.field_1229, arg)
					.method_311(this.field_5974)
					.method_303((float)this.field_7171 + this.field_7177.method_7292());
				class_52 lv2 = this.field_6002.method_8503().method_3857().method_367(class_39.field_353);
				List<class_1799> list = lv2.method_319(lv.method_309(class_173.field_1176));
				class_174.field_1203.method_8939((class_3222)this.field_7177, arg, this, list);

				for (class_1799 lv3 : list) {
					class_1542 lv4 = new class_1542(this.field_6002, this.field_5987, this.field_6010, this.field_6035, lv3);
					double d = this.field_7177.field_5987 - this.field_5987;
					double e = this.field_7177.field_6010 - this.field_6010;
					double f = this.field_7177.field_6035 - this.field_6035;
					double g = (double)class_3532.method_15368(d * d + e * e + f * f);
					double h = 0.1;
					lv4.field_5967 = d * 0.1;
					lv4.field_5984 = e * 0.1 + (double)class_3532.method_15368(g) * 0.08;
					lv4.field_6006 = f * 0.1;
					this.field_6002.method_8649(lv4);
					this.field_7177
						.field_6002
						.method_8649(
							new class_1303(
								this.field_7177.field_6002,
								this.field_7177.field_5987,
								this.field_7177.field_6010 + 0.5,
								this.field_7177.field_6035 + 0.5,
								this.field_5974.nextInt(6) + 1
							)
						);
					if (lv3.method_7909().method_7855(class_3489.field_15527)) {
						this.field_7177.method_7339(class_3468.field_15391, 1);
					}
				}

				i = 1;
			}

			if (this.field_7176) {
				i = 2;
			}

			this.method_5650();
			return i;
		} else {
			return 0;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 31 && this.field_6002.field_9236 && this.field_7165 instanceof class_1657 && ((class_1657)this.field_7165).method_7340()) {
			this.method_6954();
		}

		super.method_5711(b);
	}

	protected void method_6954() {
		if (this.field_7177 != null) {
			double d = this.field_7177.field_5987 - this.field_5987;
			double e = this.field_7177.field_6010 - this.field_6010;
			double f = this.field_7177.field_6035 - this.field_6035;
			double g = 0.1;
			this.field_7165.field_5967 += d * 0.1;
			this.field_7165.field_5984 += e * 0.1;
			this.field_7165.field_6006 += f * 0.1;
		}
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public void method_5650() {
		super.method_5650();
		if (this.field_7177 != null) {
			this.field_7177.field_7513 = null;
		}
	}

	public class_1657 method_6947() {
		return this.field_7177;
	}

	@Override
	public boolean method_5822() {
		return false;
	}

	static enum class_1537 {
		field_7180,
		field_7178,
		field_7179;
	}
}
