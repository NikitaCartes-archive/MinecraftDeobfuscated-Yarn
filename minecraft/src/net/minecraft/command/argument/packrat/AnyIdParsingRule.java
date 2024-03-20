package net.minecraft.command.argument.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.util.Identifier;

public class AnyIdParsingRule implements ParsingRule<StringReader, Identifier> {
	public static final ParsingRule<StringReader, Identifier> INSTANCE = new AnyIdParsingRule();

	private AnyIdParsingRule() {
	}

	@Override
	public Optional<Identifier> parse(ParsingState<StringReader> state) {
		state.getReader().skipWhitespace();

		try {
			return Optional.of(Identifier.fromCommandInputNonEmpty(state.getReader()));
		} catch (CommandSyntaxException var3) {
			return Optional.empty();
		}
	}
}
