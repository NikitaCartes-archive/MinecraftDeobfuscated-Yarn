package net.minecraft;

public class class_2619 extends class_2586 implements class_3829 {
	private class_1799 field_12031 = class_1799.field_8037;

	public class_2619() {
		super(class_2591.field_11907);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		if (arg.method_10573("RecordItem", 10)) {
			this.method_11276(class_1799.method_7915(arg.method_10562("RecordItem")));
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (!this.method_11275().method_7960()) {
			arg.method_10566("RecordItem", this.method_11275().method_7953(new class_2487()));
		}

		return arg;
	}

	public class_1799 method_11275() {
		return this.field_12031;
	}

	public void method_11276(class_1799 arg) {
		this.field_12031 = arg;
		this.method_5431();
	}

	@Override
	public void method_5448() {
		this.method_11276(class_1799.field_8037);
	}
}
