package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2720 implements class_2596<class_2602> {
	private String field_12427;
	private String field_12428;

	public class_2720() {
	}

	public class_2720(String string, String string2) {
		this.field_12427 = string;
		this.field_12428 = string2;
		if (string2.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12427 = arg.method_10800(32767);
		this.field_12428 = arg.method_10800(40);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_12427);
		arg.method_10814(this.field_12428);
	}

	public void method_11774(class_2602 arg) {
		arg.method_11141(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_11772() {
		return this.field_12427;
	}

	@Environment(EnvType.CLIENT)
	public String method_11773() {
		return this.field_12428;
	}
}
