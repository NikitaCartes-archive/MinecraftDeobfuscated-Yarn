package net.minecraft;

public class class_1807 extends class_1792 {
	public class_1807(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public boolean method_7847(class_1799 arg, class_1657 arg2, class_1309 arg3, class_1268 arg4) {
		if (arg.method_7938() && !(arg3 instanceof class_1657)) {
			arg3.method_5665(arg.method_7964());
			if (arg3 instanceof class_1308) {
				((class_1308)arg3).method_5971();
			}

			arg.method_7934(1);
			return true;
		} else {
			return false;
		}
	}
}
