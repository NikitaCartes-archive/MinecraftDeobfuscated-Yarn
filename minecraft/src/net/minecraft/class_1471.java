package net.minecraft;

public abstract class class_1471 extends class_1321 {
	private int field_6864;

	protected class_1471(class_1299<? extends class_1471> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public boolean method_6627(class_3222 arg) {
		class_2487 lv = new class_2487();
		lv.method_10582("id", this.method_5653());
		this.method_5647(lv);
		if (arg.method_7298(lv)) {
			this.method_5650();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_5773() {
		this.field_6864++;
		super.method_5773();
	}

	public boolean method_6626() {
		return this.field_6864 > 100;
	}
}
