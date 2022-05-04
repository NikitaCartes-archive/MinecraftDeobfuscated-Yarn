package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.text.Text;

/**
 * A class for reading a stringified NBT.
 * 
 * @apiNote Methods in this class throw {@code CommandSyntaxException} to indicate
 * syntax errors within the NBT representation.
 */
public class StringNbtReader {
	public static final SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType(Text.translatable("argument.nbt.trailing"));
	public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.key"));
	public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.value"));
	public static final Dynamic2CommandExceptionType LIST_MIXED = new Dynamic2CommandExceptionType(
		(receivedType, expectedType) -> Text.translatable("argument.nbt.list.mixed", receivedType, expectedType)
	);
	public static final Dynamic2CommandExceptionType ARRAY_MIXED = new Dynamic2CommandExceptionType(
		(receivedType, expectedType) -> Text.translatable("argument.nbt.array.mixed", receivedType, expectedType)
	);
	public static final DynamicCommandExceptionType ARRAY_INVALID = new DynamicCommandExceptionType(type -> Text.translatable("argument.nbt.array.invalid", type));
	public static final char COMMA = ',';
	public static final char COLON = ':';
	private static final char SQUARE_OPEN_BRACKET = '[';
	private static final char SQUARE_CLOSE_BRACKET = ']';
	private static final char RIGHT_CURLY_BRACKET = '}';
	private static final char LEFT_CURLY_BRACKET = '{';
	private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
	private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
	private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
	private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
	private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
	private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
	private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
	private final StringReader reader;

	/**
	 * {@return the NBT compound parsed from the {@code string}}
	 * 
	 * @throws CommandSyntaxException if the reader detects a syntax error (including
	 * {@linkplain #TRAILING trailing strings})
	 */
	public static NbtCompound parse(String string) throws CommandSyntaxException {
		return new StringNbtReader(new StringReader(string)).readCompound();
	}

	@VisibleForTesting
	NbtCompound readCompound() throws CommandSyntaxException {
		NbtCompound nbtCompound = this.parseCompound();
		this.reader.skipWhitespace();
		if (this.reader.canRead()) {
			throw TRAILING.createWithContext(this.reader);
		} else {
			return nbtCompound;
		}
	}

	public StringNbtReader(StringReader reader) {
		this.reader = reader;
	}

