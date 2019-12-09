/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;

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

    public boolean hasPermissionLevel(int var1);

    public static <T> void forEachMatching(Iterable<T> candidates, String string, Function<T, Identifier> identifier, Consumer<T> action) {
        boolean bl = string.indexOf(58) > -1;
        for (T object : candidates) {
            Identifier identifier2 = identifier.apply(object);
            if (bl) {
                String string2 = identifier2.toString();
                if (!string2.startsWith(string)) continue;
                action.accept(object);
                continue;
            }
            if (!identifier2.getNamespace().startsWith(string) && (!identifier2.getNamespace().equals("minecraft") || !identifier2.getPath().startsWith(string))) continue;
            action.accept(object);
        }
    }

    public static <T> void forEachMatching(Iterable<T> candidates, String string, String string2, Function<T, Identifier> identifier, Consumer<T> action) {
        if (string.isEmpty()) {
            candidates.forEach(action);
        } else {
            String string3 = Strings.commonPrefix(string, string2);
            if (!string3.isEmpty()) {
                String string4 = string.substring(string3.length());
                CommandSource.forEachMatching(candidates, string4, identifier, action);
            }
        }
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candiates, SuggestionsBuilder builder, String string) {
        String string2 = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candiates, string2, string, identifier -> identifier, identifier -> builder.suggest(string + identifier));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, identifier -> identifier, identifier -> builder.suggest(identifier.toString()));
        return builder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, identifier, object -> builder.suggest(((Identifier)identifier.apply(object)).toString(), (Message)tooltip.apply(object)));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> stream, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestIdentifiers(stream::iterator, suggestionsBuilder);
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Stream<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        return CommandSource.suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
    }

    public static CompletableFuture<Suggestions> suggestPositions(String string, Collection<RelativePosition> candidates, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate) {
        ArrayList<String> list;
        block4: {
            String[] strings;
            block5: {
                block3: {
                    list = Lists.newArrayList();
                    if (!Strings.isNullOrEmpty(string)) break block3;
                    for (RelativePosition relativePosition : candidates) {
                        String string2 = relativePosition.x + " " + relativePosition.y + " " + relativePosition.z;
                        if (!predicate.test(string2)) continue;
                        list.add(relativePosition.x);
                        list.add(relativePosition.x + " " + relativePosition.y);
                        list.add(string2);
                    }
                    break block4;
                }
                strings = string.split(" ");
                if (strings.length != 1) break block5;
                for (RelativePosition relativePosition2 : candidates) {
                    String string3 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
                    if (!predicate.test(string3)) continue;
                    list.add(strings[0] + " " + relativePosition2.y);
                    list.add(string3);
                }
                break block4;
            }
            if (strings.length != 2) break block4;
            for (RelativePosition relativePosition2 : candidates) {
                String string3 = strings[0] + " " + strings[1] + " " + relativePosition2.z;
                if (!predicate.test(string3)) continue;
                list.add(string3);
            }
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> suggestColumnPositions(String string, Collection<RelativePosition> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate) {
        ArrayList<String> list;
        block3: {
            block2: {
                list = Lists.newArrayList();
                if (!Strings.isNullOrEmpty(string)) break block2;
                for (RelativePosition relativePosition : collection) {
                    String string2 = relativePosition.x + " " + relativePosition.z;
                    if (!predicate.test(string2)) continue;
                    list.add(relativePosition.x);
                    list.add(string2);
                }
                break block3;
            }
            String[] strings = string.split(" ");
            if (strings.length != 1) break block3;
            for (RelativePosition relativePosition2 : collection) {
                String string3 = strings[0] + " " + relativePosition2.z;
                if (!predicate.test(string3)) continue;
                list.add(string3);
            }
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> suggestMatching(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : iterable) {
            if (!string2.toLowerCase(Locale.ROOT).startsWith(string)) continue;
            suggestionsBuilder.suggest(string2);
        }
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        stream.filter(string2 -> string2.toLowerCase(Locale.ROOT).startsWith(string)).forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(String[] strings, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : strings) {
            if (!string2.toLowerCase(Locale.ROOT).startsWith(string)) continue;
            suggestionsBuilder.suggest(string2);
        }
        return suggestionsBuilder.buildFuture();
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

