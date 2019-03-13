package net.minecraft.sortme;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableTextComponent;

public class JsonLikeTagParser {
	public static final SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.nbt.trailing"));
	public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.nbt.expected.key"));
	public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.nbt.expected.value"));
	public static final Dynamic2CommandExceptionType LIST_MIXED = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.nbt.list.mixed", object, object2)
	);
	public static final Dynamic2CommandExceptionType ARRAY_MIXED = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.nbt.array.mixed", object, object2)
	);
	public static final DynamicCommandExceptionType ARRAY_INVALID = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.nbt.array.invalid", object)
	);
	private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
	private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
	private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
	private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
	private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
	private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
	private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
	private final StringReader reader;

	public static CompoundTag parse(String string) throws CommandSyntaxException {
		return new JsonLikeTagParser(new StringReader(string)).readCompoundTag();
	}

	@VisibleForTesting
	CompoundTag readCompoundTag() throws CommandSyntaxException {
		CompoundTag compoundTag = this.parseCompoundTag();
		this.reader.skipWhitespace();
		if (this.reader.canRead()) {
			throw TRAILING.createWithContext(this.reader);
		} else {
			return compoundTag;
		}
	}

	public JsonLikeTagParser(StringReader stringReader) {
		this.reader = stringReader;
	}

	protected String readString() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_KEY.createWithContext(this.reader);
		} else {
			return this.reader.readString();
		}
	}

	protected Tag parseTagPrimitive() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		int i = this.reader.getCursor();
		if (StringReader.isQuotedStringStart(this.reader.peek())) {
			return new StringTag(this.reader.readQuotedString());
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

	private Tag parsePrimitive(String string) {
		try {
			if (FLOAT_PATTERN.matcher(string).matches()) {
				return new FloatTag(Float.parseFloat(string.substring(0, string.length() - 1)));
			}

			if (BYTE_PATTERN.matcher(string).matches()) {
				return new ByteTag(Byte.parseByte(string.substring(0, string.length() - 1)));
			}

			if (LONG_PATTERN.matcher(string).matches()) {
				return new LongTag(Long.parseLong(string.substring(0, string.length() - 1)));
			}

			if (SHORT_PATTERN.matcher(string).matches()) {
				return new ShortTag(Short.parseShort(string.substring(0, string.length() - 1)));
			}

			if (INT_PATTERN.matcher(string).matches()) {
				return new IntTag(Integer.parseInt(string));
			}

			if (DOUBLE_PATTERN.matcher(string).matches()) {
				return new DoubleTag(Double.parseDouble(string.substring(0, string.length() - 1)));
			}

			if (DOUBLE_PATTERN_IMPLICIT.matcher(string).matches()) {
				return new DoubleTag(Double.parseDouble(string));
			}

			if ("true".equalsIgnoreCase(string)) {
				return new ByteTag((byte)1);
			}

			if ("false".equalsIgnoreCase(string)) {
				return new ByteTag((byte)0);
			}
		} catch (NumberFormatException var3) {
		}

		return new StringTag(string);
	}

	public Tag parseTag() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE.createWithContext(this.reader);
		} else {
			char c = this.reader.peek();
			if (c == '{') {
				return this.parseCompoundTag();
			} else {
				return c == '[' ? this.parseTagArray() : this.parseTagPrimitive();
			}
		}
	}

	protected Tag parseTagArray() throws CommandSyntaxException {
		return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';'
			? this.parseTagPrimitiveArray()
			: this.parseListTag();
	}

	public CompoundTag parseCompoundTag() throws CommandSyntaxException {
		this.expect('{');
		CompoundTag compoundTag = new CompoundTag();
		this.reader.skipWhitespace();

		while (this.reader.canRead() && this.reader.peek() != '}') {
			int i = this.reader.getCursor();
			String string = this.readString();
			if (string.isEmpty()) {
				this.reader.setCursor(i);
				throw EXPECTED_KEY.createWithContext(this.reader);
			}

			this.expect(':');
			compoundTag.method_10566(string, this.parseTag());
			if (!this.method_10716()) {
				break;
			}

			if (!this.reader.canRead()) {
				throw EXPECTED_KEY.createWithContext(this.reader);
			}
		}

		this.expect('}');
		return compoundTag;
	}

	private Tag parseListTag() throws CommandSyntaxException {
		this.expect('[');
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE.createWithContext(this.reader);
		} else {
			ListTag listTag = new ListTag();
			int i = -1;

			while (this.reader.peek() != ']') {
				int j = this.reader.getCursor();
				Tag tag = this.parseTag();
				int k = tag.getType();
				if (i < 0) {
					i = k;
				} else if (k != i) {
					this.reader.setCursor(j);
					throw LIST_MIXED.createWithContext(this.reader, Tag.idToString(k), Tag.idToString(i));
				}

				listTag.add(tag);
				if (!this.method_10716()) {
					break;
				}

				if (!this.reader.canRead()) {
					throw EXPECTED_VALUE.createWithContext(this.reader);
				}
			}

			this.expect(']');
			return listTag;
		}
	}

	private Tag parseTagPrimitiveArray() throws CommandSyntaxException {
		this.expect('[');
		int i = this.reader.getCursor();
		char c = this.reader.read();
		this.reader.read();
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE.createWithContext(this.reader);
		} else if (c == 'B') {
			return new ByteArrayTag(this.method_10728((byte)7, (byte)1));
		} else if (c == 'L') {
			return new LongArrayTag(this.method_10728((byte)12, (byte)4));
		} else if (c == 'I') {
			return new IntArrayTag(this.method_10728((byte)11, (byte)3));
		} else {
			this.reader.setCursor(i);
			throw ARRAY_INVALID.createWithContext(this.reader, String.valueOf(c));
		}
	}

	private <T extends Number> List<T> method_10728(byte b, byte c) throws CommandSyntaxException {
		List<T> list = Lists.<T>newArrayList();

		while (this.reader.peek() != ']') {
			int i = this.reader.getCursor();
			Tag tag = this.parseTag();
			int j = tag.getType();
			if (j != c) {
				this.reader.setCursor(i);
				throw ARRAY_MIXED.createWithContext(this.reader, Tag.idToString(j), Tag.idToString(b));
			}

			if (c == 1) {
				list.add(((AbstractNumberTag)tag).getByte());
			} else if (c == 4) {
				list.add(((AbstractNumberTag)tag).getLong());
			} else {
				list.add(((AbstractNumberTag)tag).getInt());
			}

			if (!this.method_10716()) {
				break;
			}

			if (!this.reader.canRead()) {
				throw EXPECTED_VALUE.createWithContext(this.reader);
			}
		}

		this.expect(']');
		return list;
	}

	private boolean method_10716() {
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
