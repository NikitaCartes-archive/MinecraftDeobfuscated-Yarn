package net.minecraft;

public class class_1520 extends class_1514 {
	private int field_7049;

	public class_1520(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6853() {
		this.field_7036
			.field_6002
			.method_8486(
				this.field_7036.field_5987,
				this.field_7036.field_6010,
				this.field_7036.field_6035,
				class_3417.field_14671,
				this.field_7036.method_5634(),
				2.5F,
				0.8F + this.field_7036.method_6051().nextFloat() * 0.3F,
				false
			);
	}

	@Override
	public void method_6855() {
		if (this.field_7049++ >= 40) {
			this.field_7036.method_6831().method_6863(class_1527.field_7072);
		}
	}

	@Override
	public void method_6856() {
		this.field_7049 = 0;
	}

	@Override
	public class_1527<class_1520> method_6849() {
		return class_1527.field_7073;
	}
}
