package net.minecraft;

public class class_3866 extends class_2609 {
	public class_3866() {
		super(class_2591.field_11903);
	}

	@Override
	protected class_2561 method_5477() {
		return new class_2588("container.furnace");
	}

	@Override
	protected int method_17029() {
		class_3861 lv = (class_3861)this.field_11863.method_8433().method_8132(this, this.field_11863);
		return lv != null ? lv.method_8167() : super.method_17029();
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_11863.method_8321(this.field_11867) != this ? false : super.method_5443(arg);
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_3858(i, arg, this, this.field_17374);
	}
}
