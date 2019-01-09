package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2815 implements class_2596<class_2792> {
	private int field_12827;

	public class_2815() {
	}

	@Environment(EnvType.CLIENT)
	public class_2815(int i) {
		this.field_12827 = i;
	}

	public void method_12198(class_2792 arg) {
		arg.method_12054(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12827 = arg.readByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12827);
	}
}
