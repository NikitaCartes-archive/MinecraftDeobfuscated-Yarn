package net.minecraft;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1577 extends class_1588 {
	private static final class_2940<Boolean> field_7280 = class_2945.method_12791(class_1577.class, class_2943.field_13323);
	private static final class_2940<Integer> field_7290 = class_2945.method_12791(class_1577.class, class_2943.field_13327);
	protected float field_7286;
	protected float field_7284;
	protected float field_7281;
	protected float field_7285;
	protected float field_7287;
	private class_1309 field_7288;
	private int field_7282;
	private boolean field_7283;
	protected class_1379 field_7289;

	public class_1577(class_1299<? extends class_1577> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6194 = 10;
		this.field_6207 = new class_1577.class_1580(this);
		this.field_7286 = this.field_5974.nextFloat();
		this.field_7284 = this.field_7286;
	}

	@Override
	protected void method_5959() {
		class_1370 lv = new class_1370(this, 1.0);
		this.field_7289 = new class_1379(this, 1.0, 80);
		this.field_6201.method_6277(4, new class_1577.class_1578(this));
		this.field_6201.method_6277(5, lv);
		this.field_6201.method_6277(7, this.field_7289);
		this.field_6201.method_6277(8, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(8, new class_1361(this, class_1577.class, 12.0F, 0.01F));
		this.field_6201.method_6277(9, new class_1376(this));
		this.field_7289.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		lv.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		this.field_6185.method_6277(1, new class_1400(this, class_1309.class, 10, true, false, new class_1577.class_1579(this)));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7363).method_6192(6.0);
		this.method_5996(class_1612.field_7357).method_6192(0.5);
		this.method_5996(class_1612.field_7365).method_6192(16.0);
		this.method_5996(class_1612.field_7359).method_6192(30.0);
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		return new class_1412(this, arg);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7280, false);
		this.field_6011.method_12784(field_7290, 0);
	}

	@Override
	public boolean method_6094() {
		return true;
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6292;
	}

	public boolean method_7058() {
		return this.field_6011.method_12789(field_7280);
	}

	private void method_7054(boolean bl) {
		this.field_6011.method_12778(field_7280, bl);
	}

	public int method_7055() {
		return 80;
	}

	private void method_7060(int i) {
		this.field_6011.method_12778(field_7290, i);
	}

	public boolean method_7063() {
		return this.field_6011.method_12789(field_7290) != 0;
	}

	@Nullable
	public class_1309 method_7052() {
		if (!this.method_7063()) {
			return null;
		} else if (this.field_6002.field_9236) {
			if (this.field_7288 != null) {
				return this.field_7288;
			} else {
				class_1297 lv = this.field_6002.method_8469(this.field_6011.method_12789(field_7290));
				if (lv instanceof class_1309) {
					this.field_7288 = (class_1309)lv;
					return this.field_7288;
				} else {
					return null;
				}
			}
		} else {
			return this.method_5968();
		}
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		super.method_5674(arg);
		if (field_7290.equals(arg)) {
			this.field_7282 = 0;
			this.field_7288 = null;
		}
	}

	@Override
	public int method_5970() {
		return 160;
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_5816() ? class_3417.field_14714 : class_3417.field_14968;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_5816() ? class_3417.field_14679 : class_3417.field_14758;
	}

	@Override
	protected class_3414 method_6002() {
		return this.method_5816() ? class_3417.field_15138 : class_3417.field_15232;
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.5F;
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		return arg2.method_8316(arg).method_15767(class_3486.field_15517) ? 10.0F + arg2.method_8610(arg) - 0.5F : super.method_6144(arg, arg2);
	}

	@Override
	public void method_6007() {
		if (this.method_5805()) {
			if (this.field_6002.field_9236) {
				this.field_7284 = this.field_7286;
				if (!this.method_5799()) {
					this.field_7281 = 2.0F;
					class_243 lv = this.method_18798();
					if (lv.field_1351 > 0.0 && this.field_7283 && !this.method_5701()) {
						this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, this.method_7062(), this.method_5634(), 1.0F, 1.0F, false);
					}

					this.field_7283 = lv.field_1351 < 0.0 && this.field_6002.method_8515(new class_2338(this).method_10074(), this);
				} else if (this.method_7058()) {
					if (this.field_7281 < 0.5F) {
						this.field_7281 = 4.0F;
					} else {
						this.field_7281 = this.field_7281 + (0.5F - this.field_7281) * 0.1F;
					}
				} else {
					this.field_7281 = this.field_7281 + (0.125F - this.field_7281) * 0.2F;
				}

				this.field_7286 = this.field_7286 + this.field_7281;
				this.field_7287 = this.field_7285;
				if (!this.method_5816()) {
					this.field_7285 = this.field_5974.nextFloat();
				} else if (this.method_7058()) {
					this.field_7285 = this.field_7285 + (0.0F - this.field_7285) * 0.25F;
				} else {
					this.field_7285 = this.field_7285 + (1.0F - this.field_7285) * 0.06F;
				}

				if (this.method_7058() && this.method_5799()) {
					class_243 lv = this.method_5828(0.0F);

					for (int i = 0; i < 2; i++) {
						this.field_6002
							.method_8406(
								class_2398.field_11247,
								this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681() - lv.field_1352 * 1.5,
								this.field_6010 + this.field_5974.nextDouble() * (double)this.method_17682() - lv.field_1351 * 1.5,
								this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681() - lv.field_1350 * 1.5,
								0.0,
								0.0,
								0.0
							);
					}
				}

				if (this.method_7063()) {
					if (this.field_7282 < this.method_7055()) {
						this.field_7282++;
					}

					class_1309 lv2 = this.method_7052();
					if (lv2 != null) {
						this.method_5988().method_6226(lv2, 90.0F, 90.0F);
						this.method_5988().method_6231();
						double d = (double)this.method_7061(0.0F);
						double e = lv2.field_5987 - this.field_5987;
						double f = lv2.field_6010 + (double)(lv2.method_17682() * 0.5F) - (this.field_6010 + (double)this.method_5751());
						double g = lv2.field_6035 - this.field_6035;
						double h = Math.sqrt(e * e + f * f + g * g);
						e /= h;
						f /= h;
						g /= h;
						double j = this.field_5974.nextDouble();

						while (j < h) {
							j += 1.8 - d + this.field_5974.nextDouble() * (1.7 - d);
							this.field_6002
								.method_8406(
									class_2398.field_11247, this.field_5987 + e * j, this.field_6010 + f * j + (double)this.method_5751(), this.field_6035 + g * j, 0.0, 0.0, 0.0
								);
						}
					}
				}
			}

			if (this.method_5816()) {
				this.method_5855(300);
			} else if (this.field_5952) {
				this.method_18799(
					this.method_18798()
						.method_1031((double)((this.field_5974.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5, (double)((this.field_5974.nextFloat() * 2.0F - 1.0F) * 0.4F))
				);
				this.field_6031 = this.field_5974.nextFloat() * 360.0F;
				this.field_5952 = false;
				this.field_6007 = true;
			}

			if (this.method_7063()) {
				this.field_6031 = this.field_6241;
			}
		}

		super.method_6007();
	}

	protected class_3414 method_7062() {
		return class_3417.field_14584;
	}

	@Environment(EnvType.CLIENT)
	public float method_7057(float f) {
		return class_3532.method_16439(f, this.field_7284, this.field_7286);
	}

	@Environment(EnvType.CLIENT)
	public float method_7053(float f) {
		return class_3532.method_16439(f, this.field_7287, this.field_7285);
	}

	public float method_7061(float f) {
		return ((float)this.field_7282 + f) / (float)this.method_7055();
	}

	@Override
	protected boolean method_7075() {
		return true;
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		return arg.method_8606(this);
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return (this.field_5974.nextInt(20) == 0 || !arg.method_8626(new class_2338(this))) && super.method_5979(arg, arg2);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (!this.method_7058() && !arg.method_5527() && arg.method_5526() instanceof class_1309) {
			class_1309 lv = (class_1309)arg.method_5526();
			if (!arg.method_5535()) {
				lv.method_5643(class_1282.method_5513(this), 2.0F);
			}
		}

		if (this.field_7289 != null) {
			this.field_7289.method_6304();
		}

		return super.method_5643(arg, f);
	}

	@Override
	public int method_5978() {
		return 180;
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_6034() && this.method_5799()) {
			this.method_5724(0.1F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.9));
			if (!this.method_7058() && this.method_5968() == null) {
				this.method_18799(this.method_18798().method_1031(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(arg);
		}
	}

	static class class_1578 extends class_1352 {
		private final class_1577 field_7293;
		private int field_7291;
		private final boolean field_7292;

		public class_1578(class_1577 arg) {
			this.field_7293 = arg;
			this.field_7292 = arg instanceof class_1550;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			class_1309 lv = this.field_7293.method_5968();
			return lv != null && lv.method_5805();
		}

		@Override
		public boolean method_6266() {
			return super.method_6266() && (this.field_7292 || this.field_7293.method_5858(this.field_7293.method_5968()) > 9.0);
		}

		@Override
		public void method_6269() {
			this.field_7291 = -10;
			this.field_7293.method_5942().method_6340();
			this.field_7293.method_5988().method_6226(this.field_7293.method_5968(), 90.0F, 90.0F);
			this.field_7293.field_6007 = true;
		}

		@Override
		public void method_6270() {
			this.field_7293.method_7060(0);
			this.field_7293.method_5980(null);
			this.field_7293.field_7289.method_6304();
		}

		@Override
		public void method_6268() {
			class_1309 lv = this.field_7293.method_5968();
			this.field_7293.method_5942().method_6340();
			this.field_7293.method_5988().method_6226(lv, 90.0F, 90.0F);
			if (!this.field_7293.method_6057(lv)) {
				this.field_7293.method_5980(null);
			} else {
				this.field_7291++;
				if (this.field_7291 == 0) {
					this.field_7293.method_7060(this.field_7293.method_5968().method_5628());
					this.field_7293.field_6002.method_8421(this.field_7293, (byte)21);
				} else if (this.field_7291 >= this.field_7293.method_7055()) {
					float f = 1.0F;
					if (this.field_7293.field_6002.method_8407() == class_1267.field_5807) {
						f += 2.0F;
					}

					if (this.field_7292) {
						f += 2.0F;
					}

					lv.method_5643(class_1282.method_5536(this.field_7293, this.field_7293), f);
					lv.method_5643(class_1282.method_5511(this.field_7293), (float)this.field_7293.method_5996(class_1612.field_7363).method_6194());
					this.field_7293.method_5980(null);
				}

				super.method_6268();
			}
		}
	}

	static class class_1579 implements Predicate<class_1309> {
		private final class_1577 field_7294;

		public class_1579(class_1577 arg) {
			this.field_7294 = arg;
		}

		public boolean method_7064(@Nullable class_1309 arg) {
			return (arg instanceof class_1657 || arg instanceof class_1477) && arg.method_5858(this.field_7294) > 9.0;
		}
	}

	static class class_1580 extends class_1335 {
		private final class_1577 field_7295;

		public class_1580(class_1577 arg) {
			super(arg);
			this.field_7295 = arg;
		}

		@Override
		public void method_6240() {
			if (this.field_6374 == class_1335.class_1336.field_6378 && !this.field_7295.method_5942().method_6357()) {
				class_243 lv = new class_243(
					this.field_6370 - this.field_7295.field_5987, this.field_6369 - this.field_7295.field_6010, this.field_6367 - this.field_7295.field_6035
				);
				double d = lv.method_1033();
				double e = lv.field_1352 / d;
				double f = lv.field_1351 / d;
				double g = lv.field_1350 / d;
				float h = (float)(class_3532.method_15349(lv.field_1350, lv.field_1352) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_7295.field_6031 = this.method_6238(this.field_7295.field_6031, h, 90.0F);
				this.field_7295.field_6283 = this.field_7295.field_6031;
				float i = (float)(this.field_6372 * this.field_7295.method_5996(class_1612.field_7357).method_6194());
				float j = class_3532.method_16439(0.125F, this.field_7295.method_6029(), i);
				this.field_7295.method_6125(j);
				double k = Math.sin((double)(this.field_7295.field_6012 + this.field_7295.method_5628()) * 0.5) * 0.05;
				double l = Math.cos((double)(this.field_7295.field_6031 * (float) (Math.PI / 180.0)));
				double m = Math.sin((double)(this.field_7295.field_6031 * (float) (Math.PI / 180.0)));
				double n = Math.sin((double)(this.field_7295.field_6012 + this.field_7295.method_5628()) * 0.75) * 0.05;
				this.field_7295.method_18799(this.field_7295.method_18798().method_1031(k * l, n * (m + l) * 0.25 + (double)j * f * 0.1, k * m));
				class_1333 lv2 = this.field_7295.method_5988();
				double o = this.field_7295.field_5987 + e * 2.0;
				double p = (double)this.field_7295.method_5751() + this.field_7295.field_6010 + f / d;
				double q = this.field_7295.field_6035 + g * 2.0;
				double r = lv2.method_6225();
				double s = lv2.method_6227();
				double t = lv2.method_6228();
				if (!lv2.method_6232()) {
					r = o;
					s = p;
					t = q;
				}

				this.field_7295
					.method_5988()
					.method_6230(class_3532.method_16436(0.125, r, o), class_3532.method_16436(0.125, s, p), class_3532.method_16436(0.125, t, q), 10.0F, 40.0F);
				this.field_7295.method_7054(true);
			} else {
				this.field_7295.method_6125(0.0F);
				this.field_7295.method_7054(false);
			}
		}
	}
}
