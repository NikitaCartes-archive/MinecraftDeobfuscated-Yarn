package net.minecraft;

import javax.annotation.Nullable;

public class class_1394 extends class_1379 {
	protected final float field_6626;

	public class_1394(class_1314 arg, double d) {
		this(arg, d, 0.001F);
	}

	public class_1394(class_1314 arg, double d, float f) {
		super(arg, d);
		this.field_6626 = f;
	}

	@Nullable
	@Override
	protected class_243 method_6302() {
		if (this.field_6566.method_5816()) {
			class_243 lv = class_1414.method_6378(this.field_6566, 15, 7);
			return lv == null ? super.method_6302() : lv;
		} else {
			return this.field_6566.method_6051().nextFloat() >= this.field_6626 ? class_1414.method_6378(this.field_6566, 10, 7) : super.method_6302();
		}
	}
}
