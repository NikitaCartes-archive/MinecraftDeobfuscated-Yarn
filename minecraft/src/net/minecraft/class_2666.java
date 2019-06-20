package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2666 implements class_2596<class_2602> {
	private int field_12195;
	private int field_12194;

	public class_2666() {
	}

	public class_2666(int i, int j) {
		this.field_12195 = i;
		this.field_12194 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12195 = arg.readInt();
		this.field_12194 = arg.readInt();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12195);
		arg.writeInt(this.field_12194);
	}

	public void method_11486(class_2602 arg) {
		arg.method_11107(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11487() {
		return this.field_12195;
	}

	@Environment(EnvType.CLIENT)
	public int method_11485() {
		return this.field_12194;
	}
}
