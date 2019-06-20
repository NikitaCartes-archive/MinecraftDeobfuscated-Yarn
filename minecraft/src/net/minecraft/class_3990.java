package net.minecraft;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3990 {
	private final Random field_17726 = new Random();
	private final class_3218 field_17727;
	private int field_17728;
	private int field_17729;
	private int field_17730;

	public class_3990(class_3218 arg) {
		this.field_17727 = arg;
		this.field_17728 = 1200;
		class_31 lv = arg.method_8401();
		this.field_17729 = lv.method_18038();
		this.field_17730 = lv.method_18039();
		if (this.field_17729 == 0 && this.field_17730 == 0) {
			this.field_17729 = 24000;
			lv.method_18041(this.field_17729);
			this.field_17730 = 25;
			lv.method_18042(this.field_17730);
		}
	}

	public void method_18015() {
		if (--this.field_17728 <= 0) {
			this.field_17728 = 1200;
			class_31 lv = this.field_17727.method_8401();
			this.field_17729 -= 1200;
			lv.method_18041(this.field_17729);
			if (this.field_17729 <= 0) {
				this.field_17729 = 24000;
				if (this.field_17727.method_8450().method_8355(class_1928.field_19390)) {
					int i = this.field_17730;
					this.field_17730 = class_3532.method_15340(this.field_17730 + 25, 25, 75);
					lv.method_18042(this.field_17730);
					if (this.field_17726.nextInt(100) <= i) {
						if (this.method_18018()) {
							this.field_17730 = 25;
						}
					}
				}
			}
		}
	}

	private boolean method_18018() {
		class_1657 lv = this.field_17727.method_18779();
		if (lv == null) {
			return true;
		} else if (this.field_17726.nextInt(10) != 0) {
			return false;
		} else {
			class_2338 lv2 = lv.method_5704();
			int i = 48;
			class_4153 lv3 = this.field_17727.method_19494();
			Optional<class_2338> optional = lv3.method_19127(class_4158.field_18518.method_19164(), arg -> true, lv2, 48, class_4153.class_4155.field_18489);
			class_2338 lv4 = (class_2338)optional.orElse(lv2);
			class_2338 lv5 = this.method_18017(lv4, 48);
			if (lv5 != null) {
				if (this.field_17727.method_8310(lv5) == class_1972.field_9473) {
					return false;
				}

				class_3989 lv6 = class_1299.field_17713.method_5899(this.field_17727, null, null, null, lv5, class_3730.field_16467, false, false);
				if (lv6 != null) {
					for (int j = 0; j < 2; j++) {
						this.method_18016(lv6, 4);
					}

					this.field_17727.method_8401().method_18040(lv6.method_5667());
					lv6.method_18013(48000);
					lv6.method_18069(lv4);
					lv6.method_18408(lv4, 16);
					return true;
				}
			}

			return false;
		}
	}

	private void method_18016(class_3989 arg, int i) {
		class_2338 lv = this.method_18017(new class_2338(arg), i);
		if (lv != null) {
			class_3986 lv2 = class_1299.field_17714.method_5899(this.field_17727, null, null, null, lv, class_3730.field_16467, false, false);
			if (lv2 != null) {
				lv2.method_5954(arg, true);
			}
		}
	}

	@Nullable
	private class_2338 method_18017(class_2338 arg, int i) {
		class_2338 lv = null;

		for (int j = 0; j < 10; j++) {
			int k = arg.method_10263() + this.field_17726.nextInt(i * 2) - i;
			int l = arg.method_10260() + this.field_17726.nextInt(i * 2) - i;
			int m = this.field_17727.method_8589(class_2902.class_2903.field_13202, k, l);
			class_2338 lv2 = new class_2338(k, m, l);
			if (class_1948.method_8660(class_1317.class_1319.field_6317, this.field_17727, lv2, class_1299.field_17713)) {
				lv = lv2;
				break;
			}
		}

		return lv;
	}
}
