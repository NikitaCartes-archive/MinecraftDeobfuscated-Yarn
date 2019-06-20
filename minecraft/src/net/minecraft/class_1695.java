package net.minecraft;

public class class_1695 extends class_1688 {
	public class_1695(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1695(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6096, arg, d, e, f);
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		if (arg.method_5715()) {
			return false;
		} else if (this.method_5782()) {
			return true;
		} else {
			if (!this.field_6002.field_9236) {
				arg.method_5804(this);
			}

			return true;
		}
	}

	@Override
	public void method_7506(int i, int j, int k, boolean bl) {
		if (bl) {
			if (this.method_5782()) {
				this.method_5772();
			}

			if (this.method_7507() == 0) {
				this.method_7524(-this.method_7522());
				this.method_7509(10);
				this.method_7520(50.0F);
				this.method_5785();
			}
		}
	}

	@Override
	public class_1688.class_1689 method_7518() {
		return class_1688.class_1689.field_7674;
	}
}
