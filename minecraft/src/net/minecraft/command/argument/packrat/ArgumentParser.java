package net.minecraft.command.argument.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;

public record ArgumentParser<T>(ParsingRules<StringReader> rules, Symbol<T> top) {
	public Optional<T> startParsing(ParsingState<StringReader> state) {
		return state.startParsing(this.top);
	}

	public T parse(StringReader reader) throws CommandSyntaxException {
		ParseErrorList.Impl<StringReader> impl = new ParseErrorList.Impl<>();
		ParsingStateImpl parsingStateImpl = new ParsingStateImpl(this.rules(), impl, reader);
		Optional<T> optional = this.startParsing(parsingStateImpl);
		if (optional.isPresent()) {
			return (T)optional.get();
		} else {
			List<Exception> list = impl.getErrors().stream().mapMulti((error, consumer) -> {
				if (error.reason() instanceof Exception exceptionx) {
					consumer.accept(exceptionx);
				}
			}).toList();

			for (Exception exception : list) {
				if (exception instanceof CommandSyntaxException commandSyntaxException) {
					throw commandSyntaxException;
				}
			}

			if (list.size() == 1 && list.get(0) instanceof RuntimeException runtimeException) {
				throw runtimeException;
			} else {
				throw new IllegalStateException("Failed to parse: " + (String)impl.getErrors().stream().map(ParseError::toString).collect(Collectors.joining(", ")));
			}
		}
	}

	public CompletableFuture<Suggestions> listSuggestions(SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ParseErrorList.Impl<StringReader> impl = new ParseErrorList.Impl<>();
		ParsingStateImpl parsingStateImpl = new ParsingStateImpl(this.rules(), impl, stringReader);
		this.startParsing(parsingStateImpl);
		List<ParseError<StringReader>> list = impl.getErrors();
		if (list.isEmpty()) {
			return builder.buildFuture();
		} else {
			SuggestionsBuilder suggestionsBuilder = builder.createOffset(impl.getCursor());

			for (ParseError<StringReader> parseError : list) {
				if (parseError.suggestions() instanceof IdentifierSuggestable identifierSuggestable) {
					CommandSource.suggestIdentifiers(identifierSuggestable.possibleIds(), suggestionsBuilder);
				} else {
					CommandSource.suggestMatching(parseError.suggestions().possibleValues(parsingStateImpl), suggestionsBuilder);
				}
			}

			return suggestionsBuilder.buildFuture();
		}
	}
}
