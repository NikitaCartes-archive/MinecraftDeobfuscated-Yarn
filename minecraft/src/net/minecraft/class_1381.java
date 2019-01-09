package net.minecraft;

public class class_1381 extends class_1352 {
	private final class_1308 field_6583;
	private final class_1603 field_6582;
	private class_1309 field_6580;
	private int field_6581 = -1;
	private final double field_6586;
	private int field_6579;
	private final int field_6578;
	private final int field_6577;
	private final float field_6585;
	private final float field_6584;

	public class_1381(class_1603 arg, double d, int i, float f) {
		this(arg, d, i, i, f);
	}

	public class_1381(class_1603 arg, double d, int i, int j, float f) {
		if (!(arg instanceof class_1309)) {
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		} else {
			this.field_6582 = arg;
			this.field_6583 = (class_1308)arg;
			this.field_6586 = d;
			this.field_6578 = i;
			this.field_6577 = j;
			this.field_6585 = f;
			this.field_6584 = f * f;
			this.method_6265(3);
		}
	}

	@Override
	public boolean method_6264() {
		class_1309 lv = this.field_6583.method_5968();
		if (lv == null) {
			return false;
		} else {
			this.field_6580 = lv;
			return true;
		}
	}

	@Override
	public boolean method_6266() {
		return this.method_6264() || !this.field_6583.method_5942().method_6357();
	}

	@Override
	public void method_6270() {
		this.field_6580 = null;
		this.field_6579 = 0;
		this.field_6581 = -1;
	}

	@Override
	public void method_6268() {
		double d = this.field_6583.method_5649(this.field_6580.field_5987, this.field_6580.method_5829().field_1322, this.field_6580.field_6035);
		boolean bl = this.field_6583.method_5985().method_6369(this.field_6580);
		if (bl) {
			this.field_6579++;
		} else {
			this.field_6579 = 0;
		}

		if (!(d > (double)this.field_6584) && this.field_6579 >= 5) {
			this.field_6583.method_5942().method_6340();
		} else {
			this.field_6583.method_5942().method_6335(this.field_6580, this.field_6586);
		}

		this.field_6583.method_5988().method_6226(this.field_6580, 30.0F, 30.0F);
		if (--this.field_6581 == 0) {
			if (!bl) {
				return;
			}

			float f = class_3532.method_15368(d) / this.field_6585;
			float g = class_3532.method_15363(f, 0.1F, 1.0F);
			this.field_6582.method_7105(this.field_6580, g);
			this.field_6581 = class_3532.method_15375(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
		} else if (this.field_6581 < 0) {
			float f = class_3532.method_15368(d) / this.field_6585;
			this.field_6581 = class_3532.method_15375(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
		}
	}
}
