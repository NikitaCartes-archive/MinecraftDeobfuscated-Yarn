package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class StringTag implements Tag {
	public static final TagReader<StringTag> READER = new TagReader<StringTag>() {
		public StringTag method_23257(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(288L);
			String string = dataInput.readUTF();
			positionTracker.add((long)(16 * string.length()));
			return StringTag.of(string);
		}

		@Override
		public String getCrashReportName() {
			return "STRING";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_String";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private static final StringTag EMPTY = new StringTag("");
	private final String value;

	private StringTag(String string) {
		Objects.requireNonNull(string, "Null string not allowed");
		this.value = string;
	}

	public static StringTag of(String string) {
		return string.isEmpty() ? EMPTY : new StringTag(string);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeUTF(this.value);
	}

	@Override
	public byte getType() {
		return 8;
	}

	@Override
	public TagReader<StringTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return escape(this.value);
	}

	public StringTag method_10705() {
		return this;
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof StringTag && Objects.equals(this.value, ((StringTag)object).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public Text toText(String string, int i) {
		String string2 = escape(this.value);
		String string3 = string2.substring(0, 1);
		Text text = new LiteralText(string2.substring(1, string2.length() - 1)).formatted(GREEN);
		return new LiteralText(string3).append(text).append(string3);
	}

	public static String escape(String string) {
		StringBuilder stringBuilder = new StringBuilder(" ");
		char c = 0;

		for (int i = 0; i < string.length(); i++) {
			char d = string.charAt(i);
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
