package net.minecraft.command.argument.packrat;

import com.mojang.brigadier.StringReader;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;

public interface IdentifierSuggestable extends Suggestable<StringReader> {
	Stream<Identifier> possibleIds();

	@Override
	default Stream<String> possibleValues(ParsingState<StringReader> parsingState) {
		return this.possibleIds().map(Identifier::toString);
	}
}
