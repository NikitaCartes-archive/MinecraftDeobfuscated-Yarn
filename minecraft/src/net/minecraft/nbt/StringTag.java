package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class StringTag implements Tag {
	public static final TagReader<StringTag> READER = new TagReader<StringTag>() {
		public StringTag read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(288L);
			String string = dataInput.readUTF();
			nbtTagSizeTracker.add((long)(16 * string.length()));
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

	private StringTag(String value) {
		Objects.requireNonNull(value, "Null string not allowed");
		this.value = value;
	}

	public static StringTag of(String value) {
		return value.isEmpty() ? EMPTY : new StringTag(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.value);
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
		return Tag.super.asString();
	}

	public StringTag copy() {
		return this;
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
	public void accept(NbtTagVisitor visitor) {
		visitor.visitStringTag(this);
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
