package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface class_2172 {
	Collection<String> method_9262();

	default Collection<String> method_9269() {
		return Collections.emptyList();
	}

	Collection<String> method_9267();

	Collection<class_2960> method_9254();

	Stream<class_2960> method_9273();

	CompletableFuture<Suggestions> method_9261(CommandContext<class_2172> commandContext, SuggestionsBuilder suggestionsBuilder);

	default Collection<class_2172.class_2173> method_17771() {
		return Collections.singleton(class_2172.class_2173.field_9838);
	}

	default Collection<class_2172.class_2173> method_17772() {
		return Collections.singleton(class_2172.class_2173.field_9838);
	}

	boolean method_9259(int i);

	static <T> void method_9268(Iterable<T> iterable, String string, Function<T, class_2960> function, Consumer<T> consumer) {
		boolean bl = string.indexOf(58) > -1;

		for (T object : iterable) {
			class_2960 lv = (class_2960)function.apply(object);
			if (bl) {
				String string2 = lv.toString();
				if (string2.startsWith(string)) {
					consumer.accept(object);
				}
			} else if (lv.method_12836().startsWith(string) || lv.method_12836().equals("minecraft") && lv.method_12832().startsWith(string)) {
				consumer.accept(object);
			}
		}
	}

	static <T> void method_9250(Iterable<T> iterable, String string, String string2, Function<T, class_2960> function, Consumer<T> consumer) {
		if (string.isEmpty()) {
			iterable.forEach(consumer);
		} else {
			String string3 = Strings.commonPrefix(string, string2);
			if (!string3.isEmpty()) {
				String string4 = string.substring(string3.length());
				method_9268(iterable, string4, function, consumer);
			}
		}
	}

	static CompletableFuture<Suggestions> method_9258(Iterable<class_2960> iterable, SuggestionsBuilder suggestionsBuilder, String string) {
		String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		method_9250(iterable, string2, string, arg -> arg, arg -> suggestionsBuilder.suggest(string + arg));
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_9270(Iterable<class_2960> iterable, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		method_9268(iterable, string, arg -> arg, arg -> suggestionsBuilder.suggest(arg.toString()));
		return suggestionsBuilder.buildFuture();
	}

	static <T> CompletableFuture<Suggestions> method_9255(
		Iterable<T> iterable, SuggestionsBuilder suggestionsBuilder, Function<T, class_2960> function, Function<T, Message> function2
	) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		method_9268(
			iterable, string, function, object -> suggestionsBuilder.suggest(((class_2960)function.apply(object)).toString(), (Message)function2.apply(object))
		);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_9257(Stream<class_2960> stream, SuggestionsBuilder suggestionsBuilder) {
		return method_9270(stream::iterator, suggestionsBuilder);
	}

	static <T> CompletableFuture<Suggestions> method_9271(
		Stream<T> stream, SuggestionsBuilder suggestionsBuilder, Function<T, class_2960> function, Function<T, Message> function2
	) {
		return method_9255(stream::iterator, suggestionsBuilder, function, function2);
	}

	static CompletableFuture<Suggestions> method_9260(
		String string, Collection<class_2172.class_2173> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (class_2172.class_2173 lv : collection) {
				String string2 = lv.field_9835 + " " + lv.field_9836 + " " + lv.field_9837;
				if (predicate.test(string2)) {
					list.add(lv.field_9835);
					list.add(lv.field_9835 + " " + lv.field_9836);
					list.add(string2);
				}
			}
		} else {
			String[] strings = string.split(" ");
			if (strings.length == 1) {
				for (class_2172.class_2173 lv2 : collection) {
					String string3 = strings[0] + " " + lv2.field_9836 + " " + lv2.field_9837;
					if (predicate.test(string3)) {
						list.add(strings[0] + " " + lv2.field_9836);
						list.add(string3);
					}
				}
			} else if (strings.length == 2) {
				for (class_2172.class_2173 lv2x : collection) {
					String string3 = strings[0] + " " + strings[1] + " " + lv2x.field_9837;
					if (predicate.test(string3)) {
						list.add(string3);
					}
				}
			}
		}

		return method_9265(list, suggestionsBuilder);
	}

	static CompletableFuture<Suggestions> method_9252(
		String string, Collection<class_2172.class_2173> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (class_2172.class_2173 lv : collection) {
				String string2 = lv.field_9835 + " " + lv.field_9837;
				if (predicate.test(string2)) {
					list.add(lv.field_9835);
					list.add(string2);
				}
			}
		} else {
			String[] strings = string.split(" ");
			if (strings.length == 1) {
				for (class_2172.class_2173 lv2 : collection) {
					String string3 = strings[0] + " " + lv2.field_9837;
					if (predicate.test(string3)) {
						list.add(string3);
					}
				}
			}
		}

		return method_9265(list, suggestionsBuilder);
	}

	static CompletableFuture<Suggestions> method_9265(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : iterable) {
			if (string2.toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_9264(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		stream.filter(string2 -> string2.toLowerCase(Locale.ROOT).startsWith(string)).forEach(suggestionsBuilder::suggest);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_9253(String[] strings, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : strings) {
			if (string2.toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	public static class class_2173 {
		public static final class_2172.class_2173 field_9834 = new class_2172.class_2173("^", "^", "^");
		public static final class_2172.class_2173 field_9838 = new class_2172.class_2173("~", "~", "~");
		public final String field_9835;
		public final String field_9836;
		public final String field_9837;

		public class_2173(String string, String string2, String string3) {
			this.field_9835 = string;
			this.field_9836 = string2;
			this.field_9837 = string3;
		}
	}
}
