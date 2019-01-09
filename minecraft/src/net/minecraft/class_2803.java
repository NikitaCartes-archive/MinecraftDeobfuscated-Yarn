package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2803 implements class_2596<class_2792> {
	private String field_12777;
	private int field_12780;
	private class_1657.class_1659 field_12781;
	private boolean field_12779;
	private int field_12778;
	private class_1306 field_12782;

	public class_2803() {
	}

	@Environment(EnvType.CLIENT)
	public class_2803(String string, int i, class_1657.class_1659 arg, boolean bl, int j, class_1306 arg2) {
		this.field_12777 = string;
		this.field_12780 = i;
		this.field_12781 = arg;
		this.field_12779 = bl;
		this.field_12778 = j;
		this.field_12782 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12777 = arg.method_10800(16);
		this.field_12780 = arg.readByte();
		this.field_12781 = arg.method_10818(class_1657.class_1659.class);
		this.field_12779 = arg.readBoolean();
		this.field_12778 = arg.readUnsignedByte();
		this.field_12782 = arg.method_10818(class_1306.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_12777);
		arg.writeByte(this.field_12780);
		arg.method_10817(this.field_12781);
		arg.writeBoolean(this.field_12779);
		arg.writeByte(this.field_12778);
		arg.method_10817(this.field_12782);
	}

	public void method_12133(class_2792 arg) {
		arg.method_12069(this);
	}

	public String method_12131() {
		return this.field_12777;
	}

	public class_1657.class_1659 method_12134() {
		return this.field_12781;
	}

	public boolean method_12135() {
		return this.field_12779;
	}

	public int method_12136() {
		return this.field_12778;
	}

	public class_1306 method_12132() {
		return this.field_12782;
	}
}
