package net.minecraft.command;

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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface CommandSource {
	Collection<String> getPlayerNames();

	default Collection<String> getChatSuggestions() {
		return this.getPlayerNames();
	}

	default Collection<String> getEntitySuggestions() {
		return Collections.emptyList();
	}

	Collection<String> getTeamNames();

	Stream<Identifier> getSoundIds();

	Stream<Identifier> getRecipeIds();

	CompletableFuture<Suggestions> getCompletions(CommandContext<?> context);

	default Collection<CommandSource.RelativePosition> getBlockPositionSuggestions() {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}

	default Collection<CommandSource.RelativePosition> getPositionSuggestions() {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}

	Set<RegistryKey<World>> getWorldKeys();

	DynamicRegistryManager getRegistryManager();

	FeatureSet getEnabledFeatures();

	default void suggestIdentifiers(Registry<?> registry, CommandSource.SuggestedIdType suggestedIdType, SuggestionsBuilder builder) {
		if (suggestedIdType.canSuggestTags()) {
			suggestIdentifiers(registry.streamTags().map(TagKey::id), builder, "#");
		}

		if (suggestedIdType.canSuggestElements()) {
			suggestIdentifiers(registry.getIds(), builder);
		}
	}

	CompletableFuture<Suggestions> listIdSuggestions(
		RegistryKey<? extends Registry<?>> registryRef, CommandSource.SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context
	);

	boolean hasPermissionLevel(int level);

	static <T> void forEachMatching(Iterable<T> candidates, String remaining, Function<T, Identifier> identifier, Consumer<T> action) {
		boolean bl = remaining.indexOf(58) > -1;

		for (T object : candidates) {
			Identifier identifier2 = (Identifier)identifier.apply(object);
			if (bl) {
				String string = identifier2.toString();
				if (shouldSuggest(remaining, string)) {
					action.accept(object);
				}
			} else if (shouldSuggest(remaining, identifier2.getNamespace())
				|| identifier2.getNamespace().equals("minecraft") && shouldSuggest(remaining, identifier2.getPath())) {
				action.accept(object);
			}
		}
	}

	static <T> void forEachMatching(Iterable<T> candidates, String remaining, String prefix, Function<T, Identifier> identifier, Consumer<T> action) {
		if (remaining.isEmpty()) {
			candidates.forEach(action);
		} else {
			String string = Strings.commonPrefix(remaining, prefix);
			if (!string.isEmpty()) {
				String string2 = remaining.substring(string.length());
				forEachMatching(candidates, string2, identifier, action);
			}
		}
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(candidates, string, prefix, id -> id, id -> builder.suggest(prefix + id));
		return builder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
		return suggestIdentifiers(candidates::iterator, builder, prefix);
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(candidates, string, id -> id, id -> builder.suggest(id.toString()));
		return builder.buildFuture();
	}

	static <T> CompletableFuture<Suggestions> suggestFromIdentifier(
		Iterable<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip
	) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		forEachMatching(candidates, string, identifier, object -> builder.suggest(((Identifier)identifier.apply(object)).toString(), (Message)tooltip.apply(object)));
		return builder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder) {
		return suggestIdentifiers(candidates::iterator, builder);
	}

	static <T> CompletableFuture<Suggestions> suggestFromIdentifier(
		Stream<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip
	) {
		return suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
	}

	static CompletableFuture<Suggestions> suggestPositions(
		String remaining, Collection<CommandSource.RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(remaining)) {
			for (CommandSource.RelativePosition relativePosition : candidates) {
				String string = relativePosition.x + " " + relativePosition.y + " " + relativePosition.z;
				if (predicate.test(string)) {
					list.add(relativePosition.x);
					list.add(relativePosition.x + " " + relativePosition.y);
					list.add(string);
				}
			}
		} else {
			String[] strings = remaining.split(" ");
			if (strings.length == 1) {
				for (CommandSource.RelativePosition relativePosition2 : candidates) {
					String string2 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
					if (predicate.test(string2)) {
						list.add(strings[0] + " " + relativePosition2.y);
						list.add(string2);
					}
				}
			} else if (strings.length == 2) {
				for (CommandSource.RelativePosition relativePosition2x : candidates) {
					String string2 = strings[0] + " " + strings[1] + " " + relativePosition2x.z;
					if (predicate.test(string2)) {
						list.add(string2);
					}
				}
			}
		}

		return suggestMatching(list, builder);
	}

	static CompletableFuture<Suggestions> suggestColumnPositions(
		String remaining, Collection<CommandSource.RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate
	) {
		List<String> list = Lists.<String>newArrayList();
		if (Strings.isNullOrEmpty(remaining)) {
			for (CommandSource.RelativePosition relativePosition : candidates) {
				String string = relativePosition.x + " " + relativePosition.z;
				if (predicate.test(string)) {
					list.add(relativePosition.x);
					list.add(string);
				}
			}
		} else {
			String[] strings = remaining.split(" ");
			if (strings.length == 1) {
				for (CommandSource.RelativePosition relativePosition2 : candidates) {
					String string2 = strings[0] + " " + relativePosition2.z;
					if (predicate.test(string2)) {
						list.add(string2);
					}
				}
			}
		}

		return suggestMatching(list, builder);
	}

	static CompletableFuture<Suggestions> suggestMatching(Iterable<String> candidates, SuggestionsBuilder builder) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : candidates) {
			if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
				builder.suggest(string2);
			}
		}

		return builder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestMatching(Stream<String> candidates, SuggestionsBuilder builder) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		candidates.filter(candidate -> shouldSuggest(string, candidate.toLowerCase(Locale.ROOT))).forEach(builder::suggest);
		return builder.buildFuture();
	}

	static CompletableFuture<Suggestions> suggestMatching(String[] candidates, SuggestionsBuilder builder) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : candidates) {
			if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
				builder.suggest(string2);
			}
		}

		return builder.buildFuture();
	}

	static <T> CompletableFuture<Suggestions> suggestMatching(
		Iterable<T> candidates, SuggestionsBuilder builder, Function<T, String> suggestionText, Function<T, Message> tooltip
	) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (T object : candidates) {
			String string2 = (String)suggestionText.apply(object);
			if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
				builder.suggest(string2, (Message)tooltip.apply(object));
			}
		}

		return builder.buildFuture();
	}

	/**
	 * {@return if a candidate should be suggested}
	 * 
	 * <p>Returns {@code true} if the {@code remaining} starts with {@code
	 * candidate} or contains {@code "_" + candidate}
	 */
	static boolean shouldSuggest(String remaining, String candidate) {
		for (int i = 0; !candidate.startsWith(remaining, i); i++) {
			i = candidate.indexOf(95, i);
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

	public static enum SuggestedIdType {
		TAGS,
		ELEMENTS,
		ALL;

		public boolean canSuggestTags() {
			return this == TAGS || this == ALL;
		}

		public boolean canSuggestElements() {
			return this == ELEMENTS || this == ALL;
		}
	}
}
