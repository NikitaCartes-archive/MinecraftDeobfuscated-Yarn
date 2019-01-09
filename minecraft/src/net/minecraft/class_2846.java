package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2846 implements class_2596<class_2792> {
	private class_2338 field_12967;
	private class_2350 field_12965;
	private class_2846.class_2847 field_12966;

	public class_2846() {
	}

	@Environment(EnvType.CLIENT)
	public class_2846(class_2846.class_2847 arg, class_2338 arg2, class_2350 arg3) {
		this.field_12966 = arg;
		this.field_12967 = arg2;
		this.field_12965 = arg3;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12966 = arg.method_10818(class_2846.class_2847.class);
		this.field_12967 = arg.method_10811();
		this.field_12965 = class_2350.method_10143(arg.readUnsignedByte());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12966);
		arg.method_10807(this.field_12967);
		arg.writeByte(this.field_12965.method_10146());
	}

	public void method_12361(class_2792 arg) {
		arg.method_12066(this);
	}

	public class_2338 method_12362() {
		return this.field_12967;
	}

	public class_2350 method_12360() {
		return this.field_12965;
	}

	public class_2846.class_2847 method_12363() {
		return this.field_12966;
	}

	public static enum class_2847 {
		field_12968,
		field_12971,
		field_12973,
		field_12970,
		field_12975,
		field_12974,
		field_12969;
	}
}
