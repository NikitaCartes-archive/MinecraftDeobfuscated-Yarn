package net.minecraft;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1545 extends class_1588 {
	private float field_7214 = 0.5F;
	private int field_7215;
	private static final class_2940<Byte> field_7216 = class_2945.method_12791(class_1545.class, class_2943.field_13319);

	public class_1545(class_1299<? extends class_1545> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5941(class_7.field_18, -1.0F);
		this.method_5941(class_7.field_14, 8.0F);
		this.method_5941(class_7.field_9, 0.0F);
		this.method_5941(class_7.field_3, 0.0F);
		this.field_6194 = 10;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(4, new class_1545.class_1546(this));
		this.field_6201.method_6277(5, new class_1370(this, 1.0));
		this.field_6201.method_6277(7, new class_1394(this, 1.0, 0.0F));
		this.field_6201.method_6277(8, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7363).method_6192(6.0);
		this.method_5996(class_1612.field_7357).method_6192(0.23F);
		this.method_5996(class_1612.field_7365).method_6192(48.0);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7216, (byte)0);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14991;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14842;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14580;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		return 15728880;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Override
	public void method_6007() {
		if (!this.field_5952 && this.method_18798().field_1351 < 0.0) {
			this.method_18799(this.method_18798().method_18805(1.0, 0.6, 1.0));
		}

		if (this.field_6002.field_9236) {
			if (this.field_5974.nextInt(24) == 0 && !this.method_5701()) {
				this.field_6002
					.method_8486(
						this.field_5987 + 0.5,
						this.field_6010 + 0.5,
						this.field_6035 + 0.5,
						class_3417.field_14734,
						this.method_5634(),
						1.0F + this.field_5974.nextFloat(),
						this.field_5974.nextFloat() * 0.7F + 0.3F,
						false
					);
			}

			for (int i = 0; i < 2; i++) {
				this.field_6002
					.method_8406(
						class_2398.field_11237,
						this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681(),
						this.field_6010 + this.field_5974.nextDouble() * (double)this.method_17682(),
						this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681(),
						0.0,
						0.0,
						0.0
					);
			}
		}

		super.method_6007();
	}

	@Override
	protected void method_5958() {
		if (this.method_5637()) {
			this.method_5643(class_1282.field_5859, 1.0F);
		}

		this.field_7215--;
		if (this.field_7215 <= 0) {
			this.field_7215 = 100;
			this.field_7214 = 0.5F + (float)this.field_5974.nextGaussian() * 3.0F;
		}

		class_1309 lv = this.method_5968();
		if (lv != null && lv.field_6010 + (double)lv.method_5751() > this.field_6010 + (double)this.method_5751() + (double)this.field_7214 && this.method_18395(lv)) {
			class_243 lv2 = this.method_18798();
			this.method_18799(this.method_18798().method_1031(0.0, (0.3F - lv2.field_1351) * 0.3F, 0.0));
			this.field_6007 = true;
		}

		super.method_5958();
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	public boolean method_5809() {
		return this.method_6994();
	}

	private boolean method_6994() {
		return (this.field_6011.method_12789(field_7216) & 1) != 0;
	}

	private void method_6993(boolean bl) {
		byte b = this.field_6011.method_12789(field_7216);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.field_6011.method_12778(field_7216, b);
	}

	static class class_1546 extends class_1352 {
		private final class_1545 field_7219;
		private int field_7218;
		private int field_7217;
		private int field_19420;

		public class_1546(class_1545 arg) {
			this.field_7219 = arg;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			class_1309 lv = this.field_7219.method_5968();
			return lv != null && lv.method_5805() && this.field_7219.method_18395(lv);
		}

		@Override
		public void method_6269() {
			this.field_7218 = 0;
		}

		@Override
		public void method_6270() {
			this.field_7219.method_6993(false);
			this.field_19420 = 0;
		}

		@Override
		public void method_6268() {
			this.field_7217--;
			class_1309 lv = this.field_7219.method_5968();
			if (lv != null) {
				boolean bl = this.field_7219.method_5985().method_6369(lv);
				if (bl) {
					this.field_19420 = 0;
				} else {
					this.field_19420++;
				}

				double d = this.field_7219.method_5858(lv);
				if (d < 4.0) {
					if (!bl) {
						return;
					}

					if (this.field_7217 <= 0) {
						this.field_7217 = 20;
						this.field_7219.method_6121(lv);
					}

					this.field_7219.method_5962().method_6239(lv.field_5987, lv.field_6010, lv.field_6035, 1.0);
				} else if (d < this.method_6995() * this.method_6995() && bl) {
					double e = lv.field_5987 - this.field_7219.field_5987;
					double f = lv.method_5829().field_1322
						+ (double)(lv.method_17682() / 2.0F)
						- (this.field_7219.field_6010 + (double)(this.field_7219.method_17682() / 2.0F));
					double g = lv.field_6035 - this.field_7219.field_6035;
					if (this.field_7217 <= 0) {
						this.field_7218++;
						if (this.field_7218 == 1) {
							this.field_7217 = 60;
							this.field_7219.method_6993(true);
						} else if (this.field_7218 <= 4) {
							this.field_7217 = 6;
						} else {
							this.field_7217 = 100;
							this.field_7218 = 0;
							this.field_7219.method_6993(false);
						}

						if (this.field_7218 > 1) {
							float h = class_3532.method_15355(class_3532.method_15368(d)) * 0.5F;
							this.field_7219.field_6002.method_8444(null, 1018, new class_2338(this.field_7219), 0);

							for (int i = 0; i < 1; i++) {
								class_1677 lv2 = new class_1677(
									this.field_7219.field_6002,
									this.field_7219,
									e + this.field_7219.method_6051().nextGaussian() * (double)h,
									f,
									g + this.field_7219.method_6051().nextGaussian() * (double)h
								);
								lv2.field_6010 = this.field_7219.field_6010 + (double)(this.field_7219.method_17682() / 2.0F) + 0.5;
								this.field_7219.field_6002.method_8649(lv2);
							}
						}
					}

					this.field_7219.method_5988().method_6226(lv, 10.0F, 10.0F);
				} else if (this.field_19420 < 5) {
					this.field_7219.method_5962().method_6239(lv.field_5987, lv.field_6010, lv.field_6035, 1.0);
				}

				super.method_6268();
			}
		}

		private double method_6995() {
			return this.field_7219.method_5996(class_1612.field_7365).method_6194();
		}
	}
}
