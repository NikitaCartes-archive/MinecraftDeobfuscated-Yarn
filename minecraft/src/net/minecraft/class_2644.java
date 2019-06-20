package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2644 implements class_2596<class_2602> {
	private int field_12136;
	private short field_12134;
	private boolean field_12135;

	public class_2644() {
	}

	public class_2644(int i, short s, boolean bl) {
		this.field_12136 = i;
		this.field_12134 = s;
		this.field_12135 = bl;
	}

	public void method_11424(class_2602 arg) {
		arg.method_11123(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12136 = arg.readUnsignedByte();
		this.field_12134 = arg.readShort();
		this.field_12135 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12136);
		arg.writeShort(this.field_12134);
		arg.writeBoolean(this.field_12135);
	}

	@Environment(EnvType.CLIENT)
	public int method_11425() {
		return this.field_12136;
	}

	@Environment(EnvType.CLIENT)
	public short method_11423() {
		return this.field_12134;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11426() {
		return this.field_12135;
	}
}
