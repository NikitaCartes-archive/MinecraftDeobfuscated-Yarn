package net.minecraft;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2752 implements class_2596<class_2602> {
	private int field_12594;
	private int[] field_12593;

	public class_2752() {
	}

	public class_2752(class_1297 arg) {
		this.field_12594 = arg.method_5628();
		List<class_1297> list = arg.method_5685();
		this.field_12593 = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			this.field_12593[i] = ((class_1297)list.get(i)).method_5628();
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12594 = arg.method_10816();
		this.field_12593 = arg.method_10787();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12594);
		arg.method_10806(this.field_12593);
	}

	public void method_11842(class_2602 arg) {
		arg.method_11080(this);
	}

	@Environment(EnvType.CLIENT)
	public int[] method_11840() {
		return this.field_12593;
	}

	@Environment(EnvType.CLIENT)
	public int method_11841() {
		return this.field_12594;
	}
}
