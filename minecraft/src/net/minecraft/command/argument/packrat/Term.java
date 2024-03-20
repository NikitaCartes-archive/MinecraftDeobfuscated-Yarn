package net.minecraft.command.argument.packrat;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.mutable.MutableBoolean;

public interface Term<S> {
	boolean matches(ParsingState<S> state, ParseResults results, Cut cut);

	static <S> Term<S> symbol(Symbol<?> symbol) {
		return new Term.SymbolTerm<>(symbol);
	}

	static <S, T> Term<S> always(Symbol<T> symbol, T value) {
		return new Term.AlwaysTerm<>(symbol, value);
	}

	@SafeVarargs
	static <S> Term<S> sequence(Term<S>... terms) {
		return new Term.SequenceTerm<>(List.of(terms));
	}

	@SafeVarargs
	static <S> Term<S> anyOf(Term<S>... terms) {
		return new Term.AnyOfTerm<>(List.of(terms));
	}

	static <S> Term<S> optional(Term<S> term) {
		return new Term.OptionalTerm<>(term);
	}

	static <S> Term<S> cutting() {
		return new Term<S>() {
			@Override
			public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
				cut.cut();
				return true;
			}

			public String toString() {
				return "↑";
			}
		};
	}

	static <S> Term<S> epsilon() {
		return new Term<S>() {
			@Override
			public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
				return true;
			}

			public String toString() {
				return "ε";
			}
		};
	}

	public static record AlwaysTerm<S, T>(Symbol<T> name, T value) implements Term<S> {
		@Override
		public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
			results.put(this.name, this.value);
			return true;
		}
	}

	public static record AnyOfTerm<S>(List<Term<S>> elements) implements Term<S> {
		@Override
		public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
			MutableBoolean mutableBoolean = new MutableBoolean();
			Cut cut2 = mutableBoolean::setTrue;
			int i = state.getCursor();

			for (Term<S> term : this.elements) {
				if (mutableBoolean.isTrue()) {
					break;
				}

				ParseResults parseResults = new ParseResults();
				if (term.matches(state, parseResults, cut2)) {
					results.putAll(parseResults);
					return true;
				}

				state.setCursor(i);
			}

			return false;
		}
	}

	public static record OptionalTerm<S>(Term<S> term) implements Term<S> {
		@Override
		public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
			int i = state.getCursor();
			if (!this.term.matches(state, results, cut)) {
				state.setCursor(i);
			}

			return true;
		}
	}

	public static record SequenceTerm<S>(List<Term<S>> elements) implements Term<S> {
		@Override
		public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
			int i = state.getCursor();

			for (Term<S> term : this.elements) {
				if (!term.matches(state, results, cut)) {
					state.setCursor(i);
					return false;
				}
			}

			return true;
		}
	}

	public static record SymbolTerm<S, T>(Symbol<T> name) implements Term<S> {
		@Override
		public boolean matches(ParsingState<S> state, ParseResults results, Cut cut) {
			Optional<T> optional = state.parse(this.name);
			if (optional.isEmpty()) {
				return false;
			} else {
				results.put(this.name, (T)optional.get());
				return true;
			}
		}
	}
}
