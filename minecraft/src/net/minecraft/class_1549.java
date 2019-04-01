package net.minecraft;

import javax.annotation.Nullable;

public class class_1549 extends class_1628 {
	public class_1549(class_1299<? extends class_1549> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(12.0);
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		if (super.method_6121(arg)) {
			if (arg instanceof class_1309) {
				int i = 0;
				if (this.field_6002.method_8407() == class_1267.field_5802) {
					i = 7;
				} else if (this.field_6002.method_8407() == class_1267.field_5807) {
					i = 15;
				}

				if (i > 0) {
					((class_1309)arg).method_6092(new class_1293(class_1294.field_5899, i * 20, 0));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		return arg4;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 0.45F;
	}
}
