package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2648 implements class_2596<class_2602> {
	private int field_12144;
	private int field_12143;
	private int field_12142;

	public class_2648() {
	}

	public class_2648(int i, int j, int k) {
		this.field_12144 = i;
		this.field_12143 = j;
		this.field_12142 = k;
	}

	public void method_11437(class_2602 arg) {
		arg.method_11089(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12144 = arg.readUnsignedByte();
		this.field_12143 = arg.method_10816();
		this.field_12142 = arg.readInt();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12144);
		arg.method_10804(this.field_12143);
		arg.writeInt(this.field_12142);
	}

	@Environment(EnvType.CLIENT)
	public int method_11432() {
		return this.field_12144;
	}

	@Environment(EnvType.CLIENT)
	public int method_11434() {
		return this.field_12143;
	}

	@Environment(EnvType.CLIENT)
	public int method_11433() {
		return this.field_12142;
	}
}
