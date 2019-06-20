package net.minecraft;

public class class_1364 extends class_1361 {
	private final class_3988 field_6495;

	public class_1364(class_3988 arg) {
		super(arg, class_1657.class, 8.0F);
		this.field_6495 = arg;
	}

	@Override
	public boolean method_6264() {
		if (this.field_6495.method_18009()) {
			this.field_6484 = this.field_6495.method_8257();
			return true;
		} else {
			return false;
		}
	}
}
