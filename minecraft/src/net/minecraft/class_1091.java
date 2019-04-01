package net.minecraft;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1091 extends class_2960 {
	private final String field_5406;

	protected class_1091(String[] strings) {
		super(strings);
		this.field_5406 = strings[2].toLowerCase(Locale.ROOT);
	}

	public class_1091(String string) {
		this(method_4741(string));
	}

	public class_1091(class_2960 arg, String string) {
		this(arg.toString(), string);
	}

	public class_1091(String string, String string2) {
		this(method_4741(string + '#' + string2));
	}

	protected static String[] method_4741(String string) {
		String[] strings = new String[]{null, string, ""};
		int i = string.indexOf(35);
		String string2 = string;
		if (i >= 0) {
			strings[2] = string.substring(i + 1, string.length());
			if (i > 1) {
				string2 = string.substring(0, i);
			}
		}

		System.arraycopy(class_2960.method_12830(string2, ':'), 0, strings, 0, 2);
		return strings;
	}

	public String method_4740() {
		return this.field_5406;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_1091 && super.equals(object)) {
			class_1091 lv = (class_1091)object;
			return this.field_5406.equals(lv.field_5406);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + this.field_5406.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() + '#' + this.field_5406;
	}
}
