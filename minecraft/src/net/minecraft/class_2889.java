package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2889 implements class_2596<class_2890> {
	private int field_13158;
	private String field_13159;
	private int field_13157;
	private class_2539 field_13156;

	public class_2889() {
	}

	@Environment(EnvType.CLIENT)
	public class_2889(String string, int i, class_2539 arg) {
		this.field_13158 = class_155.method_16673().getProtocolVersion();
		this.field_13159 = string;
		this.field_13157 = i;
		this.field_13156 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13158 = arg.method_10816();
		this.field_13159 = arg.method_10800(255);
		this.field_13157 = arg.readUnsignedShort();
		this.field_13156 = class_2539.method_10782(arg.method_10816());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13158);
		arg.method_10814(this.field_13159);
		arg.writeShort(this.field_13157);
		arg.method_10804(this.field_13156.method_10785());
	}

	public void method_12575(class_2890 arg) {
		arg.method_12576(this);
	}

	public class_2539 method_12573() {
		return this.field_13156;
	}

	public int method_12574() {
		return this.field_13158;
	}
}
