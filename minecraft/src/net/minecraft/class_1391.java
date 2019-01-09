package net.minecraft;

public class class_1391 extends class_1352 {
	protected final class_1314 field_6616;
	private final double field_6615;
	private double field_6614;
	private double field_6611;
	private double field_6621;
	private double field_6619;
	private double field_6618;
	protected class_1657 field_6617;
	private int field_6612;
	private boolean field_6613;
	private final class_1856 field_6622;
	private final boolean field_6620;

	public class_1391(class_1314 arg, double d, class_1856 arg2, boolean bl) {
		this(arg, d, bl, arg2);
	}

	public class_1391(class_1314 arg, double d, boolean bl, class_1856 arg2) {
		this.field_6616 = arg;
		this.field_6615 = d;
		this.field_6622 = arg2;
		this.field_6620 = bl;
		this.method_6265(3);
		if (!(arg.method_5942() instanceof class_1409)) {
			throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
		}
	}

	@Override
	public boolean method_6264() {
		if (this.field_6612 > 0) {
			this.field_6612--;
			return false;
		} else {
			this.field_6617 = this.field_6616.field_6002.method_8614(this.field_6616, 10.0);
			return this.field_6617 == null ? false : this.method_6312(this.field_6617.method_6047()) || this.method_6312(this.field_6617.method_6079());
		}
	}

	protected boolean method_6312(class_1799 arg) {
		return this.field_6622.method_8093(arg);
	}

	@Override
	public boolean method_6266() {
		if (this.method_16081()) {
			if (this.field_6616.method_5858(this.field_6617) < 36.0) {
				if (this.field_6617.method_5649(this.field_6614, this.field_6611, this.field_6621) > 0.010000000000000002) {
					return false;
				}

				if (Math.abs((double)this.field_6617.field_5965 - this.field_6619) > 5.0 || Math.abs((double)this.field_6617.field_6031 - this.field_6618) > 5.0) {
					return false;
				}
			} else {
				this.field_6614 = this.field_6617.field_5987;
				this.field_6611 = this.field_6617.field_6010;
				this.field_6621 = this.field_6617.field_6035;
			}

			this.field_6619 = (double)this.field_6617.field_5965;
			this.field_6618 = (double)this.field_6617.field_6031;
		}

		return this.method_6264();
	}

	protected boolean method_16081() {
		return this.field_6620;
	}

	@Override
	public void method_6269() {
		this.field_6614 = this.field_6617.field_5987;
		this.field_6611 = this.field_6617.field_6010;
		this.field_6621 = this.field_6617.field_6035;
		this.field_6613 = true;
	}

	@Override
	public void method_6270() {
		this.field_6617 = null;
		this.field_6616.method_5942().method_6340();
		this.field_6612 = 100;
		this.field_6613 = false;
	}

	@Override
	public void method_6268() {
		this.field_6616.method_5988().method_6226(this.field_6617, (float)(this.field_6616.method_5986() + 20), (float)this.field_6616.method_5978());
		if (this.field_6616.method_5858(this.field_6617) < 6.25) {
			this.field_6616.method_5942().method_6340();
		} else {
			this.field_6616.method_5942().method_6335(this.field_6617, this.field_6615);
		}
	}

	public boolean method_6313() {
		return this.field_6613;
	}
}
