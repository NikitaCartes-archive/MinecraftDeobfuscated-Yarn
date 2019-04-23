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

    public static <T> void forEachMatching(Iterable<T> iterable, String string, Function<T, Identifier> function, Consumer<T> consumer) {
        boolean bl = string.indexOf(58) > -1;
        for (T object : iterable) {
            Identifier identifier = function.apply(object);
            if (bl) {
                String string2 = identifier.toString();
                if (!string2.startsWith(string)) continue;
                consumer.accept(object);
                continue;
            }
            if (!identifier.getNamespace().startsWith(string) && (!identifier.getNamespace().equals("minecraft") || !identifier.getPath().startsWith(string))) continue;
            consumer.accept(object);
        }
    }

    public static <T> void forEachMatching(Iterable<T> iterable, String string, String string2, Function<T, Identifier> function, Consumer<T> consumer) {
        if (string.isEmpty()) {
            iterable.forEach(consumer);
        } else {
            String string3 = Strings.commonPrefix(string, string2);
            if (!string3.isEmpty()) {
                String string4 = string.substring(string3.length());
                CommandSource.forEachMatching(iterable, string4, function, consumer);
            }
        }
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder, String string) {
        String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(iterable, string2, string, identifier -> identifier, identifier -> suggestionsBuilder.suggest(string + identifier));
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(iterable, string, identifier -> identifier, identifier -> suggestionsBuilder.suggest(identifier.toString()));
        return suggestionsBuilder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Iterable<T> iterable, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(iterable, string, function, object -> suggestionsBuilder.suggest(((Identifier)function.apply(object)).toString(), (Message)function2.apply(object)));
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> stream, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestIdentifiers(stream::iterator, suggestionsBuilder);
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Stream<T> stream, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2) {
        return CommandSource.suggestFromIdentifier(stream::iterator, suggestionsBuilder, function, function2);
    }

    public static CompletableFuture<Suggestions> suggestPositions(String string, Collection<RelativePosition> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate) {
        ArrayList<String> list;
        block4: {
            String[] strings;
            block5: {
                block3: {
                    list = Lists.newArrayList();
                    if (!Strings.isNullOrEmpty(string)) break block3;
                    for (RelativePosition relativePosition : collection) {
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
                for (RelativePosition relativePosition2 : collection) {
                    String string3 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
                    if (!predicate.test(string3)) continue;
                    list.add(strings[0] + " " + relativePosition2.y);
                    list.add(string3);
                }
                break block4;
            }
            if (strings.length != 2) break block4;
            for (RelativePosition relativePosition2 : collection) {
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

        public RelativePosition(String string, String string2, String string3) {
            this.x = string;
            this.y = string2;
            this.z = string3;
        }
    }
}

