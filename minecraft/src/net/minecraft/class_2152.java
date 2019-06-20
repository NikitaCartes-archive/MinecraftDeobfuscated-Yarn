package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;

public class class_2152 {
	public static final class_2152 field_9780 = new class_2152(null, null);
	public static final SimpleCommandExceptionType field_9781 = new SimpleCommandExceptionType(new class_2588("argument.range.ints"));
	private final Float field_9778;
	private final Float field_9779;

	public class_2152(@Nullable Float float_, @Nullable Float float2) {
		this.field_9778 = float_;
		this.field_9779 = float2;
	}

	@Nullable
	public Float method_9175() {
		return this.field_9778;
	}

	@Nullable
	public Float method_9177() {
		return this.field_9779;
	}

	public static class_2152 method_9172(StringReader stringReader, boolean bl, Function<Float, Float> function) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw class_2096.field_9700.createWithContext(stringReader);
		} else {
			int i = stringReader.getCursor();
			Float float_ = method_9174(method_9176(stringReader, bl), function);
			Float float2;
			if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
				stringReader.skip();
				stringReader.skip();
				float2 = method_9174(method_9176(stringReader, bl), function);
				if (float_ == null && float2 == null) {
					stringReader.setCursor(i);
					throw class_2096.field_9700.createWithContext(stringReader);
				}
			} else {
				if (!bl && stringReader.canRead() && stringReader.peek() == '.') {
					stringReader.setCursor(i);
					throw field_9781.createWithContext(stringReader);
				}

				float2 = float_;
			}

			if (float_ == null && float2 == null) {
				stringReader.setCursor(i);
				throw class_2096.field_9700.createWithContext(stringReader);
			} else {
				return new class_2152(float_, float2);
			}
		}
	}

	@Nullable
	private static Float method_9176(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_9173(stringReader, bl)) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		if (string.isEmpty()) {
			return null;
		} else {
			try {
				return Float.parseFloat(string);
			} catch (NumberFormatException var5) {
				if (bl) {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(stringReader, string);
				} else {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(stringReader, string);
				}
			}
		}
	}

	private static boolean method_9173(StringReader stringReader, boolean bl) {
		char c = stringReader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return bl && c == '.' ? !stringReader.canRead(2) || stringReader.peek(1) != '.' : false;
		} else {
			return true;
		}
	}

	@Nullable
	private static Float method_9174(@Nullable Float float_, Function<Float, Float> function) {
		return float_ == null ? null : (Float)function.apply(float_);
	}
}
