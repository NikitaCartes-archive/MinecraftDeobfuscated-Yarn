package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Pattern;

public class class_2522 {
	public static final SimpleCommandExceptionType field_11602 = new SimpleCommandExceptionType(new class_2588("argument.nbt.trailing"));
	public static final SimpleCommandExceptionType field_11608 = new SimpleCommandExceptionType(new class_2588("argument.nbt.expected.key"));
	public static final SimpleCommandExceptionType field_11605 = new SimpleCommandExceptionType(new class_2588("argument.nbt.expected.value"));
	public static final Dynamic2CommandExceptionType field_11603 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("argument.nbt.list.mixed", object, object2)
	);
	public static final Dynamic2CommandExceptionType field_11597 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("argument.nbt.array.mixed", object, object2)
	);
	public static final DynamicCommandExceptionType field_11604 = new DynamicCommandExceptionType(object -> new class_2588("argument.nbt.array.invalid", object));
	private static final Pattern field_11607 = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
	private static final Pattern field_11600 = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
	private static final Pattern field_11596 = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
	private static final Pattern field_11606 = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
	private static final Pattern field_11609 = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
	private static final Pattern field_11601 = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
	private static final Pattern field_11599 = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
	private final StringReader field_11598;

	public static class_2487 method_10718(String string) throws CommandSyntaxException {
		return new class_2522(new StringReader(string)).method_10721();
	}

	@VisibleForTesting
	class_2487 method_10721() throws CommandSyntaxException {
		class_2487 lv = this.method_10727();
		this.field_11598.skipWhitespace();
		if (this.field_11598.canRead()) {
			throw field_11602.createWithContext(this.field_11598);
		} else {
			return lv;
		}
	}

	public class_2522(StringReader stringReader) {
		this.field_11598 = stringReader;
	}

	protected String method_10725() throws CommandSyntaxException {
		this.field_11598.skipWhitespace();
		if (!this.field_11598.canRead()) {
			throw field_11608.createWithContext(this.field_11598);
		} else {
			return this.field_11598.readString();
		}
	}

	protected class_2520 method_10722() throws CommandSyntaxException {
		this.field_11598.skipWhitespace();
		int i = this.field_11598.getCursor();
		if (this.field_11598.peek() == '"') {
			return new class_2519(this.field_11598.readQuotedString());
		} else {
			String string = this.field_11598.readUnquotedString();
			if (string.isEmpty()) {
				this.field_11598.setCursor(i);
				throw field_11605.createWithContext(this.field_11598);
			} else {
				return this.method_10731(string);
			}
		}
	}

	private class_2520 method_10731(String string) {
		try {
			if (field_11596.matcher(string).matches()) {
				return new class_2494(Float.parseFloat(string.substring(0, string.length() - 1)));
			}

			if (field_11606.matcher(string).matches()) {
				return new class_2481(Byte.parseByte(string.substring(0, string.length() - 1)));
			}

			if (field_11609.matcher(string).matches()) {
				return new class_2503(Long.parseLong(string.substring(0, string.length() - 1)));
			}

			if (field_11601.matcher(string).matches()) {
				return new class_2516(Short.parseShort(string.substring(0, string.length() - 1)));
			}

			if (field_11599.matcher(string).matches()) {
				return new class_2497(Integer.parseInt(string));
			}

			if (field_11600.matcher(string).matches()) {
				return new class_2489(Double.parseDouble(string.substring(0, string.length() - 1)));
			}

			if (field_11607.matcher(string).matches()) {
				return new class_2489(Double.parseDouble(string));
			}

			if ("true".equalsIgnoreCase(string)) {
				return new class_2481((byte)1);
			}

			if ("false".equalsIgnoreCase(string)) {
				return new class_2481((byte)0);
			}
		} catch (NumberFormatException var3) {
		}

		return new class_2519(string);
	}

	public class_2520 method_10723() throws CommandSyntaxException {
		this.field_11598.skipWhitespace();
		if (!this.field_11598.canRead()) {
			throw field_11605.createWithContext(this.field_11598);
		} else {
			char c = this.field_11598.peek();
			if (c == '{') {
				return this.method_10727();
			} else {
				return c == '[' ? this.method_10717() : this.method_10722();
			}
		}
	}

	protected class_2520 method_10717() throws CommandSyntaxException {
		return this.field_11598.canRead(3) && this.field_11598.peek(1) != '"' && this.field_11598.peek(2) == ';' ? this.method_10726() : this.method_10729();
	}

	public class_2487 method_10727() throws CommandSyntaxException {
		this.method_10719('{');
		class_2487 lv = new class_2487();
		this.field_11598.skipWhitespace();

		while (this.field_11598.canRead() && this.field_11598.peek() != '}') {
			int i = this.field_11598.getCursor();
			String string = this.method_10725();
			if (string.isEmpty()) {
				this.field_11598.setCursor(i);
				throw field_11608.createWithContext(this.field_11598);
			}

			this.method_10719(':');
			lv.method_10566(string, this.method_10723());
			if (!this.method_10716()) {
				break;
			}

			if (!this.field_11598.canRead()) {
				throw field_11608.createWithContext(this.field_11598);
			}
		}

		this.method_10719('}');
		return lv;
	}

	private class_2520 method_10729() throws CommandSyntaxException {
		this.method_10719('[');
		this.field_11598.skipWhitespace();
		if (!this.field_11598.canRead()) {
			throw field_11605.createWithContext(this.field_11598);
		} else {
			class_2499 lv = new class_2499();
			int i = -1;

			while (this.field_11598.peek() != ']') {
				int j = this.field_11598.getCursor();
				class_2520 lv2 = this.method_10723();
				int k = lv2.method_10711();
				if (i < 0) {
					i = k;
				} else if (k != i) {
					this.field_11598.setCursor(j);
					throw field_11603.createWithContext(this.field_11598, class_2520.method_10712(k), class_2520.method_10712(i));
				}

				lv.method_10606(lv2);
				if (!this.method_10716()) {
					break;
				}

				if (!this.field_11598.canRead()) {
					throw field_11605.createWithContext(this.field_11598);
				}
			}

			this.method_10719(']');
			return lv;
		}
	}

	private class_2520 method_10726() throws CommandSyntaxException {
		this.method_10719('[');
		int i = this.field_11598.getCursor();
		char c = this.field_11598.read();
		this.field_11598.read();
		this.field_11598.skipWhitespace();
		if (!this.field_11598.canRead()) {
			throw field_11605.createWithContext(this.field_11598);
		} else if (c == 'B') {
			return new class_2479(this.method_10728((byte)7, (byte)1));
		} else if (c == 'L') {
			return new class_2501(this.method_10728((byte)12, (byte)4));
		} else if (c == 'I') {
			return new class_2495(this.method_10728((byte)11, (byte)3));
		} else {
			this.field_11598.setCursor(i);
			throw field_11604.createWithContext(this.field_11598, String.valueOf(c));
		}
	}

	private <T extends Number> List<T> method_10728(byte b, byte c) throws CommandSyntaxException {
		List<T> list = Lists.<T>newArrayList();

		while (this.field_11598.peek() != ']') {
			int i = this.field_11598.getCursor();
			class_2520 lv = this.method_10723();
			int j = lv.method_10711();
			if (j != c) {
				this.field_11598.setCursor(i);
				throw field_11597.createWithContext(this.field_11598, class_2520.method_10712(j), class_2520.method_10712(b));
			}

			if (c == 1) {
				list.add(((class_2514)lv).method_10698());
			} else if (c == 4) {
				list.add(((class_2514)lv).method_10699());
			} else {
				list.add(((class_2514)lv).method_10701());
			}

			if (!this.method_10716()) {
				break;
			}

			if (!this.field_11598.canRead()) {
				throw field_11605.createWithContext(this.field_11598);
			}
		}

		this.method_10719(']');
		return list;
	}

	private boolean method_10716() {
		this.field_11598.skipWhitespace();
		if (this.field_11598.canRead() && this.field_11598.peek() == ',') {
			this.field_11598.skip();
			this.field_11598.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	private void method_10719(char c) throws CommandSyntaxException {
		this.field_11598.skipWhitespace();
		this.field_11598.expect(c);
	}
}
