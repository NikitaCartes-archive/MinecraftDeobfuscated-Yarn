package net.minecraft.command.argument.packrat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

public class PackratParsing {
	public static <T, C, P> ArgumentParser<List<T>> createParser(PackratParsing.Callbacks<T, C, P> callbacks) {
		Symbol<List<T>> symbol = Symbol.of("top");
		Symbol<Optional<T>> symbol2 = Symbol.of("type");
		Symbol<Unit> symbol3 = Symbol.of("any_type");
		Symbol<T> symbol4 = Symbol.of("element_type");
		Symbol<T> symbol5 = Symbol.of("tag_type");
		Symbol<List<T>> symbol6 = Symbol.of("conditions");
		Symbol<List<T>> symbol7 = Symbol.of("alternatives");
		Symbol<T> symbol8 = Symbol.of("term");
		Symbol<T> symbol9 = Symbol.of("negation");
		Symbol<T> symbol10 = Symbol.of("test");
		Symbol<C> symbol11 = Symbol.of("component_type");
		Symbol<P> symbol12 = Symbol.of("predicate_type");
		Symbol<Identifier> symbol13 = Symbol.of("id");
		Symbol<NbtElement> symbol14 = Symbol.of("tag");
		ParsingRules<StringReader> parsingRules = new ParsingRules<>();
		parsingRules.set(
			symbol,
			Term.anyOf(
				Term.sequence(Term.symbol(symbol2), Literals.character('['), Term.cutting(), Term.optional(Term.symbol(symbol6)), Literals.character(']')),
				Term.symbol(symbol2)
			),
			results -> {
				Builder<T> builder = ImmutableList.builder();
				results.getOrThrow(symbol2).ifPresent(builder::add);
				List<T> list = results.get(symbol6);
				if (list != null) {
					builder.addAll(list);
				}

				return builder.build();
			}
		);
		parsingRules.set(
			symbol2,
			Term.anyOf(Term.symbol(symbol4), Term.sequence(Literals.character('#'), Term.cutting(), Term.symbol(symbol5)), Term.symbol(symbol3)),
			results -> Optional.ofNullable(results.getAny(symbol4, symbol5))
		);
		parsingRules.set(symbol3, Literals.character('*'), results -> Unit.INSTANCE);
		parsingRules.set(symbol4, new PackratParsing.ItemParsingRule<>(symbol13, callbacks));
		parsingRules.set(symbol5, new PackratParsing.TagParsingRule<>(symbol13, callbacks));
		parsingRules.set(symbol6, Term.sequence(Term.symbol(symbol7), Term.optional(Term.sequence(Literals.character(','), Term.symbol(symbol6)))), results -> {
			T object = callbacks.anyOf(results.getOrThrow(symbol7));
			return (List<T>)Optional.ofNullable(results.get(symbol6)).map(predicates -> Util.withPrepended(object, predicates)).orElse(List.of(object));
		});
		parsingRules.set(symbol7, Term.sequence(Term.symbol(symbol8), Term.optional(Term.sequence(Literals.character('|'), Term.symbol(symbol7)))), results -> {
			T object = results.getOrThrow(symbol8);
			return (List<T>)Optional.ofNullable(results.get(symbol7)).map(predicates -> Util.withPrepended(object, predicates)).orElse(List.of(object));
		});
		parsingRules.set(
			symbol8,
			Term.anyOf(Term.symbol(symbol10), Term.sequence(Literals.character('!'), Term.symbol(symbol9))),
			results -> results.getAnyOrThrow(symbol10, symbol9)
		);
		parsingRules.set(symbol9, Term.symbol(symbol10), results -> callbacks.negate(results.getOrThrow(symbol10)));
		parsingRules.set(
			symbol10,
			Term.anyOf(
				Term.sequence(Term.symbol(symbol11), Literals.character('='), Term.cutting(), Term.symbol(symbol14)),
				Term.sequence(Term.symbol(symbol12), Literals.character('~'), Term.cutting(), Term.symbol(symbol14)),
				Term.symbol(symbol11)
			),
			(state, results) -> {
				P object = results.get(symbol12);

				try {
					if (object != null) {
						NbtElement nbtElement = results.getOrThrow(symbol14);
						return Optional.of(callbacks.subPredicatePredicate(state.getReader(), object, nbtElement));
					} else {
						C object2 = results.getOrThrow(symbol11);
						NbtElement nbtElement2 = results.get(symbol14);
						return Optional.of(
							nbtElement2 != null
								? callbacks.componentMatchPredicate(state.getReader(), object2, nbtElement2)
								: callbacks.componentPresencePredicate(state.getReader(), object2)
						);
					}
				} catch (CommandSyntaxException var9x) {
					state.getErrors().add(state.getCursor(), var9x);
					return Optional.empty();
				}
			}
		);
		parsingRules.set(symbol11, new PackratParsing.ComponentParsingRule<>(symbol13, callbacks));
		parsingRules.set(symbol12, new PackratParsing.SubPredicateParsingRule<>(symbol13, callbacks));
		parsingRules.set(symbol14, NbtParsingRule.INSTANCE);
		parsingRules.set(symbol13, AnyIdParsingRule.INSTANCE);
		return new ArgumentParser<>(parsingRules, symbol);
	}

