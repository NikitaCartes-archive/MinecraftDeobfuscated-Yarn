package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2877 implements class_2596<class_2792> {
	private class_2338 field_13101;
	private String[] field_13100;

	public class_2877() {
	}

	@Environment(EnvType.CLIENT)
	public class_2877(class_2338 arg, class_2561 arg2, class_2561 arg3, class_2561 arg4, class_2561 arg5) {
		this.field_13101 = arg;
		this.field_13100 = new String[]{arg2.getString(), arg3.getString(), arg4.getString(), arg5.getString()};
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13101 = arg.method_10811();
		this.field_13100 = new String[4];

		for (int i = 0; i < 4; i++) {
			this.field_13100[i] = arg.method_10800(384);
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_13101);

		for (int i = 0; i < 4; i++) {
			arg.method_10814(this.field_13100[i]);
		}
	}

	public void method_12509(class_2792 arg) {
		arg.method_12071(this);
	}

	public class_2338 method_12510() {
		return this.field_13101;
	}

	public String[] method_12508() {
		return this.field_13100;
	}
}
