package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class class_2519 implements class_2520 {
	private String field_11590;

	public class_2519() {
		this("");
	}

	public class_2519(String string) {
		Objects.requireNonNull(string, "Null string not allowed");
		this.field_11590 = string;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeUTF(this.field_11590);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(288L);
		this.field_11590 = dataInput.readUTF();
		arg.method_10623((long)(16 * this.field_11590.length()));
	}

	@Override
	public byte method_10711() {
		return 8;
	}

	@Override
	public String toString() {
		return method_10706(this.field_11590, true);
	}

	public class_2519 method_10705() {
		return new class_2519(this.field_11590);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2519 && Objects.equals(this.field_11590, ((class_2519)object).field_11590);
	}

	public int hashCode() {
		return this.field_11590.hashCode();
	}

	@Override
	public String method_10714() {
		return this.field_11590;
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585(method_10706(this.field_11590, false)).method_10854(field_11594);
		return new class_2585("\"").method_10852(lv).method_10864("\"");
	}

	public static String method_10706(String string, boolean bl) {
		StringBuilder stringBuilder = new StringBuilder();
		if (bl) {
			stringBuilder.append('"');
		}

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\\' || c == '"') {
				stringBuilder.append('\\');
			}

			stringBuilder.append(c);
		}

		if (bl) {
			stringBuilder.append('"');
		}

		return stringBuilder.toString();
	}
}
