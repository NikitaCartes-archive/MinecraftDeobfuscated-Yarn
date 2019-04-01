package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2783 implements class_2596<class_2602> {
	private int field_12727;
	private byte field_12725;
	private byte field_12729;
	private int field_12726;
	private byte field_12728;

	public class_2783() {
	}

	public class_2783(int i, class_1293 arg) {
		this.field_12727 = i;
		this.field_12725 = (byte)(class_1291.method_5554(arg.method_5579()) & 0xFF);
		this.field_12729 = (byte)(arg.method_5578() & 0xFF);
		if (arg.method_5584() > 32767) {
			this.field_12726 = 32767;
		} else {
			this.field_12726 = arg.method_5584();
		}

		this.field_12728 = 0;
		if (arg.method_5591()) {
			this.field_12728 = (byte)(this.field_12728 | 1);
		}

		if (arg.method_5581()) {
			this.field_12728 = (byte)(this.field_12728 | 2);
		}

		if (arg.method_5592()) {
			this.field_12728 = (byte)(this.field_12728 | 4);
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12727 = arg.method_10816();
		this.field_12725 = arg.readByte();
		this.field_12729 = arg.readByte();
		this.field_12726 = arg.method_10816();
		this.field_12728 = arg.readByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12727);
		arg.writeByte(this.field_12725);
		arg.writeByte(this.field_12729);
		arg.method_10804(this.field_12726);
		arg.writeByte(this.field_12728);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11947() {
		return this.field_12726 == 32767;
	}

	public void method_11948(class_2602 arg) {
		arg.method_11084(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11943() {
		return this.field_12727;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11946() {
		return this.field_12725;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11945() {
		return this.field_12729;
	}

	@Environment(EnvType.CLIENT)
	public int method_11944() {
		return this.field_12726;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11949() {
		return (this.field_12728 & 2) == 2;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11950() {
		return (this.field_12728 & 1) == 1;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11942() {
		return (this.field_12728 & 4) == 4;
	}
}
