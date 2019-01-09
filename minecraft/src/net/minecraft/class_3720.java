package net.minecraft;

public class class_3720 extends class_2609 {
	public class_3720() {
		super(class_2591.field_16415);
	}

	@Override
	protected class_2561 method_5477() {
		return new class_2588("container.blast_furnace");
	}

	@Override
	protected int method_17029() {
		class_3859 lv = (class_3859)this.field_11863.method_8433().method_8132(this, this.field_11863);
		return lv != null ? lv.method_8167() : super.method_17029();
	}

	@Override
	protected int method_11200(class_1799 arg) {
		return super.method_11200(arg) / 2;
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_11863.method_8321(this.field_11867) != this ? false : super.method_5443(arg);
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_3705(i, arg, this, this.field_17374);
	}
}
