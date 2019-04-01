package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2716 implements class_2596<class_2602> {
	private int[] field_12422;

	public class_2716() {
	}

	public class_2716(int... is) {
		this.field_12422 = is;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12422 = new int[arg.method_10816()];

		for (int i = 0; i < this.field_12422.length; i++) {
			this.field_12422[i] = arg.method_10816();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12422.length);

		for (int i : this.field_12422) {
			arg.method_10804(i);
		}
	}

	public void method_11764(class_2602 arg) {
		arg.method_11095(this);
	}

	@Environment(EnvType.CLIENT)
	public int[] method_11763() {
		return this.field_12422;
	}
}
