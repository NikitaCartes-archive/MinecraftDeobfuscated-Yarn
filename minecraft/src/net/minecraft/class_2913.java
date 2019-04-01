package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2913 implements class_2596<class_2911> {
	private int field_13259;
	private class_2540 field_13258;

	public class_2913() {
	}

	@Environment(EnvType.CLIENT)
	public class_2913(int i, @Nullable class_2540 arg) {
		this.field_13259 = i;
		this.field_13258 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13259 = arg.method_10816();
		if (arg.readBoolean()) {
			int i = arg.readableBytes();
			if (i < 0 || i > 1048576) {
				throw new IOException("Payload may not be larger than 1048576 bytes");
			}

			this.field_13258 = new class_2540(arg.readBytes(i));
		} else {
			this.field_13258 = null;
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13259);
		if (this.field_13258 != null) {
			arg.writeBoolean(true);
			arg.writeBytes(this.field_13258.copy());
		} else {
			arg.writeBoolean(false);
		}
	}

	public void method_12645(class_2911 arg) {
		arg.method_12640(this);
	}
}
