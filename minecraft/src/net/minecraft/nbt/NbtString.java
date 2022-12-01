package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Objects;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.util.Util;

/**
 * Represents an NBT string. Its type is {@value NbtElement#STRING_TYPE}.
 * Instances are immutable.
 */
public class NbtString implements NbtElement {
	private static final int SIZE = 36;
	public static final NbtType<NbtString> TYPE = new NbtType.OfVariableSize<NbtString>() {
		public NbtString read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(36L);
			String string = dataInput.readUTF();
			nbtTagSizeTracker.add((long)(2 * string.length()));
			return NbtString.of(string);
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
			return visitor.visitString(input.readUTF());
		}

		@Override
		public void skip(DataInput input) throws IOException {
			NbtString.skip(input);
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
	private static final NbtString EMPTY = new NbtString("");
	private static final char DOUBLE_QUOTE = '"';
	private static final char SINGLE_QUOTE = '\'';
	private static final char BACKSLASH = '\\';
	private static final char NULL = '\u0000';
	private final String value;

	public static void skip(DataInput input) throws IOException {
		input.skipBytes(input.readUnsignedShort());
	}

	private NbtString(String value) {
		Objects.requireNonNull(value, "Null string not allowed");
		this.value = value;
	}

	/**
	 * {@return the NBT string from {@code value}}
	 */
	public static NbtString of(String value) {
		return value.isEmpty() ? EMPTY : new NbtString(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		try {
			output.writeUTF(this.value);
		} catch (UTFDataFormatException var3) {
			Util.error("Failed to write NBT String", var3);
			output.writeUTF("");
		}
	}

	@Override
	public int getSizeInBytes() {
		return 36 + 2 * this.value.length();
	}

	@Override
	public byte getType() {
		return NbtElement.STRING_TYPE;
	}

	@Override
	public NbtType<NbtString> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return NbtElement.super.asString();
	}

	public NbtString copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtString && Objects.equals(this.value, ((NbtString)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitString(this);
	}

	/**
	 * {@return the string quoted with quotes and backslashes escaped}
	 * 
	 * @implNote If {@code value} contains one of the singlequote or the double quote,
	 * it tries to use the other quotes to quote the string. If both appear, then the quote
	 * that appeared later will be used to quote the string. If neither of them appears, this
	 * uses a double quote. For example, the string {@code It's a "Tiny Potato"!} will be
	 * escaped as {@code "It's a \"Tiny Potato\"!"}, while the string
	 * {@code It is a "Tiny Potato"!} will be escaped as {@code 'It is a "Tiny Potato"!'}.
	 */
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

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		return visitor.visitString(this.value);
	}
}
