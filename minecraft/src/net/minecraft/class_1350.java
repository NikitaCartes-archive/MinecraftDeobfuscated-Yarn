package net.minecraft;

public class class_1350 extends class_1352 {
	private final class_1321 field_6448;
	private class_1309 field_6444;
	protected final class_1941 field_6445;
	private final double field_6442;
	private final class_1408 field_6446;
	private int field_6443;
	private final float field_6450;
	private final float field_6449;
	private float field_6447;

	public class_1350(class_1321 arg, double d, float f, float g) {
		this.field_6448 = arg;
		this.field_6445 = arg.field_6002;
		this.field_6442 = d;
		this.field_6446 = arg.method_5942();
		this.field_6449 = f;
		this.field_6450 = g;
		this.method_6265(3);
		if (!(arg.method_5942() instanceof class_1409) && !(arg.method_5942() instanceof class_1407)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean method_6264() {
		class_1309 lv = this.field_6448.method_6177();
		if (lv == null) {
			return false;
		} else if (lv instanceof class_1657 && ((class_1657)lv).method_7325()) {
			return false;
		} else if (this.field_6448.method_6172()) {
			return false;
		} else if (this.field_6448.method_5858(lv) < (double)(this.field_6449 * this.field_6449)) {
			return false;
		} else {
			this.field_6444 = lv;
			return true;
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6446.method_6357()
			&& this.field_6448.method_5858(this.field_6444) > (double)(this.field_6450 * this.field_6450)
			&& !this.field_6448.method_6172();
	}

	@Override
	public void method_6269() {
		this.field_6443 = 0;
		this.field_6447 = this.field_6448.method_5944(class_7.field_18);
		this.field_6448.method_5941(class_7.field_18, 0.0F);
	}

	@Override
	public void method_6270() {
		this.field_6444 = null;
		this.field_6446.method_6340();
		this.field_6448.method_5941(class_7.field_18, this.field_6447);
	}

	@Override
	public void method_6268() {
		this.field_6448.method_5988().method_6226(this.field_6444, 10.0F, (float)this.field_6448.method_5978());
		if (!this.field_6448.method_6172()) {
			if (--this.field_6443 <= 0) {
				this.field_6443 = 10;
				if (!this.field_6446.method_6335(this.field_6444, this.field_6442)) {
					if (!this.field_6448.method_5934() && !this.field_6448.method_5765()) {
						if (!(this.field_6448.method_5858(this.field_6444) < 144.0)) {
							int i = class_3532.method_15357(this.field_6444.field_5987) - 2;
							int j = class_3532.method_15357(this.field_6444.field_6035) - 2;
							int k = class_3532.method_15357(this.field_6444.method_5829().field_1322);

							for (int l = 0; l <= 4; l++) {
								for (int m = 0; m <= 4; m++) {
									if ((l < 1 || m < 1 || l > 3 || m > 3) && this.method_6263(i, j, k, l, m)) {
										this.field_6448
											.method_5808((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + m) + 0.5F), this.field_6448.field_6031, this.field_6448.field_5965);
										this.field_6446.method_6340();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean method_6263(int i, int j, int k, int l, int m) {
		class_2338 lv = new class_2338(i + l, k - 1, j + m);
		class_2680 lv2 = this.field_6445.method_8320(lv);
		return class_2248.method_9501(lv2.method_11628(this.field_6445, lv), class_2350.field_11033)
			&& lv2.method_11611(this.field_6448)
			&& this.field_6445.method_8623(lv.method_10084())
			&& this.field_6445.method_8623(lv.method_10086(2));
	}
}
