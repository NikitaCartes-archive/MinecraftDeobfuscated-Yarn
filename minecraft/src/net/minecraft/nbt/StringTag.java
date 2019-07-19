package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class StringTag implements Tag {
	private String value;

	public StringTag() {
		this("");
	}

	public StringTag(String string) {
		Objects.requireNonNull(string, "Null string not allowed");
		this.value = string;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(288L);
		this.value = input.readUTF();
		positionTracker.add((long)(16 * this.value.length()));
	}

	@Override
	public byte getType() {
		return 8;
	}

	@Override
	public String toString() {
		return escape(this.value);
	}

	public StringTag copy() {
		return new StringTag(this.value);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof StringTag && Objects.equals(this.value, ((StringTag)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public Text toText(String indent, int depth) {
		String string = escape(this.value);
		String string2 = string.substring(0, 1);
		Text text = new LiteralText(string.substring(1, string.length() - 1)).formatted(GREEN);
		return new LiteralText(string2).append(text).append(string2);
	}

	public static String escape(String value) {
		StringBuilder stringBuilder = new StringBuilder(" ");
		char c = 0;

		for (int i = 0; i < value.length(); i++) {
			char d = value.charAt(i);
			if (d == '\\') {
				stringBuilder.append('\\');
			} else if (d == '"' || d == '\'') {
				if (c == 0) {
					c = (char)(d == '"' ? 39 : 34);
				}

				if (c == d) {
					stringBuilder.append('\\');
				}
			}

			stringBuilder.append(d);
		}

		if (c == 0) {
			c = '"';
		}

		stringBuilder.setCharAt(0, c);
		stringBuilder.append(c);
		return stringBuilder.toString();
	}
}
