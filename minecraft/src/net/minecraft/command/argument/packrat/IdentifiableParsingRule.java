package net.minecraft.command.argument.packrat;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import java.util.Optional;
import net.minecraft.util.Identifier;

public abstract class IdentifiableParsingRule<C, V> implements ParsingRule<StringReader, V>, IdentifierSuggestable {
	private final Symbol<Identifier> symbol;
	protected final C callbacks;

	protected IdentifiableParsingRule(Symbol<Identifier> symbol, C callbacks) {
		this.symbol = symbol;
		this.callbacks = callbacks;
	}

	@Override
	public Optional<V> parse(ParsingState<StringReader> state) {
		state.getReader().skipWhitespace();
		int i = state.getCursor();
		Optional<Identifier> optional = state.parse(this.symbol);
		if (optional.isPresent()) {
			try {
				return Optional.of(this.parse(state.getReader(), (Identifier)optional.get()));
			} catch (Exception var5) {
				state.getErrors().add(i, this, var5);
				return Optional.empty();
			}
		} else {
			state.getErrors().add(i, this, Identifier.COMMAND_EXCEPTION.createWithContext(state.getReader()));
			return Optional.empty();
		}
	}

	protected abstract V parse(ImmutableStringReader reader, Identifier id) throws Exception;
}
