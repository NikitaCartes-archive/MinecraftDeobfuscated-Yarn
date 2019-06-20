package net.minecraft;

public abstract class class_1514 extends class_1512 {
	public class_1514(class_1510 arg) {
		super(arg);
	}

	@Override
	public boolean method_6848() {
		return true;
	}

	@Override
	public float method_6852(class_1282 arg, float f) {
		if (arg.method_5526() instanceof class_1665) {
			arg.method_5526().method_5639(1);
			return 0.0F;
		} else {
			return super.method_6852(arg, f);
		}
	}
}
