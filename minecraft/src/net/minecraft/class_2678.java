package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2678 implements class_2596<class_2602> {
	private int field_12280;
	private boolean field_12281;
	private class_1934 field_12282;
	private class_2874 field_12284;
	private int field_12279;
	private class_1942 field_12277;
	private int field_19145;
	private boolean field_12278;

	public class_2678() {
	}

	public class_2678(int i, class_1934 arg, boolean bl, class_2874 arg2, int j, class_1942 arg3, int k, boolean bl2) {
		this.field_12280 = i;
		this.field_12284 = arg2;
		this.field_12282 = arg;
		this.field_12279 = j;
		this.field_12281 = bl;
		this.field_12277 = arg3;
		this.field_19145 = k;
		this.field_12278 = bl2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12280 = arg.readInt();
		int i = arg.readUnsignedByte();
		this.field_12281 = (i & 8) == 8;
		i &= -9;
		this.field_12282 = class_1934.method_8384(i);
		this.field_12284 = class_2874.method_12490(arg.readInt());
		this.field_12279 = arg.readUnsignedByte();
		this.field_12277 = class_1942.method_8639(arg.method_10800(16));
		if (this.field_12277 == null) {
			this.field_12277 = class_1942.field_9265;
		}

		this.field_19145 = arg.method_10816();
		this.field_12278 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12280);
		int i = this.field_12282.method_8379();
		if (this.field_12281) {
			i |= 8;
		}

		arg.writeByte(i);
		arg.writeInt(this.field_12284.method_12484());
		arg.writeByte(this.field_12279);
		arg.method_10814(this.field_12277.method_8635());
		arg.method_10804(this.field_19145);
		arg.writeBoolean(this.field_12278);
	}

	public void method_11567(class_2602 arg) {
		arg.method_11120(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11564() {
		return this.field_12280;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11568() {
		return this.field_12281;
	}

	@Environment(EnvType.CLIENT)
	public class_1934 method_11561() {
		return this.field_12282;
	}

	@Environment(EnvType.CLIENT)
	public class_2874 method_11565() {
		return this.field_12284;
	}

	@Environment(EnvType.CLIENT)
	public class_1942 method_11563() {
		return this.field_12277;
	}

	@Environment(EnvType.CLIENT)
	public int method_20204() {
		return this.field_19145;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11562() {
		return this.field_12278;
	}
}
