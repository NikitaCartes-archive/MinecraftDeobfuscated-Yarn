package net.minecraft.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.NumberRange;

public class FloatRange {
	public static final FloatRange ANY = new FloatRange(null, null);
	public static final SimpleCommandExceptionType ONLY_INTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.range.ints"));
	private final Float min;
	private final Float max;

	public FloatRange(@Nullable Float float_, @Nullable Float float2) {
		this.min = float_;
		this.max = float2;
	}

	@Nullable
	public Float getMin() {
		return this.min;
	}

	@Nullable
	public Float getMax() {
		return this.max;
	}

	public static FloatRange parse(StringReader stringReader, boolean bl, Function<Float, Float> function) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw NumberRange.EXCEPTION_EMPTY.createWithContext(stringReader);
		} else {
			int i = stringReader.getCursor();
			Float float_ = mapFloat(parseFloat(stringReader, bl), function);
			Float float2;
			if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
				stringReader.skip();
				stringReader.skip();
				float2 = mapFloat(parseFloat(stringReader, bl), function);
				if (float_ == null && float2 == null) {
					stringReader.setCursor(i);
					throw NumberRange.EXCEPTION_EMPTY.createWithContext(stringReader);
				}
			} else {
				if (!bl && stringReader.canRead() && stringReader.peek() == '.') {
					stringReader.setCursor(i);
					throw ONLY_INTS_EXCEPTION.createWithContext(stringReader);
				}

				float2 = float_;
			}

			if (float_ == null && float2 == null) {
				stringReader.setCursor(i);
				throw NumberRange.EXCEPTION_EMPTY.createWithContext(stringReader);
			} else {
				return new FloatRange(float_, float2);
			}
		}
	}

	@Nullable
	private static Float parseFloat(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && peekDigit(stringReader, bl)) {
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

	private static boolean peekDigit(StringReader stringReader, boolean bl) {
		char c = stringReader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return bl && c == '.' ? !stringReader.canRead(2) || stringReader.peek(1) != '.' : false;
		} else {
			return true;
		}
	}

	@Nullable
	private static Float mapFloat(@Nullable Float float_, Function<Float, Float> function) {
		return float_ == null ? null : (Float)function.apply(float_);
	}
}
