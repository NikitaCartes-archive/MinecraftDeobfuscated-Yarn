package net.minecraft;

public class class_1683 extends class_3857 {
	public class_1683(class_1937 arg) {
		super(class_1299.field_6064, arg);
	}

	public class_1683(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6064, arg2, arg);
	}

	public class_1683(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6064, d, e, f, arg);
	}

	@Override
	protected class_1792 method_16942() {
		return class_1802.field_8287;
	}

	@Override
	protected float method_7490() {
		return 0.07F;
	}

	@Override
	protected void method_7492(class_239 arg) {
		if (!this.field_6002.field_9236) {
			this.field_6002.method_8535(2002, new class_2338(this), class_1844.method_8062(class_1847.field_8991));
			int i = 3 + this.field_6002.field_9229.nextInt(5) + this.field_6002.field_9229.nextInt(5);

			while (i > 0) {
				int j = class_1303.method_5918(i);
				i -= j;
				this.field_6002.method_8649(new class_1303(this.field_6002, this.field_5987, this.field_6010, this.field_6035, j));
			}

			this.method_5650();
		}
	}
}
