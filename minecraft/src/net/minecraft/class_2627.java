package net.minecraft;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2627 extends class_2621 implements class_1278, class_3000 {
	private static final int[] field_12059 = IntStream.range(0, 27).toArray();
	private class_2371<class_1799> field_12054 = class_2371.method_10213(27, class_1799.field_8037);
	private int field_12053;
	private class_2627.class_2628 field_12057 = class_2627.class_2628.field_12065;
	private float field_12056;
	private float field_12055;
	private class_1767 field_12060;
	private boolean field_12058;

	public class_2627(@Nullable class_1767 arg) {
		super(class_2591.field_11896);
		this.field_12060 = arg;
	}

	public class_2627() {
		this(null);
		this.field_12058 = true;
	}

	@Override
	public void method_16896() {
		this.method_11318();
		if (this.field_12057 == class_2627.class_2628.field_12066 || this.field_12057 == class_2627.class_2628.field_12064) {
			this.method_11316();
		}
	}

	protected void method_11318() {
		this.field_12055 = this.field_12056;
		switch (this.field_12057) {
			case field_12065:
				this.field_12056 = 0.0F;
				break;
			case field_12066:
				this.field_12056 += 0.1F;
				if (this.field_12056 >= 1.0F) {
					this.method_11316();
					this.field_12057 = class_2627.class_2628.field_12063;
					this.field_12056 = 1.0F;
				}
				break;
			case field_12064:
				this.field_12056 -= 0.1F;
				if (this.field_12056 <= 0.0F) {
					this.field_12057 = class_2627.class_2628.field_12065;
					this.field_12056 = 0.0F;
				}
				break;
			case field_12063:
				this.field_12056 = 1.0F;
		}
	}

	public class_2627.class_2628 method_11313() {
		return this.field_12057;
	}

	public class_238 method_11314(class_2680 arg) {
		return this.method_11311(arg.method_11654(class_2480.field_11496));
	}

	public class_238 method_11311(class_2350 arg) {
		return class_259.method_1077()
			.method_1107()
			.method_1012(
				(double)(0.5F * this.method_11312(1.0F) * (float)arg.method_10148()),
				(double)(0.5F * this.method_11312(1.0F) * (float)arg.method_10164()),
				(double)(0.5F * this.method_11312(1.0F) * (float)arg.method_10165())
			);
	}

	private class_238 method_11315(class_2350 arg) {
		class_2350 lv = arg.method_10153();
		return this.method_11311(arg).method_1002((double)lv.method_10148(), (double)lv.method_10164(), (double)lv.method_10165());
	}

	private void method_11316() {
		class_2680 lv = this.field_11863.method_8320(this.method_11016());
		if (lv.method_11614() instanceof class_2480) {
			class_2350 lv2 = lv.method_11654(class_2480.field_11496);
			class_238 lv3 = this.method_11315(lv2).method_996(this.field_11867);
			List<class_1297> list = this.field_11863.method_8335(null, lv3);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					class_1297 lv4 = (class_1297)list.get(i);
					if (lv4.method_5657() != class_3619.field_15975) {
						double d = 0.0;
						double e = 0.0;
						double f = 0.0;
						class_238 lv5 = lv4.method_5829();
						switch (lv2.method_10166()) {
							case field_11048:
								if (lv2.method_10171() == class_2350.class_2352.field_11056) {
									d = lv3.field_1320 - lv5.field_1323;
								} else {
									d = lv5.field_1320 - lv3.field_1323;
								}

								d += 0.01;
								break;
							case field_11052:
								if (lv2.method_10171() == class_2350.class_2352.field_11056) {
									e = lv3.field_1325 - lv5.field_1322;
								} else {
									e = lv5.field_1325 - lv3.field_1322;
								}

								e += 0.01;
								break;
							case field_11051:
								if (lv2.method_10171() == class_2350.class_2352.field_11056) {
									f = lv3.field_1324 - lv5.field_1321;
								} else {
									f = lv5.field_1324 - lv3.field_1321;
								}

								f += 0.01;
						}

						lv4.method_5784(class_1313.field_6306, d * (double)lv2.method_10148(), e * (double)lv2.method_10164(), f * (double)lv2.method_10165());
					}
				}
			}
		}
	}

	@Override
	public int method_5439() {
		return this.field_12054.size();
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.field_12053 = j;
			if (j == 0) {
				this.field_12057 = class_2627.class_2628.field_12064;
			}

			if (j == 1) {
				this.field_12057 = class_2627.class_2628.field_12066;
			}

			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public void method_5435(class_1657 arg) {
		if (!arg.method_7325()) {
			if (this.field_12053 < 0) {
				this.field_12053 = 0;
			}

			this.field_12053++;
			this.field_11863.method_8427(this.field_11867, this.method_11010().method_11614(), 1, this.field_12053);
			if (this.field_12053 == 1) {
				this.field_11863
					.method_8396(null, this.field_11867, class_3417.field_14825, class_3419.field_15245, 0.5F, this.field_11863.field_9229.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void method_5432(class_1657 arg) {
		if (!arg.method_7325()) {
			this.field_12053--;
			this.field_11863.method_8427(this.field_11867, this.method_11010().method_11614(), 1, this.field_12053);
			if (this.field_12053 <= 0) {
				this.field_11863
					.method_8396(null, this.field_11867, class_3417.field_14751, class_3419.field_15245, 0.5F, this.field_11863.field_9229.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	protected class_2561 method_5477() {
		return new class_2588("container.shulkerBox");
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.method_11319(arg);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		return this.method_11317(arg);
	}

	public void method_11319(class_2487 arg) {
		this.field_12054 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		if (!this.method_11283(arg) && arg.method_10573("Items", 9)) {
			class_1262.method_5429(arg, this.field_12054);
		}
	}

	public class_2487 method_11317(class_2487 arg) {
		if (!this.method_11286(arg)) {
			class_1262.method_5427(arg, this.field_12054, false);
		}

		return arg;
	}

	@Override
	protected class_2371<class_1799> method_11282() {
		return this.field_12054;
	}

	@Override
	protected void method_11281(class_2371<class_1799> arg) {
		this.field_12054 = arg;
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_12054) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int[] method_5494(class_2350 arg) {
		return field_12059;
	}

	@Override
	public boolean method_5492(int i, class_1799 arg, @Nullable class_2350 arg2) {
		return !(class_2248.method_9503(arg.method_7909()) instanceof class_2480);
	}

	@Override
	public boolean method_5493(int i, class_1799 arg, class_2350 arg2) {
		return true;
	}

	public float method_11312(float f) {
		return class_3532.method_16439(f, this.field_12055, this.field_12056);
	}

	@Environment(EnvType.CLIENT)
	public class_1767 method_11320() {
		if (this.field_12058) {
			this.field_12060 = class_2480.method_10526(this.method_11010().method_11614());
			this.field_12058 = false;
		}

		return this.field_12060;
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_1733(i, arg, this);
	}

	public static enum class_2628 {
		field_12065,
		field_12066,
		field_12063,
		field_12064;
	}
}
