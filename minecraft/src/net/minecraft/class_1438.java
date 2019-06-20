package net.minecraft;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.tuple.Pair;

public class class_1438 extends class_1430 {
	private static final class_2940<String> field_18105 = class_2945.method_12791(class_1438.class, class_2943.field_13326);
	private class_1291 field_18106;
	private int field_18107;
	private UUID field_18108;

	public class_1438(class_1299<? extends class_1438> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		return arg2.method_8320(arg.method_10074()).method_11614() == class_2246.field_10402 ? 10.0F : arg2.method_8610(arg) - 0.5F;
	}

	public static boolean method_20665(class_1299<class_1438> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		return arg2.method_8320(arg4.method_10074()).method_11614() == class_2246.field_10402 && arg2.method_8624(arg4, 0) > 8;
	}

	@Override
	public void method_5800(class_1538 arg) {
		UUID uUID = arg.method_5667();
		if (!uUID.equals(this.field_18108)) {
			this.method_18433(this.method_18435() == class_1438.class_4053.field_18109 ? class_1438.class_4053.field_18110 : class_1438.class_4053.field_18109);
			this.field_18108 = uUID;
			this.method_5783(class_3417.field_18266, 2.0F, 1.0F);
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_18105, class_1438.class_4053.field_18109.field_18111);
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8428 && this.method_5618() >= 0 && !arg.field_7503.field_7477) {
			lv.method_7934(1);
			boolean bl = false;
			class_1799 lv2;
			if (this.field_18106 != null) {
				bl = true;
				lv2 = new class_1799(class_1802.field_8766);
				class_1830.method_8021(lv2, this.field_18106, this.field_18107);
				this.field_18106 = null;
				this.field_18107 = 0;
			} else {
				lv2 = new class_1799(class_1802.field_8208);
			}

			if (lv.method_7960()) {
				arg.method_6122(arg2, lv2);
			} else if (!arg.field_7514.method_7394(lv2)) {
				arg.method_7328(lv2, false);
			}

			class_3414 lv3;
			if (bl) {
				lv3 = class_3417.field_18269;
			} else {
				lv3 = class_3417.field_18268;
			}

			this.method_5783(lv3, 1.0F, 1.0F);
			return true;
		} else if (lv.method_7909() == class_1802.field_8868 && this.method_5618() >= 0) {
			this.field_6002.method_8406(class_2398.field_11236, this.field_5987, this.field_6010 + (double)(this.method_17682() / 2.0F), this.field_6035, 0.0, 0.0, 0.0);
			if (!this.field_6002.field_9236) {
				this.method_5650();
				class_1430 lv4 = class_1299.field_6085.method_5883(this.field_6002);
				lv4.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
				lv4.method_6033(this.method_6032());
				lv4.field_6283 = this.field_6283;
				if (this.method_16914()) {
					lv4.method_5665(this.method_5797());
				}

				this.field_6002.method_8649(lv4);

				for (int i = 0; i < 5; i++) {
					this.field_6002
						.method_8649(
							new class_1542(
								this.field_6002,
								this.field_5987,
								this.field_6010 + (double)this.method_17682(),
								this.field_6035,
								new class_1799(this.method_18435().field_18112.method_11614())
							)
						);
				}

				lv.method_7956(1, arg, arg2x -> arg2x.method_20236(arg2));
				this.method_5783(class_3417.field_14705, 1.0F, 1.0F);
			}

			return true;
		} else {
			if (this.method_18435() == class_1438.class_4053.field_18110 && lv.method_7909().method_7855(class_3489.field_15543)) {
				if (this.field_18106 != null) {
					for (int j = 0; j < 2; j++) {
						this.field_6002
							.method_8406(
								class_2398.field_11251,
								this.field_5987 + (double)(this.field_5974.nextFloat() / 2.0F),
								this.field_6010 + (double)(this.method_17682() / 2.0F),
								this.field_6035 + (double)(this.field_5974.nextFloat() / 2.0F),
								0.0,
								(double)(this.field_5974.nextFloat() / 5.0F),
								0.0
							);
					}
				} else {
					Pair<class_1291, Integer> pair = this.method_18436(lv);
					if (!arg.field_7503.field_7477) {
						lv.method_7934(1);
					}

					for (int i = 0; i < 4; i++) {
						this.field_6002
							.method_8406(
								class_2398.field_11245,
								this.field_5987 + (double)(this.field_5974.nextFloat() / 2.0F),
								this.field_6010 + (double)(this.method_17682() / 2.0F),
								this.field_6035 + (double)(this.field_5974.nextFloat() / 2.0F),
								0.0,
								(double)(this.field_5974.nextFloat() / 5.0F),
								0.0
							);
					}

					this.field_18106 = pair.getLeft();
					this.field_18107 = pair.getRight();
					this.method_5783(class_3417.field_18267, 2.0F, 1.0F);
				}
			}

			return super.method_5992(arg, arg2);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10582("Type", this.method_18435().field_18111);
		if (this.field_18106 != null) {
			arg.method_10567("EffectId", (byte)class_1291.method_5554(this.field_18106));
			arg.method_10569("EffectDuration", this.field_18107);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_18433(class_1438.class_4053.method_18441(arg.method_10558("Type")));
		if (arg.method_10573("EffectId", 1)) {
			this.field_18106 = class_1291.method_5569(arg.method_10571("EffectId"));
		}

		if (arg.method_10573("EffectDuration", 3)) {
			this.field_18107 = arg.method_10550("EffectDuration");
		}
	}

	private Pair<class_1291, Integer> method_18436(class_1799 arg) {
		class_2356 lv = (class_2356)((class_1747)arg.method_7909()).method_7711();
		return Pair.of(lv.method_10188(), lv.method_10187());
	}

	private void method_18433(class_1438.class_4053 arg) {
		this.field_6011.method_12778(field_18105, arg.field_18111);
	}

	public class_1438.class_4053 method_18435() {
		return class_1438.class_4053.method_18441(this.field_6011.method_12789(field_18105));
	}

	public class_1438 method_6495(class_1296 arg) {
		class_1438 lv = class_1299.field_6143.method_5883(this.field_6002);
		lv.method_18433(this.method_18434((class_1438)arg));
		return lv;
	}

	private class_1438.class_4053 method_18434(class_1438 arg) {
		class_1438.class_4053 lv = this.method_18435();
		class_1438.class_4053 lv2 = arg.method_18435();
		class_1438.class_4053 lv3;
		if (lv == lv2 && this.field_5974.nextInt(1024) == 0) {
			lv3 = lv == class_1438.class_4053.field_18110 ? class_1438.class_4053.field_18109 : class_1438.class_4053.field_18110;
		} else {
			lv3 = this.field_5974.nextBoolean() ? lv : lv2;
		}

		return lv3;
	}

	public static enum class_4053 {
		field_18109("red", class_2246.field_10559.method_9564()),
		field_18110("brown", class_2246.field_10251.method_9564());

		private final String field_18111;
		private final class_2680 field_18112;

		private class_4053(String string2, class_2680 arg) {
			this.field_18111 = string2;
			this.field_18112 = arg;
		}

		@Environment(EnvType.CLIENT)
		public class_2680 method_18437() {
			return this.field_18112;
		}

		private static class_1438.class_4053 method_18441(String string) {
			for (class_1438.class_4053 lv : values()) {
				if (lv.field_18111.equals(string)) {
					return lv;
				}
			}

			return field_18109;
		}
	}
}
