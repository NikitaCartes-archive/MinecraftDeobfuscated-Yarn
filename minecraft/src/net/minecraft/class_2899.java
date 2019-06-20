package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2899 implements class_2596<class_2896> {
	private int field_13188;
	private class_2960 field_13187;
	private class_2540 field_13189;

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13188 = arg.method_10816();
		this.field_13187 = arg.method_10810();
		int i = arg.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.field_13189 = new class_2540(arg.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13188);
		arg.method_10812(this.field_13187);
		arg.writeBytes(this.field_13189.copy());
	}

	public void method_12591(class_2896 arg) {
		arg.method_12586(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_12592() {
		return this.field_13188;
	}
}
