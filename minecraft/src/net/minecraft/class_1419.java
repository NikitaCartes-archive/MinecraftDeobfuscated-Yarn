package net.minecraft;

import javax.annotation.Nullable;

public class class_1419 {
	private final class_1937 field_6727;
	private boolean field_6725;
	private int field_6724 = -1;
	private int field_6723;
	private int field_6722;
	private class_1415 field_6726;
	private int field_6721;
	private int field_6720;
	private int field_6719;

	public class_1419(class_1937 arg) {
		this.field_6727 = arg;
	}

	public void method_6445() {
		if (this.field_6727.method_8530()) {
			this.field_6724 = 0;
		} else if (this.field_6724 != 2) {
			if (this.field_6724 == 0) {
				float f = this.field_6727.method_8400(0.0F);
				if ((double)f < 0.5 || (double)f > 0.501) {
					return;
				}

				this.field_6724 = this.field_6727.field_9229.nextInt(10) == 0 ? 1 : 2;
				this.field_6725 = false;
				if (this.field_6724 == 2) {
					return;
				}
			}

			if (this.field_6724 != -1) {
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
						this.field_6724 = 2;
					}
				}
			}
		}
	}

	private boolean method_6446() {
		for (class_1657 lv : this.field_6727.field_9228) {
			if (!lv.method_7325()) {
				this.field_6726 = this.field_6727.method_8557().method_6438(new class_2338(lv), 1);
				if (this.field_6726 != null && this.field_6726.method_6384() >= 10 && this.field_6726.method_6402() >= 20 && this.field_6726.method_6387() >= 20) {
					class_2338 lv2 = this.field_6726.method_6382();
					float f = (float)this.field_6726.method_6403();
					boolean bl = false;

					for (int i = 0; i < 10; i++) {
						float g = this.field_6727.field_9229.nextFloat() * (float) (Math.PI * 2);
						this.field_6721 = lv2.method_10263() + (int)((double)(class_3532.method_15362(g) * f) * 0.9);
						this.field_6720 = lv2.method_10264();
						this.field_6719 = lv2.method_10260() + (int)((double)(class_3532.method_15374(g) * f) * 0.9);
						bl = false;

						for (class_1415 lv3 : this.field_6727.method_8557().method_6436()) {
							if (lv3 != this.field_6726 && lv3.method_6383(new class_2338(this.field_6721, this.field_6720, this.field_6719))) {
								bl = true;
								break;
							}
						}

						if (!bl) {
							break;
						}
					}

					if (bl) {
						return false;
					}

					class_243 lv4 = this.method_6448(new class_2338(this.field_6721, this.field_6720, this.field_6719));
					if (lv4 != null) {
						this.field_6722 = 0;
						this.field_6723 = 20;
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean method_6447() {
		class_243 lv = this.method_6448(new class_2338(this.field_6721, this.field_6720, this.field_6719));
		if (lv == null) {
			return false;
		} else {
			class_1642 lv2;
			try {
				lv2 = new class_1642(this.field_6727);
				lv2.method_5943(this.field_6727, this.field_6727.method_8404(new class_2338(lv2)), class_3730.field_16467, null, null);
			} catch (Exception var4) {
				var4.printStackTrace();
				return false;
			}

			lv2.method_5808(lv.field_1352, lv.field_1351, lv.field_1350, this.field_6727.field_9229.nextFloat() * 360.0F, 0.0F);
			this.field_6727.method_8649(lv2);
			class_2338 lv3 = this.field_6726.method_6382();
			lv2.method_6145(lv3, this.field_6726.method_6403());
			return true;
		}
	}

	@Nullable
	private class_243 method_6448(class_2338 arg) {
		for (int i = 0; i < 10; i++) {
			class_2338 lv = arg.method_10069(
				this.field_6727.field_9229.nextInt(16) - 8, this.field_6727.field_9229.nextInt(6) - 3, this.field_6727.field_9229.nextInt(16) - 8
			);
			if (this.field_6726.method_6383(lv) && class_1948.method_8660(class_1317.class_1319.field_6317, this.field_6727, lv, null)) {
				return new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260());
			}
		}

		return null;
	}
}
