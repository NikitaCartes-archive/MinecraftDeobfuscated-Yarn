package net.minecraft;

public class class_3720 extends class_2609 {
	public class_3720() {
		super(class_2591.field_16415, class_3956.field_17547);
	}

	@Override
	protected class_2561 method_17823() {
		return new class_2588("container.blast_furnace");
	}

	@Override
	protected int method_11200(class_1799 arg) {
		return super.method_11200(arg) / 2;
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_3705(i, arg, this, this.field_17374);
	}
}
