package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1667 extends class_1665 {
	private static final class_2940<Integer> field_7595 = class_2945.method_12791(class_1667.class, class_2943.field_13327);
	private class_1842 field_7597 = class_1847.field_8984;
	private final Set<class_1293> field_7598 = Sets.<class_1293>newHashSet();
	private boolean field_7596;

	public class_1667(class_1937 arg) {
		super(class_1299.field_6122, arg);
	}

	public class_1667(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6122, d, e, f, arg);
	}

	public class_1667(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6122, arg2, arg);
	}

	public void method_7459(class_1799 arg) {
		if (arg.method_7909() == class_1802.field_8087) {
			this.field_7597 = class_1844.method_8063(arg);
			Collection<class_1293> collection = class_1844.method_8068(arg);
			if (!collection.isEmpty()) {
				for (class_1293 lv : collection) {
					this.field_7598.add(new class_1293(lv));
				}
			}

			int i = method_7464(arg);
			if (i == -1) {
				this.method_7462();
			} else {
				this.method_7465(i);
			}
		} else if (arg.method_7909() == class_1802.field_8107) {
			this.field_7597 = class_1847.field_8984;
			this.field_7598.clear();
			this.field_6011.method_12778(field_7595, -1);
		}
	}

	public static int method_7464(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		return lv != null && lv.method_10573("CustomPotionColor", 99) ? lv.method_10550("CustomPotionColor") : -1;
	}

	private void method_7462() {
		this.field_7596 = false;
		this.field_6011.method_12778(field_7595, class_1844.method_8055(class_1844.method_8059(this.field_7597, this.field_7598)));
	}

	public void method_7463(class_1293 arg) {
		this.field_7598.add(arg);
		this.method_5841().method_12778(field_7595, class_1844.method_8055(class_1844.method_8059(this.field_7597, this.field_7598)));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7595, -1);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236) {
			if (this.field_7588) {
				if (this.field_7576 % 5 == 0) {
					this.method_7461(1);
				}
			} else {
				this.method_7461(2);
			}
		} else if (this.field_7588 && this.field_7576 != 0 && !this.field_7598.isEmpty() && this.field_7576 >= 600) {
			this.field_6002.method_8421(this, (byte)0);
			this.field_7597 = class_1847.field_8984;
			this.field_7598.clear();
			this.field_6011.method_12778(field_7595, -1);
		}
	}

	private void method_7461(int i) {
		int j = this.method_7460();
		if (j != -1 && i > 0) {
			double d = (double)(j >> 16 & 0xFF) / 255.0;
			double e = (double)(j >> 8 & 0xFF) / 255.0;
			double f = (double)(j >> 0 & 0xFF) / 255.0;

			for (int k = 0; k < i; k++) {
				this.field_6002
					.method_8406(
						class_2398.field_11226,
						this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
						this.field_6010 + this.field_5974.nextDouble() * (double)this.field_6019,
						this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
						d,
						e,
						f
					);
			}
		}
	}

	public int method_7460() {
		return this.field_6011.method_12789(field_7595);
	}

	private void method_7465(int i) {
		this.field_7596 = true;
		this.field_6011.method_12778(field_7595, i);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.field_7597 != class_1847.field_8984 && this.field_7597 != null) {
			arg.method_10582("Potion", class_2378.field_11143.method_10221(this.field_7597).toString());
		}

		if (this.field_7596) {
			arg.method_10569("Color", this.method_7460());
		}

		if (!this.field_7598.isEmpty()) {
			class_2499 lv = new class_2499();

			for (class_1293 lv2 : this.field_7598) {
				lv.method_10606(lv2.method_5582(new class_2487()));
			}

			arg.method_10566("CustomPotionEffects", lv);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("Potion", 8)) {
			this.field_7597 = class_1844.method_8057(arg);
		}

		for (class_1293 lv : class_1844.method_8060(arg)) {
			this.method_7463(lv);
		}

		if (arg.method_10573("Color", 99)) {
			this.method_7465(arg.method_10550("Color"));
		} else {
			this.method_7462();
		}
	}

	@Override
	protected void method_7450(class_1309 arg) {
		super.method_7450(arg);

		for (class_1293 lv : this.field_7597.method_8049()) {
			arg.method_6092(new class_1293(lv.method_5579(), Math.max(lv.method_5584() / 8, 1), lv.method_5578(), lv.method_5591(), lv.method_5581()));
		}

		if (!this.field_7598.isEmpty()) {
			for (class_1293 lv : this.field_7598) {
				arg.method_6092(lv);
			}
		}
	}

	@Override
	protected class_1799 method_7445() {
		if (this.field_7598.isEmpty() && this.field_7597 == class_1847.field_8984) {
			return new class_1799(class_1802.field_8107);
		} else {
			class_1799 lv = new class_1799(class_1802.field_8087);
			class_1844.method_8061(lv, this.field_7597);
			class_1844.method_8056(lv, this.field_7598);
			if (this.field_7596) {
				lv.method_7948().method_10569("CustomPotionColor", this.method_7460());
			}

			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 0) {
			int i = this.method_7460();
			if (i != -1) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;

				for (int j = 0; j < 20; j++) {
					this.field_6002
						.method_8406(
							class_2398.field_11226,
							this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
							this.field_6010 + this.field_5974.nextDouble() * (double)this.field_6019,
							this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
							d,
							e,
							f
						);
				}
			}
		} else {
			super.method_5711(b);
		}
	}
}
