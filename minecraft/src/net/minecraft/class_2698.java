package net.minecraft;

import java.io.IOException;

public class class_2698 implements class_2596<class_2602> {
	public class_2698.class_2699 field_12347;
	public int field_12349;
	public int field_12348;
	public int field_12345;
	public class_2561 field_12346;

	public class_2698() {
	}

	public class_2698(class_1283 arg, class_2698.class_2699 arg2) {
		this(arg, arg2, new class_2585(""));
	}

	public class_2698(class_1283 arg, class_2698.class_2699 arg2, class_2561 arg3) {
		this.field_12347 = arg2;
		class_1309 lv = arg.method_5541();
		switch (arg2) {
			case field_12353:
				this.field_12345 = arg.method_5546();
				this.field_12348 = lv == null ? -1 : lv.method_5628();
				break;
			case field_12350:
				this.field_12349 = arg.method_5540().method_5628();
				this.field_12348 = lv == null ? -1 : lv.method_5628();
				this.field_12346 = arg3;
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12347 = arg.method_10818(class_2698.class_2699.class);
		if (this.field_12347 == class_2698.class_2699.field_12353) {
			this.field_12345 = arg.method_10816();
			this.field_12348 = arg.readInt();
		} else if (this.field_12347 == class_2698.class_2699.field_12350) {
			this.field_12349 = arg.method_10816();
			this.field_12348 = arg.readInt();
			this.field_12346 = arg.method_10808();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12347);
		if (this.field_12347 == class_2698.class_2699.field_12353) {
			arg.method_10804(this.field_12345);
			arg.writeInt(this.field_12348);
		} else if (this.field_12347 == class_2698.class_2699.field_12350) {
			arg.method_10804(this.field_12349);
			arg.writeInt(this.field_12348);
			arg.method_10805(this.field_12346);
		}
	}

	public void method_11706(class_2602 arg) {
		arg.method_11133(this);
	}

	@Override
	public boolean method_11051() {
		return this.field_12347 == class_2698.class_2699.field_12350;
	}

	public static enum class_2699 {
		field_12352,
		field_12353,
		field_12350;
	}
}