	public interface Callbacks<T, C, P> {
		T itemMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

		Stream<Identifier> streamItemIds();

		T tagMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

		Stream<Identifier> streamTags();

		C componentCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

		Stream<Identifier> streamComponentIds();

		T componentMatchPredicate(ImmutableStringReader reader, C check, NbtElement nbt) throws CommandSyntaxException;

		T componentPresencePredicate(ImmutableStringReader reader, C check);

		P subPredicateCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

		Stream<Identifier> streamSubPredicateIds();

		T subPredicatePredicate(ImmutableStringReader reader, P check, NbtElement nbt) throws CommandSyntaxException;

		T negate(T predicate);

		T anyOf(List<T> predicates);
	}

	static class ComponentParsingRule<T, C, P> extends IdentifiableParsingRule<PackratParsing.Callbacks<T, C, P>, C> {
		ComponentParsingRule(Symbol<Identifier> symbol, PackratParsing.Callbacks<T, C, P> callbacks) {
			super(symbol, callbacks);
		}

		@Override
		protected C parse(ImmutableStringReader reader, Identifier id) throws Exception {
			return this.callbacks.componentCheck(reader, id);
		}

		@Override
		public Stream<Identifier> possibleIds() {
			return this.callbacks.streamComponentIds();
		}
	}

	static class ItemParsingRule<T, C, P> extends IdentifiableParsingRule<PackratParsing.Callbacks<T, C, P>, T> {
		ItemParsingRule(Symbol<Identifier> symbol, PackratParsing.Callbacks<T, C, P> callbacks) {
			super(symbol, callbacks);
		}

		@Override
		protected T parse(ImmutableStringReader reader, Identifier id) throws Exception {
			return this.callbacks.itemMatchPredicate(reader, id);
		}

		@Override
		public Stream<Identifier> possibleIds() {
			return this.callbacks.streamItemIds();
		}
	}

	static class SubPredicateParsingRule<T, C, P> extends IdentifiableParsingRule<PackratParsing.Callbacks<T, C, P>, P> {
		SubPredicateParsingRule(Symbol<Identifier> symbol, PackratParsing.Callbacks<T, C, P> callbacks) {
			super(symbol, callbacks);
		}

		@Override
		protected P parse(ImmutableStringReader reader, Identifier id) throws Exception {
			return this.callbacks.subPredicateCheck(reader, id);
		}

		@Override
		public Stream<Identifier> possibleIds() {
			return this.callbacks.streamSubPredicateIds();
		}
	}

	static class TagParsingRule<T, C, P> extends IdentifiableParsingRule<PackratParsing.Callbacks<T, C, P>, T> {
		TagParsingRule(Symbol<Identifier> symbol, PackratParsing.Callbacks<T, C, P> callbacks) {
			super(symbol, callbacks);
		}

		@Override
		protected T parse(ImmutableStringReader reader, Identifier id) throws Exception {
			return this.callbacks.tagMatchPredicate(reader, id);
		}

		@Override
		public Stream<Identifier> possibleIds() {
			return this.callbacks.streamTags();
		}
	}
}
