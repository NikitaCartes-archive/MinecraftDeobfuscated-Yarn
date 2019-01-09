package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2795 implements class_2596<class_2792> {
	private int field_12762;
	private class_2338 field_12763;

	public class_2795() {
	}

	@Environment(EnvType.CLIENT)
	public class_2795(int i, class_2338 arg) {
		this.field_12762 = i;
		this.field_12763 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12762 = arg.method_10816();
		this.field_12763 = arg.method_10811();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12762);
		arg.method_10807(this.field_12763);
	}

	public void method_12095(class_2792 arg) {
		arg.method_12072(this);
	}

	public int method_12096() {
		return this.field_12762;
	}

	public class_2338 method_12094() {
		return this.field_12763;
	}
}
