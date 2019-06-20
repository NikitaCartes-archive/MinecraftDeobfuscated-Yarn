package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2632 implements class_2596<class_2602> {
	private class_1267 field_12090;
	private boolean field_12091;

	public class_2632() {
	}

	public class_2632(class_1267 arg, boolean bl) {
		this.field_12090 = arg;
		this.field_12091 = bl;
	}

	public void method_11341(class_2602 arg) {
		arg.method_11140(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12090 = class_1267.method_5462(arg.readUnsignedByte());
		this.field_12091 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12090.method_5461());
		arg.writeBoolean(this.field_12091);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11340() {
		return this.field_12091;
	}

	@Environment(EnvType.CLIENT)
	public class_1267 method_11342() {
		return this.field_12090;
	}
}
