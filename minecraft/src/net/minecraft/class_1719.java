package net.minecraft;

public class class_1719 extends class_1735 {
	private final class_1657 field_7818;
	private int field_7819;

	public class_1719(class_1657 arg, class_1263 arg2, int i, int j, int k) {
		super(arg2, i, j, k);
		this.field_7818 = arg;
	}

	@Override
	public boolean method_7680(class_1799 arg) {
		return false;
	}

	@Override
	public class_1799 method_7671(int i) {
		if (this.method_7681()) {
			this.field_7819 = this.field_7819 + Math.min(i, this.method_7677().method_7947());
		}

		return super.method_7671(i);
	}

	@Override
	public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
		this.method_7669(arg2);
		super.method_7667(arg, arg2);
		return arg2;
	}

	@Override
	protected void method_7678(class_1799 arg, int i) {
		this.field_7819 += i;
		this.method_7669(arg);
	}

	@Override
	protected void method_7669(class_1799 arg) {
		arg.method_7982(this.field_7818.field_6002, this.field_7818, this.field_7819);
		if (!this.field_7818.field_6002.field_9236 && this.field_7871 instanceof class_2609) {
			((class_2609)this.field_7871).method_17763(this.field_7818);
		}

		this.field_7819 = 0;
	}
}
