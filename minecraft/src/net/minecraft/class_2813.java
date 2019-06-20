package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2813 implements class_2596<class_2792> {
	private int field_12819;
	private int field_12818;
	private int field_12817;
	private short field_12820;
	private class_1799 field_12816 = class_1799.field_8037;
	private class_1713 field_12815;

	public class_2813() {
	}

	@Environment(EnvType.CLIENT)
	public class_2813(int i, int j, int k, class_1713 arg, class_1799 arg2, short s) {
		this.field_12819 = i;
		this.field_12818 = j;
		this.field_12817 = k;
		this.field_12816 = arg2.method_7972();
		this.field_12820 = s;
		this.field_12815 = arg;
	}

	public void method_12191(class_2792 arg) {
		arg.method_12076(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12819 = arg.readByte();
		this.field_12818 = arg.readShort();
		this.field_12817 = arg.readByte();
		this.field_12820 = arg.readShort();
		this.field_12815 = arg.method_10818(class_1713.class);
		this.field_12816 = arg.method_10819();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12819);
		arg.writeShort(this.field_12818);
		arg.writeByte(this.field_12817);
		arg.writeShort(this.field_12820);
		arg.method_10817(this.field_12815);
		arg.method_10793(this.field_12816);
	}

	public int method_12194() {
		return this.field_12819;
	}

	public int method_12192() {
		return this.field_12818;
	}

	public int method_12193() {
		return this.field_12817;
	}

	public short method_12189() {
		return this.field_12820;
	}

	public class_1799 method_12190() {
		return this.field_12816;
	}

	public class_1713 method_12195() {
		return this.field_12815;
	}
}
