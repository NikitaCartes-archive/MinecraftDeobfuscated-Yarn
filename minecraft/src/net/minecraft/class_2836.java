package net.minecraft;

import java.io.IOException;

public class class_2836 implements class_2596<class_2792> {
	private boolean field_12907;
	private boolean field_12906;

	public class_2836() {
	}

	public class_2836(boolean bl, boolean bl2) {
		this.field_12907 = bl;
		this.field_12906 = bl2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12907 = arg.readBoolean();
		this.field_12906 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeBoolean(this.field_12907);
		arg.writeBoolean(this.field_12906);
	}

	public void method_12283(class_2792 arg) {
		arg.method_12064(this);
	}

	public boolean method_12284() {
		return this.field_12907;
	}

	public boolean method_12285() {
		return this.field_12906;
	}
}
