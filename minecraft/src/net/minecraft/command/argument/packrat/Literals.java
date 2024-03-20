package net.minecraft.command.argument.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;

public interface Literals {
	static Term<StringReader> string(String string) {
		return new Literals.StringLiteral(string);
	}

	static Term<StringReader> character(char c) {
		return new Literals.CharLiteral(c);
	}

	public static record CharLiteral(char value) implements Term<StringReader> {
		@Override
		public boolean matches(ParsingState<StringReader> state, ParseResults results, Cut cut) {
			state.getReader().skipWhitespace();
			int i = state.getCursor();
			if (state.getReader().canRead() && state.getReader().read() == this.value) {
				return true;
			} else {
				state.getErrors()
					.add(i, suggestState -> Stream.of(String.valueOf(this.value)), CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(this.value));
				return false;
			}
		}
	}

	public static record StringLiteral(String value) implements Term<StringReader> {
		@Override
		public boolean matches(ParsingState<StringReader> state, ParseResults results, Cut cut) {
			state.getReader().skipWhitespace();
			int i = state.getCursor();
			String string = state.getReader().readUnquotedString();
			if (!string.equals(this.value)) {
				state.getErrors().add(i, suggestState -> Stream.of(this.value), CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(this.value));
				return false;
			} else {
				return true;
			}
		}
	}
}
