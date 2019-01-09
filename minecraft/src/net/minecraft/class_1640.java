package net.minecraft;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1640 extends class_3763 implements class_1603 {
	private static final UUID field_7418 = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final class_1322 field_7416 = new class_1322(field_7418, "Drinking speed penalty", -0.25, class_1322.class_1323.field_6328).method_6187(false);
	private static final class_2940<Boolean> field_7419 = class_2945.method_12791(class_1640.class, class_2943.field_13323);
	private int field_7417;
	private class_3909<class_3763> field_17283;
	private class_3760<class_1657> field_17284;

	public class_1640(class_1937 arg) {
		super(class_1299.field_6145, arg);
		this.method_5835(0.6F, 1.95F);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_17283 = new class_3909<>(this, class_3763.class, true, arg -> arg != null && this.method_16482());
		this.field_17284 = new class_3760<>(this, class_1657.class, 10, true, false, null);
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(2, new class_1381(this, 1.0, 60, 10.0F));
		this.field_6201.method_6277(2, new class_1394(this, 1.0));
		this.field_6201.method_6277(3, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(3, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this, class_3763.class));
		this.field_6185.method_6277(2, this.field_17283);
		this.field_6185.method_6277(3, this.field_17284);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.method_5841().method_12784(field_7419, false);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14736;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14645;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14820;
	}

	public void method_7192(boolean bl) {
		this.method_5841().method_12778(field_7419, bl);
	}

	public boolean method_7193() {
		return this.method_5841().method_12789(field_7419);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(26.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
	}

	@Override
	public void method_6007() {
		if (!this.field_6002.field_9236) {
			this.field_17283.method_17353();
			if (this.field_17283.method_17352() <= 0) {
				this.field_17284.method_17351(true);
			} else {
				this.field_17284.method_17351(false);
			}

			if (this.method_7193()) {
				if (this.field_7417-- <= 0) {
					this.method_7192(false);
					class_1799 lv = this.method_6047();
					this.method_5673(class_1304.field_6173, class_1799.field_8037);
					if (lv.method_7909() == class_1802.field_8574) {
						List<class_1293> list = class_1844.method_8067(lv);
						if (list != null) {
							for (class_1293 lv2 : list) {
								this.method_6092(new class_1293(lv2));
							}
						}
					}

					this.method_5996(class_1612.field_7357).method_6202(field_7416);
				}
			} else {
				class_1842 lv3 = null;
				if (this.field_5974.nextFloat() < 0.15F && this.method_5777(class_3486.field_15517) && !this.method_6059(class_1294.field_5923)) {
					lv3 = class_1847.field_8994;
				} else if (this.field_5974.nextFloat() < 0.15F
					&& (this.method_5809() || this.method_6081() != null && this.method_6081().method_5534())
					&& !this.method_6059(class_1294.field_5918)) {
					lv3 = class_1847.field_8987;
				} else if (this.field_5974.nextFloat() < 0.05F && this.method_6032() < this.method_6063()) {
					lv3 = class_1847.field_8963;
				} else if (this.field_5974.nextFloat() < 0.5F
					&& this.method_5968() != null
					&& !this.method_6059(class_1294.field_5904)
					&& this.method_5968().method_5858(this) > 121.0) {
					lv3 = class_1847.field_9005;
				}

				if (lv3 != null) {
					this.method_5673(class_1304.field_6173, class_1844.method_8061(new class_1799(class_1802.field_8574), lv3));
					this.field_7417 = this.method_6047().method_7935();
					this.method_7192(true);
					this.field_6002
						.method_8465(
							null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14565, this.method_5634(), 1.0F, 0.8F + this.field_5974.nextFloat() * 0.4F
						);
					class_1324 lv4 = this.method_5996(class_1612.field_7357);
					lv4.method_6202(field_7416);
					lv4.method_6197(field_7416);
				}
			}

			if (this.field_5974.nextFloat() < 7.5E-4F) {
				this.field_6002.method_8421(this, (byte)15);
			}
		}

		super.method_6007();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 15) {
			for (int i = 0; i < this.field_5974.nextInt(35) + 10; i++) {
				this.field_6002
					.method_8406(
						class_2398.field_11249,
						this.field_5987 + this.field_5974.nextGaussian() * 0.13F,
						this.method_5829().field_1325 + 0.5 + this.field_5974.nextGaussian() * 0.13F,
						this.field_6035 + this.field_5974.nextGaussian() * 0.13F,
						0.0,
						0.0,
						0.0
					);
			}
		} else {
			super.method_5711(b);
		}
	}

	@Override
	protected float method_6036(class_1282 arg, float f) {
		f = super.method_6036(arg, f);
		if (arg.method_5529() == this) {
			f = 0.0F;
		}

		if (arg.method_5527()) {
			f = (float)((double)f * 0.15);
		}

		return f;
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		if (!this.method_7193()) {
			double d = arg.field_6010 + (double)arg.method_5751() - 1.1F;
			double e = arg.field_5987 + arg.field_5967 - this.field_5987;
			double g = d - this.field_6010;
			double h = arg.field_6035 + arg.field_6006 - this.field_6035;
			float i = class_3532.method_15368(e * e + h * h);
			class_1842 lv = class_1847.field_9004;
			if (arg instanceof class_3763) {
				if (arg.method_6032() <= 4.0F) {
					lv = class_1847.field_8963;
				} else {
					lv = class_1847.field_8986;
				}

				this.method_5980(null);
			} else if (i >= 8.0F && !arg.method_6059(class_1294.field_5909)) {
				lv = class_1847.field_8996;
			} else if (arg.method_6032() >= 8.0F && !arg.method_6059(class_1294.field_5899)) {
				lv = class_1847.field_8982;
			} else if (i <= 3.0F && !arg.method_6059(class_1294.field_5911) && this.field_5974.nextFloat() < 0.25F) {
				lv = class_1847.field_8975;
			}

			class_1686 lv2 = new class_1686(this.field_6002, this);
			lv2.method_7494(class_1844.method_8061(new class_1799(class_1802.field_8436), lv));
			lv2.field_5965 -= -20.0F;
			lv2.method_7485(e, g + (double)(i * 0.2F), h, 0.75F, 8.0F);
			this.field_6002
				.method_8465(
					null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_15067, this.method_5634(), 1.0F, 0.8F + this.field_5974.nextFloat() * 0.4F
				);
			this.field_6002.method_8649(lv2);
		}
	}

	@Override
	public float method_5751() {
		return 1.62F;
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
	public void method_16484(int i, boolean bl) {
	}

	@Override
	public boolean method_16485() {
		return false;
	}
}
