package net.minecraft;

public class class_3705 extends class_1720<class_3705> {
	public class_3705(int i, class_1661 arg) {
		super(i, arg);
	}

	public class_3705(int i, class_1661 arg, class_1263 arg2, class_3913 arg3) {
		super(i, arg, arg2, arg3);
	}

	@Override
	public class_3917<class_3705> method_17358() {
		return class_3917.field_17331;
	}

	@Override
	protected boolean method_7640(class_1799 arg) {
		for (class_1860 lv : this.field_7822.method_8433().method_8126()) {
			if (lv instanceof class_3859 && lv.method_8117().get(0).method_8093(arg)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean method_16945(class_1799 arg) {
		return class_3720.method_11195(arg);
	}
}
