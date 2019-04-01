package net.minecraft;

public class class_1384 extends class_1352 {
	private final class_1314 field_6594;

	public class_1384(class_1314 arg) {
		this.field_6594 = arg;
	}

	@Override
	public boolean method_6264() {
		return this.field_6594.field_6002.method_8530()
			&& this.field_6594.method_6118(class_1304.field_6169).method_7960()
			&& this.field_6594.method_5942() instanceof class_1409;
	}

	@Override
	public void method_6269() {
		((class_1409)this.field_6594.method_5942()).method_6361(true);
	}

	@Override
	public void method_6270() {
		((class_1409)this.field_6594.method_5942()).method_6361(false);
	}
}
