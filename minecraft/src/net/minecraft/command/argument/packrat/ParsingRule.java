package net.minecraft.command.argument.packrat;

import java.util.Optional;

public interface ParsingRule<S, T> {
	Optional<T> parse(ParsingState<S> state);

	static <S, T> ParsingRule<S, T> of(Term<S> term, ParsingRule.RuleAction<S, T> action) {
		return new ParsingRule.SimpleRule<>(action, term);
	}

	static <S, T> ParsingRule<S, T> of(Term<S> term, ParsingRule.StatelessAction<T> action) {
		return new ParsingRule.SimpleRule<>((state, results) -> Optional.of(action.run(results)), term);
	}

	@FunctionalInterface
	public interface RuleAction<S, T> {
		Optional<T> run(ParsingState<S> state, ParseResults results);
	}

	public static record SimpleRule<S, T>(ParsingRule.RuleAction<S, T> action, Term<S> child) implements ParsingRule<S, T> {
		@Override
		public Optional<T> parse(ParsingState<S> state) {
			ParseResults parseResults = new ParseResults();
			return this.child.matches(state, parseResults, Cut.NOOP) ? this.action.run(state, parseResults) : Optional.empty();
		}
	}

	@FunctionalInterface
	public interface StatelessAction<T> {
		T run(ParseResults results);
	}
}
