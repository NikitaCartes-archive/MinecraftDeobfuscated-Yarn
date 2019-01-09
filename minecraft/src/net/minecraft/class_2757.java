package net.minecraft;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2757 implements class_2596<class_2602> {
	private String field_12610 = "";
	@Nullable
	private String field_12613;
	private int field_12611;
	private class_2995.class_2996 field_12612;

	public class_2757() {
	}

	public class_2757(class_2995.class_2996 arg, @Nullable String string, String string2, int i) {
		if (arg != class_2995.class_2996.field_13430 && string == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.field_12610 = string2;
			this.field_12613 = string;
			this.field_12611 = i;
			this.field_12612 = arg;
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12610 = arg.method_10800(40);
		this.field_12612 = arg.method_10818(class_2995.class_2996.class);
		String string = arg.method_10800(16);
		this.field_12613 = Objects.equals(string, "") ? null : string;
		if (this.field_12612 != class_2995.class_2996.field_13430) {
			this.field_12611 = arg.method_10816();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_12610);
		arg.method_10817(this.field_12612);
		arg.method_10814(this.field_12613 == null ? "" : this.field_12613);
		if (this.field_12612 != class_2995.class_2996.field_13430) {
			arg.method_10804(this.field_12611);
		}
	}

	public void method_11866(class_2602 arg) {
		arg.method_11118(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_11862() {
		return this.field_12610;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11864() {
		return this.field_12613;
	}

	@Environment(EnvType.CLIENT)
	public int method_11865() {
		return this.field_12611;
	}

	@Environment(EnvType.CLIENT)
	public class_2995.class_2996 method_11863() {
		return this.field_12612;
	}
}
