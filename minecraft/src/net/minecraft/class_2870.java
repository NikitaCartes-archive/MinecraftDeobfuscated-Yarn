package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2870 implements class_2596<class_2792> {
	private class_2338 field_13065;
	private String field_13064;
	private boolean field_13063;
	private boolean field_13062;
	private boolean field_13061;
	private class_2593.class_2594 field_13060;

	public class_2870() {
	}

	@Environment(EnvType.CLIENT)
	public class_2870(class_2338 arg, String string, class_2593.class_2594 arg2, boolean bl, boolean bl2, boolean bl3) {
		this.field_13065 = arg;
		this.field_13064 = string;
		this.field_13063 = bl;
		this.field_13062 = bl2;
		this.field_13061 = bl3;
		this.field_13060 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13065 = arg.method_10811();
		this.field_13064 = arg.method_10800(32767);
		this.field_13060 = arg.method_10818(class_2593.class_2594.class);
		int i = arg.readByte();
		this.field_13063 = (i & 1) != 0;
		this.field_13062 = (i & 2) != 0;
		this.field_13061 = (i & 4) != 0;
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_13065);
		arg.method_10814(this.field_13064);
		arg.method_10817(this.field_13060);
		int i = 0;
		if (this.field_13063) {
			i |= 1;
		}

		if (this.field_13062) {
			i |= 2;
		}

		if (this.field_13061) {
			i |= 4;
		}

		arg.writeByte(i);
	}

	public void method_12469(class_2792 arg) {
		arg.method_12077(this);
	}

	public class_2338 method_12473() {
		return this.field_13065;
	}

	public String method_12470() {
		return this.field_13064;
	}

	public boolean method_12472() {
		return this.field_13063;
	}

	public boolean method_12471() {
		return this.field_13062;
	}

	public boolean method_12474() {
		return this.field_13061;
	}

	public class_2593.class_2594 method_12468() {
		return this.field_13060;
	}
}
