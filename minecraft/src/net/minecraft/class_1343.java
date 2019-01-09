package net.minecraft;

public abstract class class_1343 extends class_1352 {
	protected class_1308 field_6413;
	protected class_2338 field_6414 = class_2338.field_10980;
	protected boolean field_6412;
	private boolean field_6411;
	private float field_6410;
	private float field_6409;

	public class_1343(class_1308 arg) {
		this.field_6413 = arg;
		if (!(arg.method_5942() instanceof class_1409)) {
			throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
		}
	}

	protected boolean method_6256() {
		if (!this.field_6412) {
			return false;
		} else {
			class_2680 lv = this.field_6413.field_6002.method_8320(this.field_6414);
			if (!(lv.method_11614() instanceof class_2323)) {
				this.field_6412 = false;
				return false;
			} else {
				return (Boolean)lv.method_11654(class_2323.field_10945);
			}
		}
	}

	protected void method_6255(boolean bl) {
		if (this.field_6412) {
			class_2680 lv = this.field_6413.field_6002.method_8320(this.field_6414);
			if (lv.method_11614() instanceof class_2323) {
				((class_2323)lv.method_11614()).method_10033(this.field_6413.field_6002, this.field_6414, bl);
			}
		}
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6413.field_5976) {
			return false;
		} else {
			class_1409 lv = (class_1409)this.field_6413.method_5942();
			class_11 lv2 = lv.method_6345();
			if (lv2 != null && !lv2.method_46() && lv.method_6366()) {
				for (int i = 0; i < Math.min(lv2.method_39() + 2, lv2.method_38()); i++) {
					class_9 lv3 = lv2.method_40(i);
					this.field_6414 = new class_2338(lv3.field_40, lv3.field_39 + 1, lv3.field_38);
					if (!(this.field_6413.method_5649((double)this.field_6414.method_10263(), this.field_6413.field_6010, (double)this.field_6414.method_10260()) > 2.25)) {
						this.field_6412 = this.method_6254(this.field_6414);
						if (this.field_6412) {
							return true;
						}
					}
				}

				this.field_6414 = new class_2338(this.field_6413).method_10084();
				this.field_6412 = this.method_6254(this.field_6414);
				return this.field_6412;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6411;
	}

	@Override
	public void method_6269() {
		this.field_6411 = false;
		this.field_6410 = (float)((double)((float)this.field_6414.method_10263() + 0.5F) - this.field_6413.field_5987);
		this.field_6409 = (float)((double)((float)this.field_6414.method_10260() + 0.5F) - this.field_6413.field_6035);
	}

	@Override
	public void method_6268() {
		float f = (float)((double)((float)this.field_6414.method_10263() + 0.5F) - this.field_6413.field_5987);
		float g = (float)((double)((float)this.field_6414.method_10260() + 0.5F) - this.field_6413.field_6035);
		float h = this.field_6410 * f + this.field_6409 * g;
		if (h < 0.0F) {
			this.field_6411 = true;
		}
	}

	private boolean method_6254(class_2338 arg) {
		class_2680 lv = this.field_6413.field_6002.method_8320(arg);
		return lv.method_11614() instanceof class_2323 && lv.method_11620() == class_3614.field_15932;
	}
}
