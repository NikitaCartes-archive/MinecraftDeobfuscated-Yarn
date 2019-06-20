package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2751 implements class_2596<class_2602> {
	private String field_12589;
	private class_2561 field_12591;
	private class_274.class_275 field_12592;
	private int field_12590;

	public class_2751() {
	}

	public class_2751(class_266 arg, int i) {
		this.field_12589 = arg.method_1113();
		this.field_12591 = arg.method_1114();
		this.field_12592 = arg.method_1118();
		this.field_12590 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12589 = arg.method_10800(16);
		this.field_12590 = arg.readByte();
		if (this.field_12590 == 0 || this.field_12590 == 2) {
			this.field_12591 = arg.method_10808();
			this.field_12592 = arg.method_10818(class_274.class_275.class);
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_12589);
		arg.writeByte(this.field_12590);
		if (this.field_12590 == 0 || this.field_12590 == 2) {
			arg.method_10805(this.field_12591);
			arg.method_10817(this.field_12592);
		}
	}

	public void method_11838(class_2602 arg) {
		arg.method_11144(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_11835() {
		return this.field_12589;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11836() {
		return this.field_12591;
	}

	@Environment(EnvType.CLIENT)
	public int method_11837() {
		return this.field_12590;
	}

	@Environment(EnvType.CLIENT)
	public class_274.class_275 method_11839() {
		return this.field_12592;
	}
}
