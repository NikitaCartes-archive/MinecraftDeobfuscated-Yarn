package net.minecraft;

import javax.annotation.Nullable;

public class class_1419 {
	private final class_3218 field_6727;
	private boolean field_6725;
	private class_1419.class_4152 field_18479 = class_1419.class_4152.field_18482;
	private int field_6723;
	private int field_6722;
	private int field_6721;
	private int field_6720;
	private int field_6719;

	public class_1419(class_3218 arg) {
		this.field_6727 = arg;
	}

	public void method_6445() {
		if (this.field_6727.method_8530()) {
			this.field_18479 = class_1419.class_4152.field_18482;
			this.field_6725 = false;
		} else {
			float f = this.field_6727.method_8400(0.0F);
			if ((double)f == 0.5) {
				this.field_18479 = this.field_6727.field_9229.nextInt(10) == 0 ? class_1419.class_4152.field_18481 : class_1419.class_4152.field_18482;
			}

			if (this.field_18479 != class_1419.class_4152.field_18482) {
				if (!this.field_6725) {
					if (!this.method_6446()) {
						return;
					}

					this.field_6725 = true;
				}

				if (this.field_6722 > 0) {
					this.field_6722--;
				} else {
					this.field_6722 = 2;
					if (this.field_6723 > 0) {
						this.method_6447();
						this.field_6723--;
					} else {
						this.field_18479 = class_1419.class_4152.field_18482;
					}
				}
			}
		}
	}

	private boolean method_6446() {
		for (class_1657 lv : this.field_6727.method_18456()) {
			if (!lv.method_7325()) {
				class_2338 lv2 = new class_2338(lv);
				if (this.field_6727.method_19500(lv2)) {
					for (int i = 0; i < 10; i++) {
						float f = this.field_6727.field_9229.nextFloat() * (float) (Math.PI * 2);
						this.field_6721 = lv2.method_10263() + (int)(class_3532.method_15362(f) * 32.0F);
						this.field_6720 = lv2.method_10264();
						this.field_6719 = lv2.method_10260() + (int)(class_3532.method_15374(f) * 32.0F);
					}

					class_243 lv3 = this.method_6448(new class_2338(this.field_6721, this.field_6720, this.field_6719));
					if (lv3 != null) {
						this.field_6722 = 0;
						this.field_6723 = 20;
						return true;
					}
				}
			}
		}

		return false;
	}

	private void method_6447() {
		class_243 lv = this.method_6448(new class_2338(this.field_6721, this.field_6720, this.field_6719));
		if (lv != null) {
			class_1642 lv2;
			try {
				lv2 = new class_1642(this.field_6727);
				lv2.method_5943(this.field_6727, this.field_6727.method_8404(new class_2338(lv2)), class_3730.field_16467, null, null);
			} catch (Exception var4) {
				var4.printStackTrace();
				return;
			}

			lv2.method_5808(lv.field_1352, lv.field_1351, lv.field_1350, this.field_6727.field_9229.nextFloat() * 360.0F, 0.0F);
			this.field_6727.method_8649(lv2);
		}
	}

	@Nullable
	private class_243 method_6448(class_2338 arg) {
		for (int i = 0; i < 10; i++) {
			class_2338 lv = arg.method_10069(
				this.field_6727.field_9229.nextInt(16) - 8, this.field_6727.field_9229.nextInt(6) - 3, this.field_6727.field_9229.nextInt(16) - 8
			);
			if (this.field_6727.method_19500(lv) && class_1948.method_8660(class_1317.class_1319.field_6317, this.field_6727, lv, null)) {
				return new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260());
			}
		}

		return null;
	}

	static enum class_4152 {
		field_18480,
		field_18481,
		field_18482;
	}
}