	protected String readString() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_KEY.createWithContext(this.reader);
		} else {
			return this.reader.readString();
		}
	}

	protected NbtElement parseElementPrimitive() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		int i = this.reader.getCursor();
		if (StringReader.isQuotedStringStart(this.reader.peek())) {
			return NbtString.of(this.reader.readQuotedString());
		} else {
			String string = this.reader.readUnquotedString();
			if (string.isEmpty()) {
				this.reader.setCursor(i);
				throw EXPECTED_VALUE.createWithContext(this.reader);
			} else {
				return this.parsePrimitive(string);
			}
		}
	}

	private NbtElement parsePrimitive(String input) {
		try {
			if (FLOAT_PATTERN.matcher(input).matches()) {
				return NbtFloat.of(Float.parseFloat(input.substring(0, input.length() - 1)));
			}

			if (BYTE_PATTERN.matcher(input).matches()) {
				return NbtByte.of(Byte.parseByte(input.substring(0, input.length() - 1)));
			}

			if (LONG_PATTERN.matcher(input).matches()) {
				return NbtLong.of(Long.parseLong(input.substring(0, input.length() - 1)));
			}

			if (SHORT_PATTERN.matcher(input).matches()) {
				return NbtShort.of(Short.parseShort(input.substring(0, input.length() - 1)));
			}

			if (INT_PATTERN.matcher(input).matches()) {
				return NbtInt.of(Integer.parseInt(input));
			}

			if (DOUBLE_PATTERN.matcher(input).matches()) {
				return NbtDouble.of(Double.parseDouble(input.substring(0, input.length() - 1)));
			}

			if (DOUBLE_PATTERN_IMPLICIT.matcher(input).matches()) {
				return NbtDouble.of(Double.parseDouble(input));
			}

			if ("true".equalsIgnoreCase(input)) {
				return NbtByte.ONE;
			}

			if ("false".equalsIgnoreCase(input)) {
				return NbtByte.ZERO;
			}
		} catch (NumberFormatException var3) {
		}

		return NbtString.of(input);
	}

	/**
	 * {@return the parsed NBT element}
	 * 
	 * @throws CommandSyntaxException if the reader detects a syntax error
	 */
	public NbtElement parseElement() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE.createWithContext(this.reader);
		} else {
			char c = this.reader.peek();
			if (c == '{') {
				return this.parseCompound();
			} else {
				return c == '[' ? this.parseArray() : this.parseElementPrimitive();
			}
		}
	}

	protected NbtElement parseArray() throws CommandSyntaxException {
		return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';'
			? this.parseElementPrimitiveArray()
			: this.parseList();
	}

	/**
	 * {@return the parsed NBT compound}
	 * 
	 * @throws CommandSyntaxException if the reader detects a syntax error
	 */
	public NbtCompound parseCompound() throws CommandSyntaxException {
		this.expect('{');
		NbtCompound nbtCompound = new NbtCompound();
		this.reader.skipWhitespace();

		while (this.reader.canRead() && this.reader.peek() != '}') {
			int i = this.reader.getCursor();
			String string = this.readString();
			if (string.isEmpty()) {
				this.reader.setCursor(i);
				throw EXPECTED_KEY.createWithContext(this.reader);
			}

			this.expect(':');
			nbtCompound.put(string, this.parseElement());
			if (!this.readComma()) {
				break;
			}

			if (!this.reader.canRead()) {
				throw EXPECTED_KEY.createWithContext(this.reader);
			}
		}

		this.expect('}');
		return nbtCompound;
	}

	private NbtElement parseList() throws CommandSyntaxException {
		this.expect('[');
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE.createWithContext(this.reader);
		} else {
			NbtList nbtList = new NbtList();
			NbtType<?> nbtType = null;

			while (this.reader.peek() != ']') {
				int i = this.reader.getCursor();
				NbtElement nbtElement = this.parseElement();
				NbtType<?> nbtType2 = nbtElement.getNbtType();
				if (nbtType == null) {
					nbtType = nbtType2;
				} else if (nbtType2 != nbtType) {
					this.reader.setCursor(i);
					throw LIST_MIXED.createWithContext(this.reader, nbtType2.getCommandFeedbackName(), nbtType.getCommandFeedbackName());
				}

				nbtList.add(nbtElement);
				if (!this.readComma()) {
					break;
				}

				if (!this.reader.canRead()) {
					throw EXPECTED_VALUE.createWithContext(this.reader);
				}
			}

			this.expect(']');
			return nbtList;
		}
	}

	private NbtElement parseElementPrimitiveArray() throws CommandSyntaxException {
		this.expect('[');
		int i = this.reader.getCursor();
		char c = this.reader.read();
		this.reader.read();
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE.createWithContext(this.reader);
		} else if (c == 'B') {
			return new NbtByteArray(this.readArray(NbtByteArray.TYPE, NbtByte.TYPE));
		} else if (c == 'L') {
			return new NbtLongArray(this.readArray(NbtLongArray.TYPE, NbtLong.TYPE));
		} else if (c == 'I') {
			return new NbtIntArray(this.readArray(NbtIntArray.TYPE, NbtInt.TYPE));
		} else {
			this.reader.setCursor(i);
			throw ARRAY_INVALID.createWithContext(this.reader, String.valueOf(c));
		}
	}

	private <T extends Number> List<T> readArray(NbtType<?> arrayTypeReader, NbtType<?> typeReader) throws CommandSyntaxException {
		List<T> list = Lists.<T>newArrayList();

		while (this.reader.peek() != ']') {
			int i = this.reader.getCursor();
			NbtElement nbtElement = this.parseElement();
			NbtType<?> nbtType = nbtElement.getNbtType();
			if (nbtType != typeReader) {
				this.reader.setCursor(i);
				throw ARRAY_MIXED.createWithContext(this.reader, nbtType.getCommandFeedbackName(), arrayTypeReader.getCommandFeedbackName());
			}

			if (typeReader == NbtByte.TYPE) {
				list.add(((AbstractNbtNumber)nbtElement).byteValue());
			} else if (typeReader == NbtLong.TYPE) {
				list.add(((AbstractNbtNumber)nbtElement).longValue());
			} else {
				list.add(((AbstractNbtNumber)nbtElement).intValue());
			}

			if (!this.readComma()) {
				break;
			}

			if (!this.reader.canRead()) {
				throw EXPECTED_VALUE.createWithContext(this.reader);
			}
		}

		this.expect(']');
		return list;
	}

	private boolean readComma() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == ',') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	private void expect(char c) throws CommandSyntaxException {
		this.reader.skipWhitespace();
		this.reader.expect(c);
	}
}
