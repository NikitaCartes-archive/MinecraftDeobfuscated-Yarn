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

	CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> context, SuggestionsBuilder builder);

	default Collection<CommandSource.RelativePosition> getBlockPositionSuggestions() {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}

	default Collection<CommandSource.RelativePosition> getPositionSuggestions() {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}

	boolean hasPermissionLevel(int level);

	static <T> void forEachMatching(Iterable<T> candidates, String string, Function<T, Identifier> identifier, Consumer<T> action) {
		boolean bl = string.indexOf(58) > -1;

		for (T object : candidates) {
			Identifier identifier2 = (Identifier)identifier.apply(object);
			if (bl) {
				String string2 = identifier2.toString();
				if (method_27136(string, string2)) {
					action.accept(object);
				}
			} else if (method_27136(string, identifier2.getNamespace()) || identifier2.getNamespace().equals("minecraft") && method_27136(string, identifier2.getPath())
				)
			 {
				action.accept(object);
			}
		}
	}

	static <T> void forEachMatching(Iterable<T> candidates, String string, String string2, Function<T, Identifier> identifier, Consumer<T> action) {
		if (string.isEmpty()) {
			candidates.forEach(action);
		} else {
			String string3 = Strings.commonPrefix(string, string2);
			if (!string3.isEmpty()) {
				String string4 = string.substring(string3.length());
				forEachMatching(candidates, string4, identifier, action);
			}
		}
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candiates, SuggestionsBuilder builder, String string) {
		String string2 = builder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(candiates, string2, string, identifier -> identifier, identifier -> builder.suggest(string + identifier));
		return builder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(candidates, string, identifier -> identifier, identifier -> builder.suggest(identifier.toString()));
		return builder.buildFuture();
	}

	static <T> CompletableFuture<Suggestions> suggestFromIdentifier(
		Iterable<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip
	) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(candidates, string, identifier, object -> builder.suggest(((Identifier)identifier.apply(object)).toString(), (Message)tooltip.apply(object)));
		return builder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> stream, SuggestionsBuilder suggestionsBuilder) {
		return suggestIdentifiers(stream::iterator, suggestionsBuilder);
	}

	static <T> CompletableFuture<Suggestions> suggestFromIdentifier(
		Stream<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip
	) {
		return suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
	}

	static CompletableFuture<Suggestions> suggestPositions(
		String string, Collection<CommandSource.RelativePosition> candidates, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (CommandSource.RelativePosition relativePosition : candidates) {
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
				for (CommandSource.RelativePosition relativePosition2 : candidates) {
					String string3 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
					if (predicate.test(string3)) {
						list.add(strings[0] + " " + relativePosition2.y);
						list.add(string3);
					}
				}
			} else if (strings.length == 2) {
				for (CommandSource.RelativePosition relativePosition2x : candidates) {
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
			if (method_27136(string, string2.toLowerCase(Locale.ROOT))) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestMatching(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		stream.filter(string2 -> method_27136(string, string2.toLowerCase(Locale.ROOT))).forEach(suggestionsBuilder::suggest);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestMatching(String[] strings, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : strings) {
			if (method_27136(string, string2.toLowerCase(Locale.ROOT))) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	static boolean method_27136(String string, String string2) {
		for (int i = 0; !string2.startsWith(string, i); i++) {
			i = string2.indexOf(95, i);
			if (i < 0) {
				return false;
			}
		}

		return true;
	}

	public static class RelativePosition {
		public static final CommandSource.RelativePosition ZERO_LOCAL = new CommandSource.RelativePosition("^", "^", "^");
		public static final CommandSource.RelativePosition ZERO_WORLD = new CommandSource.RelativePosition("~", "~", "~");
		public final String x;
		public final String y;
		public final String z;

		public RelativePosition(String x, String y, String z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
