package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2718 implements class_2596<class_2602> {
	private int field_12424;
	private class_1291 field_12425;

	public class_2718() {
	}

	public class_2718(int i, class_1291 arg) {
		this.field_12424 = i;
		this.field_12425 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12424 = arg.method_10816();
		this.field_12425 = class_1291.method_5569(arg.readUnsignedByte());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12424);
		arg.writeByte(class_1291.method_5554(this.field_12425));
	}

	public void method_11769(class_2602 arg) {
		arg.method_11119(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1297 method_11767(class_1937 arg) {
		return arg.method_8469(this.field_12424);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1291 method_11768() {
		return this.field_12425;
	}
}
