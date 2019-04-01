package net.minecraft;

import java.io.IOException;

public class class_2923 implements class_2596<class_2921> {
	private long field_13280;

	public class_2923() {
	}

	public class_2923(long l) {
		this.field_13280 = l;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13280 = arg.readLong();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeLong(this.field_13280);
	}

	public void method_12670(class_2921 arg) {
		arg.method_12666(this);
	}
}
