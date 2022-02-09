/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface CommandSource {
    public Collection<String> getPlayerNames();

    default public Collection<String> getEntitySuggestions() {
        return Collections.emptyList();
    }

    public Collection<String> getTeamNames();

    public Collection<Identifier> getSoundIds();

    public Stream<Identifier> getRecipeIds();

    public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> var1, SuggestionsBuilder var2);

    default public Collection<RelativePosition> getBlockPositionSuggestions() {
        return Collections.singleton(RelativePosition.ZERO_WORLD);
    }

    default public Collection<RelativePosition> getPositionSuggestions() {
        return Collections.singleton(RelativePosition.ZERO_WORLD);
    }

    public Set<RegistryKey<World>> getWorldKeys();

    public DynamicRegistryManager getRegistryManager();

    public boolean hasPermissionLevel(int var1);

    public static <T> void forEachMatching(Iterable<T> candidates, String remaining, Function<T, Identifier> identifier, Consumer<T> action) {
        boolean bl = remaining.indexOf(58) > -1;
        for (T object : candidates) {
            Identifier identifier2 = identifier.apply(object);
            if (bl) {
                String string = identifier2.toString();
                if (!CommandSource.shouldSuggest(remaining, string)) continue;
                action.accept(object);
                continue;
            }
            if (!CommandSource.shouldSuggest(remaining, identifier2.getNamespace()) && (!identifier2.getNamespace().equals("minecraft") || !CommandSource.shouldSuggest(remaining, identifier2.getPath()))) continue;
            action.accept(object);
        }
    }

    public static <T> void forEachMatching(Iterable<T> candidates, String remaining, String prefix, Function<T, Identifier> identifier, Consumer<T> action) {
        if (remaining.isEmpty()) {
            candidates.forEach(action);
        } else {
            String string = Strings.commonPrefix(remaining, prefix);
            if (!string.isEmpty()) {
                String string2 = remaining.substring(string.length());
                CommandSource.forEachMatching(candidates, string2, identifier, action);
            }
        }
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, prefix, id -> id, id -> builder.suggest(prefix + id));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
        return CommandSource.suggestIdentifiers(candidates::iterator, builder, prefix);
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, id -> id, id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, identifier, object -> builder.suggest(((Identifier)identifier.apply(object)).toString(), (Message)tooltip.apply(object)));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(candidates::iterator, builder);
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Stream<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        return CommandSource.suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
    }

    public static CompletableFuture<Suggestions> suggestPositions(String remaining, Collection<RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
        ArrayList<String> list;
        block4: {
            String[] strings;
            block5: {
                block3: {
                    list = Lists.newArrayList();
                    if (!Strings.isNullOrEmpty(remaining)) break block3;
                    for (RelativePosition relativePosition : candidates) {
                        String string = relativePosition.x + " " + relativePosition.y + " " + relativePosition.z;
                        if (!predicate.test(string)) continue;
                        list.add(relativePosition.x);
                        list.add(relativePosition.x + " " + relativePosition.y);
                        list.add(string);
                    }
                    break block4;
                }
                strings = remaining.split(" ");
                if (strings.length != 1) break block5;
                for (RelativePosition relativePosition2 : candidates) {
                    String string2 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
                    if (!predicate.test(string2)) continue;
                    list.add(strings[0] + " " + relativePosition2.y);
                    list.add(string2);
                }
                break block4;
            }
            if (strings.length != 2) break block4;
            for (RelativePosition relativePosition2 : candidates) {
                String string2 = strings[0] + " " + strings[1] + " " + relativePosition2.z;
                if (!predicate.test(string2)) continue;
                list.add(string2);
            }
        }
        return CommandSource.suggestMatching(list, builder);
    }

    public static CompletableFuture<Suggestions> suggestColumnPositions(String remaining, Collection<RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
        ArrayList<String> list;
        block3: {
            block2: {
                list = Lists.newArrayList();
                if (!Strings.isNullOrEmpty(remaining)) break block2;
                for (RelativePosition relativePosition : candidates) {
                    String string = relativePosition.x + " " + relativePosition.z;
                    if (!predicate.test(string)) continue;
                    list.add(relativePosition.x);
                    list.add(string);
                }
                break block3;
            }
            String[] strings = remaining.split(" ");
            if (strings.length != 1) break block3;
            for (RelativePosition relativePosition2 : candidates) {
                String string2 = strings[0] + " " + relativePosition2.z;
                if (!predicate.test(string2)) continue;
                list.add(string2);
            }
        }
        return CommandSource.suggestMatching(list, builder);
    }

    public static CompletableFuture<Suggestions> suggestMatching(Iterable<String> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : candidates) {
            if (!CommandSource.shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) continue;
            builder.suggest(string2);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(Stream<String> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        candidates.filter(candidate -> CommandSource.shouldSuggest(string, candidate.toLowerCase(Locale.ROOT))).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(String[] candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : candidates) {
            if (!CommandSource.shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) continue;
            builder.suggest(string2);
        }
        return builder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestMatching(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, String> suggestionText, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (T object : candidates) {
            String string2 = suggestionText.apply(object);
            if (!CommandSource.shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) continue;
            builder.suggest(string2, tooltip.apply(object));
        }
        return builder.buildFuture();
    }

    /**
     * {@return if a candidate should be suggested}
     * 
     * <p>Returns {@code true} if the {@code remaining} starts with {@code
     * candidate} or contains {@code "_" + candidate}
     */
    public static boolean shouldSuggest(String remaining, String candidate) {
        int i = 0;
        while (!candidate.startsWith(remaining, i)) {
            if ((i = candidate.indexOf(95, i)) < 0) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static class RelativePosition {
        public static final RelativePosition ZERO_LOCAL = new RelativePosition("^", "^", "^");
        public static final RelativePosition ZERO_WORLD = new RelativePosition("~", "~", "~");
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

