package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1528 extends class_1588 implements class_1603 {
	private static final class_2940<Integer> field_7088 = class_2945.method_12791(class_1528.class, class_2943.field_13327);
	private static final class_2940<Integer> field_7090 = class_2945.method_12791(class_1528.class, class_2943.field_13327);
	private static final class_2940<Integer> field_7089 = class_2945.method_12791(class_1528.class, class_2943.field_13327);
	private static final List<class_2940<Integer>> field_7087 = ImmutableList.of(field_7088, field_7090, field_7089);
	private static final class_2940<Integer> field_7085 = class_2945.method_12791(class_1528.class, class_2943.field_13327);
	private final float[] field_7084 = new float[2];
	private final float[] field_7083 = new float[2];
	private final float[] field_7095 = new float[2];
	private final float[] field_7094 = new float[2];
	private final int[] field_7091 = new int[2];
	private final int[] field_7092 = new int[2];
	private int field_7082;
	private final class_3213 field_7093 = (class_3213)new class_3213(this.method_5476(), class_1259.class_1260.field_5783, class_1259.class_1261.field_5795)
		.method_5406(true);
	private static final Predicate<class_1297> field_7086 = arg -> arg instanceof class_1309
			&& ((class_1309)arg).method_6046() != class_1310.field_6289
			&& ((class_1309)arg).method_6102();

	public class_1528(class_1937 arg) {
		super(class_1299.field_6119, arg);
		this.method_6033(this.method_6063());
		this.method_5835(0.9F, 3.5F);
		this.field_5977 = true;
		((class_1409)this.method_5942()).method_6354(true);
		this.field_6194 = 50;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1528.class_1529());
		this.field_6201.method_6277(2, new class_1381(this, 1.0, 40, 20.0F));
		this.field_6201.method_6277(5, new class_1394(this, 1.0));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(7, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this));
		this.field_6185.method_6277(2, new class_1400(this, class_1308.class, 0, false, false, field_7086));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7088, 0);
		this.field_6011.method_12784(field_7090, 0);
		this.field_6011.method_12784(field_7089, 0);
		this.field_6011.method_12784(field_7085, 0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Invul", this.method_6884());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6875(arg.method_10550("Invul"));
		if (this.method_16914()) {
			this.field_7093.method_5413(this.method_5476());
		}
	}

	@Override
	public void method_5665(@Nullable class_2561 arg) {
		super.method_5665(arg);
		this.field_7093.method_5413(this.method_5476());
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15163;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14688;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15136;
	}

	@Override
	public void method_6007() {
		this.field_5984 *= 0.6F;
		if (!this.field_6002.field_9236 && this.method_6882(0) > 0) {
			class_1297 lv = this.field_6002.method_8469(this.method_6882(0));
			if (lv != null) {
				if (this.field_6010 < lv.field_6010 || !this.method_6872() && this.field_6010 < lv.field_6010 + 5.0) {
					if (this.field_5984 < 0.0) {
						this.field_5984 = 0.0;
					}

					this.field_5984 = this.field_5984 + (0.5 - this.field_5984) * 0.6F;
				}

				double d = lv.field_5987 - this.field_5987;
				double e = lv.field_6035 - this.field_6035;
				double f = d * d + e * e;
				if (f > 9.0) {
					double g = (double)class_3532.method_15368(f);
					this.field_5967 = this.field_5967 + (d / g * 0.5 - this.field_5967) * 0.6F;
					this.field_6006 = this.field_6006 + (e / g * 0.5 - this.field_6006) * 0.6F;
				}
			}
		}

		if (this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006 > 0.05F) {
			this.field_6031 = (float)class_3532.method_15349(this.field_6006, this.field_5967) * (180.0F / (float)Math.PI) - 90.0F;
		}

		super.method_6007();

		for (int i = 0; i < 2; i++) {
			this.field_7094[i] = this.field_7083[i];
			this.field_7095[i] = this.field_7084[i];
		}

		for (int i = 0; i < 2; i++) {
			int j = this.method_6882(i + 1);
			class_1297 lv2 = null;
			if (j > 0) {
				lv2 = this.field_6002.method_8469(j);
			}

			if (lv2 != null) {
				double e = this.method_6874(i + 1);
				double f = this.method_6880(i + 1);
				double g = this.method_6881(i + 1);
				double h = lv2.field_5987 - e;
				double k = lv2.field_6010 + (double)lv2.method_5751() - f;
				double l = lv2.field_6035 - g;
				double m = (double)class_3532.method_15368(h * h + l * l);
				float n = (float)(class_3532.method_15349(l, h) * 180.0F / (float)Math.PI) - 90.0F;
				float o = (float)(-(class_3532.method_15349(k, m) * 180.0F / (float)Math.PI));
				this.field_7084[i] = this.method_6886(this.field_7084[i], o, 40.0F);
				this.field_7083[i] = this.method_6886(this.field_7083[i], n, 10.0F);
			} else {
				this.field_7083[i] = this.method_6886(this.field_7083[i], this.field_6283, 10.0F);
			}
		}

		boolean bl = this.method_6872();

		for (int jx = 0; jx < 3; jx++) {
			double p = this.method_6874(jx);
			double q = this.method_6880(jx);
			double r = this.method_6881(jx);
			this.field_6002
				.method_8406(
					class_2398.field_11251,
					p + this.field_5974.nextGaussian() * 0.3F,
					q + this.field_5974.nextGaussian() * 0.3F,
					r + this.field_5974.nextGaussian() * 0.3F,
					0.0,
					0.0,
					0.0
				);
			if (bl && this.field_6002.field_9229.nextInt(4) == 0) {
				this.field_6002
					.method_8406(
						class_2398.field_11226,
						p + this.field_5974.nextGaussian() * 0.3F,
						q + this.field_5974.nextGaussian() * 0.3F,
						r + this.field_5974.nextGaussian() * 0.3F,
						0.7F,
						0.7F,
						0.5
					);
			}
		}

		if (this.method_6884() > 0) {
			for (int jxx = 0; jxx < 3; jxx++) {
				this.field_6002
					.method_8406(
						class_2398.field_11226,
						this.field_5987 + this.field_5974.nextGaussian(),
						this.field_6010 + (double)(this.field_5974.nextFloat() * 3.3F),
						this.field_6035 + this.field_5974.nextGaussian(),
						0.7F,
						0.7F,
						0.9F
					);
			}
		}
	}

	@Override
	protected void method_5958() {
		if (this.method_6884() > 0) {
			int i = this.method_6884() - 1;
			if (i <= 0) {
				this.field_6002
					.method_8537(
						this,
						this.field_5987,
						this.field_6010 + (double)this.method_5751(),
						this.field_6035,
						7.0F,
						false,
						this.field_6002.method_8450().method_8355("mobGriefing")
					);
				this.field_6002.method_8474(1023, new class_2338(this), 0);
			}

			this.method_6875(i);
			if (this.field_6012 % 10 == 0) {
				this.method_6025(10.0F);
			}
		} else {
			super.method_5958();

			for (int ix = 1; ix < 3; ix++) {
				if (this.field_6012 >= this.field_7091[ix - 1]) {
					this.field_7091[ix - 1] = this.field_6012 + 10 + this.field_5974.nextInt(10);
					if ((this.field_6002.method_8407() == class_1267.field_5802 || this.field_6002.method_8407() == class_1267.field_5807) && this.field_7092[ix - 1]++ > 15) {
						float f = 10.0F;
						float g = 5.0F;
						double d = class_3532.method_15366(this.field_5974, this.field_5987 - 10.0, this.field_5987 + 10.0);
						double e = class_3532.method_15366(this.field_5974, this.field_6010 - 5.0, this.field_6010 + 5.0);
						double h = class_3532.method_15366(this.field_5974, this.field_6035 - 10.0, this.field_6035 + 10.0);
						this.method_6877(ix + 1, d, e, h, true);
						this.field_7092[ix - 1] = 0;
					}

					int j = this.method_6882(ix);
					if (j > 0) {
						class_1297 lv = this.field_6002.method_8469(j);
						if (lv == null || !lv.method_5805() || this.method_5858(lv) > 900.0 || !this.method_6057(lv)) {
							this.method_6876(ix, 0);
						} else if (lv instanceof class_1657 && ((class_1657)lv).field_7503.field_7480) {
							this.method_6876(ix, 0);
						} else {
							this.method_6878(ix + 1, (class_1309)lv);
							this.field_7091[ix - 1] = this.field_6012 + 40 + this.field_5974.nextInt(20);
							this.field_7092[ix - 1] = 0;
						}
					} else {
						List<class_1309> list = this.field_6002
							.method_8390(class_1309.class, this.method_5829().method_1009(20.0, 8.0, 20.0), field_7086.and(class_1301.field_6155));

						for (int k = 0; k < 10 && !list.isEmpty(); k++) {
							class_1309 lv2 = (class_1309)list.get(this.field_5974.nextInt(list.size()));
							if (lv2 != this && lv2.method_5805() && this.method_6057(lv2)) {
								if (lv2 instanceof class_1657) {
									if (!((class_1657)lv2).field_7503.field_7480) {
										this.method_6876(ix, lv2.method_5628());
									}
								} else {
									this.method_6876(ix, lv2.method_5628());
								}
								break;
							}

							list.remove(lv2);
						}
					}
				}
			}

			if (this.method_5968() != null) {
				this.method_6876(0, this.method_5968().method_5628());
			} else {
				this.method_6876(0, 0);
			}

			if (this.field_7082 > 0) {
				this.field_7082--;
				if (this.field_7082 == 0 && this.field_6002.method_8450().method_8355("mobGriefing")) {
					int ixx = class_3532.method_15357(this.field_6010);
					int j = class_3532.method_15357(this.field_5987);
					int l = class_3532.method_15357(this.field_6035);
					boolean bl = false;

					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							for (int o = 0; o <= 3; o++) {
								int p = j + m;
								int q = ixx + o;
								int r = l + n;
								class_2338 lv3 = new class_2338(p, q, r);
								class_2680 lv4 = this.field_6002.method_8320(lv3);
								class_2248 lv5 = lv4.method_11614();
								if (!lv4.method_11588() && method_6883(lv5)) {
									bl = this.field_6002.method_8651(lv3, true) || bl;
								}
							}
						}
					}

					if (bl) {
						this.field_6002.method_8444(null, 1022, new class_2338(this), 0);
					}
				}
			}

			if (this.field_6012 % 20 == 0) {
				this.method_6025(1.0F);
			}

			this.field_7093.method_5408(this.method_6032() / this.method_6063());
		}
	}

	public static boolean method_6883(class_2248 arg) {
		return arg != class_2246.field_9987
			&& arg != class_2246.field_10027
			&& arg != class_2246.field_10398
			&& arg != class_2246.field_10525
			&& arg != class_2246.field_10263
			&& arg != class_2246.field_10395
			&& arg != class_2246.field_10499
			&& arg != class_2246.field_10465
			&& arg != class_2246.field_10369
			&& arg != class_2246.field_10008
			&& arg != class_2246.field_10613;
	}

	public void method_6885() {
		this.method_6875(220);
		this.method_6033(this.method_6063() / 3.0F);
	}

	@Override
	public void method_5844(class_2680 arg, class_243 arg2) {
	}

	@Override
	public void method_5837(class_3222 arg) {
		super.method_5837(arg);
		this.field_7093.method_14088(arg);
	}

	@Override
	public void method_5742(class_3222 arg) {
		super.method_5742(arg);
		this.field_7093.method_14089(arg);
	}

	private double method_6874(int i) {
		if (i <= 0) {
			return this.field_5987;
		} else {
			float f = (this.field_6283 + (float)(180 * (i - 1))) * (float) (Math.PI / 180.0);
			float g = class_3532.method_15362(f);
			return this.field_5987 + (double)g * 1.3;
		}
	}

	private double method_6880(int i) {
		return i <= 0 ? this.field_6010 + 3.0 : this.field_6010 + 2.2;
	}

	private double method_6881(int i) {
		if (i <= 0) {
			return this.field_6035;
		} else {
			float f = (this.field_6283 + (float)(180 * (i - 1))) * (float) (Math.PI / 180.0);
			float g = class_3532.method_15374(f);
			return this.field_6035 + (double)g * 1.3;
		}
	}

	private float method_6886(float f, float g, float h) {
		float i = class_3532.method_15393(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
	}

	private void method_6878(int i, class_1309 arg) {
		this.method_6877(i, arg.field_5987, arg.field_6010 + (double)arg.method_5751() * 0.5, arg.field_6035, i == 0 && this.field_5974.nextFloat() < 0.001F);
	}

	private void method_6877(int i, double d, double e, double f, boolean bl) {
		this.field_6002.method_8444(null, 1024, new class_2338(this), 0);
		double g = this.method_6874(i);
		double h = this.method_6880(i);
		double j = this.method_6881(i);
		double k = d - g;
		double l = e - h;
		double m = f - j;
		class_1687 lv = new class_1687(this.field_6002, this, k, l, m);
		if (bl) {
			lv.method_7502(true);
		}

		lv.field_6010 = h;
		lv.field_5987 = g;
		lv.field_6035 = j;
		this.field_6002.method_8649(lv);
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		this.method_6878(0, arg);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (arg == class_1282.field_5859 || arg.method_5529() instanceof class_1528) {
			return false;
		} else if (this.method_6884() > 0 && arg != class_1282.field_5849) {
			return false;
		} else {
			if (this.method_6872()) {
				class_1297 lv = arg.method_5526();
				if (lv instanceof class_1665) {
					return false;
				}
			}

			class_1297 lv = arg.method_5529();
			if (lv != null && !(lv instanceof class_1657) && lv instanceof class_1309 && ((class_1309)lv).method_6046() == this.method_6046()) {
				return false;
			} else {
				if (this.field_7082 <= 0) {
					this.field_7082 = 20;
				}

				for (int i = 0; i < this.field_7092.length; i++) {
					this.field_7092[i] = this.field_7092[i] + 3;
				}

				return super.method_5643(arg, f);
			}
		}
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);
		class_1542 lv = this.method_5706(class_1802.field_8137);
		if (lv != null) {
			lv.method_6976();
		}
	}

	@Override
	protected void method_5982() {
		this.field_6278 = 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		return 15728880;
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	public boolean method_6092(class_1293 arg) {
		return false;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(300.0);
		this.method_5996(class_1612.field_7357).method_6192(0.6F);
		this.method_5996(class_1612.field_7365).method_6192(40.0);
		this.method_5996(class_1612.field_7358).method_6192(4.0);
	}

	@Environment(EnvType.CLIENT)
	public float method_6879(int i) {
		return this.field_7083[i];
	}

	@Environment(EnvType.CLIENT)
	public float method_6887(int i) {
		return this.field_7084[i];
	}

	public int method_6884() {
		return this.field_6011.method_12789(field_7085);
	}

	public void method_6875(int i) {
		this.field_6011.method_12778(field_7085, i);
	}

	public int method_6882(int i) {
		return this.field_6011.<Integer>method_12789((class_2940<Integer>)field_7087.get(i));
	}

	public void method_6876(int i, int j) {
		this.field_6011.method_12778((class_2940<Integer>)field_7087.get(i), j);
	}

	public boolean method_6872() {
		return this.method_6032() <= this.method_6063() / 2.0F;
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6289;
	}

	@Override
	protected boolean method_5860(class_1297 arg) {
		return false;
	}

	@Override
	public boolean method_5822() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_6999() {
		return false;
	}

	@Override
	public void method_7106(boolean bl) {
	}

	@Override
	public boolean method_6049(class_1293 arg) {
		return arg.method_5579() == class_1294.field_5920 ? false : super.method_6049(arg);
	}

	class class_1529 extends class_1352 {
		public class_1529() {
			this.method_6265(7);
		}

		@Override
		public boolean method_6264() {
			return class_1528.this.method_6884() > 0;
		}
	}
}
