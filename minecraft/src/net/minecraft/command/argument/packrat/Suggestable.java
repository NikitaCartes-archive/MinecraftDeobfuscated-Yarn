package net.minecraft.command.argument.packrat;

import java.util.stream.Stream;

public interface Suggestable<S> {
	Stream<String> possibleValues(ParsingState<S> state);

	static <S> Suggestable<S> empty() {
		return state -> Stream.empty();
	}
}
