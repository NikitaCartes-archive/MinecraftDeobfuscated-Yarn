package net.minecraft.command.argument.packrat;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ParsingRules<S> {
	private final Map<Symbol<?>, ParsingRule<S, ?>> rules = new HashMap();

	public <T> void set(Symbol<T> symbol, ParsingRule<S, T> rule) {
		ParsingRule<S, ?> parsingRule = (ParsingRule<S, ?>)this.rules.putIfAbsent(symbol, rule);
		if (parsingRule != null) {
			throw new IllegalArgumentException("Trying to override rule: " + symbol);
		}
	}

	public <T> void set(Symbol<T> symbol, Term<S> term, ParsingRule.RuleAction<S, T> action) {
		this.set(symbol, ParsingRule.of(term, action));
	}

	public <T> void set(Symbol<T> symbol, Term<S> term, ParsingRule.StatelessAction<T> action) {
		this.set(symbol, ParsingRule.of(term, action));
	}

	@Nullable
	public <T> ParsingRule<S, T> get(Symbol<T> symbol) {
		return (ParsingRule<S, T>)this.rules.get(symbol);
	}
}
