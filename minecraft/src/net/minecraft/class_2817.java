package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2817 implements class_2596<class_2792> {
	public static final class_2960 field_12831 = new class_2960("brand");
	private class_2960 field_12830;
	private class_2540 field_12832;

	public class_2817() {
	}

	@Environment(EnvType.CLIENT)
	public class_2817(class_2960 arg, class_2540 arg2) {
		this.field_12830 = arg;
		this.field_12832 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12830 = arg.method_10810();
		int i = arg.readableBytes();
		if (i >= 0 && i <= 32767) {
			this.field_12832 = new class_2540(arg.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10812(this.field_12830);
		arg.writeBytes(this.field_12832);
	}

	public void method_12199(class_2792 arg) {
		arg.method_12075(this);
		if (this.field_12832 != null) {
			this.field_12832.release();
		}
	}
}
