package net.minecraft.server.command;

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
import net.minecraft.util.Identifier;

public interface CommandSource {
	Collection<String> getPlayerNames();

	default Collection<String> getEntitySuggestions() {
		return Collections.emptyList();
	}

	Collection<String> getTeamNames();

	Collection<Identifier> getSoundIds();

	Stream<Identifier> getRecipeIds();

	CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder);

	default Collection<CommandSource.RelativePosition> getBlockPositionSuggestions() {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}

	default Collection<CommandSource.RelativePosition> getPositionSuggestions() {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}

	boolean hasPermissionLevel(int i);

	static <T> void forEachMatching(Iterable<T> iterable, String string, Function<T, Identifier> function, Consumer<T> consumer) {
		boolean bl = string.indexOf(58) > -1;

		for (T object : iterable) {
			Identifier identifier = (Identifier)function.apply(object);
			if (bl) {
				String string2 = identifier.toString();
				if (string2.startsWith(string)) {
					consumer.accept(object);
				}
			} else if (identifier.getNamespace().startsWith(string) || identifier.getNamespace().equals("minecraft") && identifier.getPath().startsWith(string)) {
				consumer.accept(object);
			}
		}
	}

	static <T> void forEachMatching(Iterable<T> iterable, String string, String string2, Function<T, Identifier> function, Consumer<T> consumer) {
		if (string.isEmpty()) {
			iterable.forEach(consumer);
		} else {
			String string3 = Strings.commonPrefix(string, string2);
			if (!string3.isEmpty()) {
				String string4 = string.substring(string3.length());
				forEachMatching(iterable, string4, function, consumer);
			}
		}
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder, String string) {
		String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(iterable, string2, string, identifier -> identifier, identifier -> suggestionsBuilder.suggest(string + identifier));
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(iterable, string, identifier -> identifier, identifier -> suggestionsBuilder.suggest(identifier.toString()));
		return suggestionsBuilder.buildFuture();
	}

	static <T> CompletableFuture<Suggestions> suggestFromIdentifier(
		Iterable<T> iterable, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2
	) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(
			iterable, string, function, object -> suggestionsBuilder.suggest(((Identifier)function.apply(object)).toString(), (Message)function2.apply(object))
		);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> stream, SuggestionsBuilder suggestionsBuilder) {
		return suggestIdentifiers(stream::iterator, suggestionsBuilder);
	}

	static <T> CompletableFuture<Suggestions> suggestFromIdentifier(
		Stream<T> stream, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2
	) {
		return suggestFromIdentifier(stream::iterator, suggestionsBuilder, function, function2);
	}

	static CompletableFuture<Suggestions> suggestPositions(
		String string, Collection<CommandSource.RelativePosition> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (CommandSource.RelativePosition relativePosition : collection) {
				String string2 = relativePosition.x + " " + relativePosition.y + " " + relativePosition.z;
				if (predicate.test(string2)) {
					list.add(relativePosition.x);
					list.add(relativePosition.x + " " + relativePosition.y);
					list.add(string2);
				}
			}
		} else {
			String[] strings = string.split(" ");
			if (strings.length == 1) {
				for (CommandSource.RelativePosition relativePosition2 : collection) {
					String string3 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
					if (predicate.test(string3)) {
						list.add(strings[0] + " " + relativePosition2.y);
						list.add(string3);
					}
				}
			} else if (strings.length == 2) {
				for (CommandSource.RelativePosition relativePosition2x : collection) {
					String string3 = strings[0] + " " + strings[1] + " " + relativePosition2x.z;
					if (predicate.test(string3)) {
						list.add(string3);
					}
				}
			}
		}

		return suggestMatching(list, suggestionsBuilder);
	}

	static CompletableFuture<Suggestions> suggestColumnPositions(
		String string, Collection<CommandSource.RelativePosition> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (CommandSource.RelativePosition relativePosition : collection) {
				String string2 = relativePosition.x + " " + relativePosition.z;
				if (predicate.test(string2)) {
					list.add(relativePosition.x);
					list.add(string2);
				}
			}
		} else {
			String[] strings = string.split(" ");
			if (strings.length == 1) {
				for (CommandSource.RelativePosition relativePosition2 : collection) {
					String string3 = strings[0] + " " + relativePosition2.z;
					if (predicate.test(string3)) {
						list.add(string3);
					}
				}
			}
		}

		return suggestMatching(list, suggestionsBuilder);
	}

	static CompletableFuture<Suggestions> suggestMatching(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : iterable) {
			if (string2.toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestMatching(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		stream.filter(string2 -> string2.toLowerCase(Locale.ROOT).startsWith(string)).forEach(suggestionsBuilder::suggest);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestMatching(String[] strings, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : strings) {
			if (string2.toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	public static class RelativePosition {
		public static final CommandSource.RelativePosition ZERO_LOCAL = new CommandSource.RelativePosition("^", "^", "^");
		public static final CommandSource.RelativePosition ZERO_WORLD = new CommandSource.RelativePosition("~", "~", "~");
		public final String x;
		public final String y;
		public final String z;

		public RelativePosition(String string, String string2, String string3) {
			this.x = string;
			this.y = string2;
			this.z = string3;
		}
	}
}
