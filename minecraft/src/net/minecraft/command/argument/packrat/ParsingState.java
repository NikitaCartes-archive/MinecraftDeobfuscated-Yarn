package net.minecraft.command.argument.packrat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class ParsingState<S> {
	private final Map<ParsingState.PackratKey<?>, ParsingState.PackratCache<?>> packrats = new HashMap();
	private final ParsingRules<S> rules;
	private final ParseErrorList<S> errors;

	protected ParsingState(ParsingRules<S> rules, ParseErrorList<S> errors) {
		this.rules = rules;
		this.errors = errors;
	}

	public ParseErrorList<S> getErrors() {
		return this.errors;
	}

	public <T> Optional<T> startParsing(Symbol<T> startSymbol) {
		Optional<T> optional = this.parse(startSymbol);
		if (optional.isPresent()) {
			this.errors.setCursor(this.getCursor());
		}

		return optional;
	}

	public <T> Optional<T> parse(Symbol<T> symbol) {
		ParsingState.PackratKey<T> packratKey = new ParsingState.PackratKey<>(symbol, this.getCursor());
		ParsingState.PackratCache<T> packratCache = this.getCache(packratKey);
		if (packratCache != null) {
			this.setCursor(packratCache.mark());
			return packratCache.value;
		} else {
			ParsingRule<S, T> parsingRule = this.rules.get(symbol);
			if (parsingRule == null) {
				throw new IllegalStateException("No symbol " + symbol);
			} else {
				Optional<T> optional = parsingRule.parse(this);
				this.putCache(packratKey, optional);
				return optional;
			}
		}
	}

	@Nullable
	private <T> ParsingState.PackratCache<T> getCache(ParsingState.PackratKey<T> key) {
		return (ParsingState.PackratCache<T>)this.packrats.get(key);
	}

	private <T> void putCache(ParsingState.PackratKey<T> key, Optional<T> value) {
		this.packrats.put(key, new ParsingState.PackratCache(value, this.getCursor()));
	}

	public abstract S getReader();

	public abstract int getCursor();

	public abstract void setCursor(int cursor);

	static record PackratCache<T>(Optional<T> value, int mark) {
	}

	static record PackratKey<T>(Symbol<T> name, int mark) {
	}
}
