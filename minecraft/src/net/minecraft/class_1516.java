package net.minecraft;

import javax.annotation.Nullable;

public class class_1516 extends class_1512 {
	private class_243 field_7042;

	public class_1516(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6855() {
		if (this.field_7042 == null) {
			this.field_7042 = new class_243(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035);
		}
	}

	@Override
	public boolean method_6848() {
		return true;
	}

	@Override
	public void method_6856() {
		this.field_7042 = null;
	}

	@Override
	public float method_6846() {
		return 1.0F;
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7042;
	}

	@Override
	public class_1527<class_1516> method_6849() {
		return class_1527.field_7075;
	}
}
