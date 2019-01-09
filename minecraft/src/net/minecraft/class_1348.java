package net.minecraft;

import java.util.List;
import java.util.function.Predicate;

public class class_1348 extends class_1352 {
	private final class_1308 field_6432;
	private final Predicate<class_1308> field_6436;
	private class_1308 field_6433;
	private final double field_6430;
	private final class_1408 field_6434;
	private int field_6431;
	private final float field_6438;
	private float field_6437;
	private final float field_6435;

	public class_1348(class_1308 arg, double d, float f, float g) {
		this.field_6432 = arg;
		this.field_6436 = arg2 -> arg2 != null && arg.getClass() != arg2.getClass();
		this.field_6430 = d;
		this.field_6434 = arg.method_5942();
		this.field_6438 = f;
		this.field_6435 = g;
		this.method_6265(3);
		if (!(arg.method_5942() instanceof class_1409) && !(arg.method_5942() instanceof class_1407)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
		}
	}

	@Override
	public boolean method_6264() {
		List<class_1308> list = this.field_6432
			.field_6002
			.method_8390(class_1308.class, this.field_6432.method_5829().method_1014((double)this.field_6435), this.field_6436);
		if (!list.isEmpty()) {
			for (class_1308 lv : list) {
				if (!lv.method_5767()) {
					this.field_6433 = lv;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean method_6266() {
		return this.field_6433 != null
			&& !this.field_6434.method_6357()
			&& this.field_6432.method_5858(this.field_6433) > (double)(this.field_6438 * this.field_6438);
	}

	@Override
	public void method_6269() {
		this.field_6431 = 0;
		this.field_6437 = this.field_6432.method_5944(class_7.field_18);
		this.field_6432.method_5941(class_7.field_18, 0.0F);
	}

	@Override
	public void method_6270() {
		this.field_6433 = null;
		this.field_6434.method_6340();
		this.field_6432.method_5941(class_7.field_18, this.field_6437);
	}

	@Override
	public void method_6268() {
		if (this.field_6433 != null && !this.field_6432.method_5934()) {
			this.field_6432.method_5988().method_6226(this.field_6433, 10.0F, (float)this.field_6432.method_5978());
			if (--this.field_6431 <= 0) {
				this.field_6431 = 10;
				double d = this.field_6432.field_5987 - this.field_6433.field_5987;
				double e = this.field_6432.field_6010 - this.field_6433.field_6010;
				double f = this.field_6432.field_6035 - this.field_6433.field_6035;
				double g = d * d + e * e + f * f;
				if (!(g <= (double)(this.field_6438 * this.field_6438))) {
					this.field_6434.method_6335(this.field_6433, this.field_6430);
				} else {
					this.field_6434.method_6340();
					class_1333 lv = this.field_6433.method_5988();
					if (g <= (double)this.field_6438
						|| lv.method_6225() == this.field_6432.field_5987 && lv.method_6227() == this.field_6432.field_6010 && lv.method_6228() == this.field_6432.field_6035) {
						double h = this.field_6433.field_5987 - this.field_6432.field_5987;
						double i = this.field_6433.field_6035 - this.field_6432.field_6035;
						this.field_6434.method_6337(this.field_6432.field_5987 - h, this.field_6432.field_6010, this.field_6432.field_6035 - i, this.field_6430);
					}
				}
			}
		}
	}
}
