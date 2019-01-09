package net.minecraft;

public class class_1727 extends class_1735 {
	private final class_1725 field_7860;
	private final class_1657 field_7857;
	private int field_7859;
	private final class_1915 field_7858;

	public class_1727(class_1657 arg, class_1915 arg2, class_1725 arg3, int i, int j, int k) {
		super(arg3, i, j, k);
		this.field_7857 = arg;
		this.field_7858 = arg2;
		this.field_7860 = arg3;
	}

	@Override
	public boolean method_7680(class_1799 arg) {
		return false;
	}

	@Override
	public class_1799 method_7671(int i) {
		if (this.method_7681()) {
			this.field_7859 = this.field_7859 + Math.min(i, this.method_7677().method_7947());
		}

		return super.method_7671(i);
	}

	@Override
	protected void method_7678(class_1799 arg, int i) {
		this.field_7859 += i;
		this.method_7669(arg);
	}

	@Override
	protected void method_7669(class_1799 arg) {
		arg.method_7982(this.field_7857.field_6002, this.field_7857, this.field_7859);
		this.field_7859 = 0;
	}

	@Override
	public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
		this.method_7669(arg2);
		class_1914 lv = this.field_7860.method_7642();
		if (lv != null) {
			class_1799 lv2 = this.field_7860.method_5438(0);
			class_1799 lv3 = this.field_7860.method_5438(1);
			if (lv.method_16953(lv2, lv3) || lv.method_16953(lv3, lv2)) {
				this.field_7858.method_8262(lv);
				arg.method_7281(class_3468.field_15378);
				this.field_7860.method_5447(0, lv2);
				this.field_7860.method_5447(1, lv3);
			}
		}

		return arg2;
	}
}
