/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class ScoreHolderArgumentType
implements ArgumentType<ScoreHolder> {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder2) -> {
        StringReader stringReader = new StringReader(suggestionsBuilder2.getInput());
        stringReader.setCursor(suggestionsBuilder2.getStart());
        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
        try {
            entitySelectorReader.read();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return entitySelectorReader.listSuggestions(suggestionsBuilder2, (SuggestionsBuilder suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getPlayerNames(), suggestionsBuilder));
    };
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
    private static final SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.scoreHolder.empty", new Object[0]));
    private final boolean multiple;

    public ScoreHolderArgumentType(boolean bl) {
        this.multiple = bl;
    }

    public static String getScoreHolder(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(commandContext, string).iterator().next();
    }

    public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(commandContext, string, Collections::emptyList);
    }

    public static Collection<String> getScoreboardScoreHolders(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(commandContext, string, commandContext.getSource().getMinecraftServer().getScoreboard()::getKnownPlayers);
    }

    public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> commandContext, String string, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
        Collection<String> collection = commandContext.getArgument(string, ScoreHolder.class).getNames(commandContext.getSource(), supplier);
        if (collection.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection;
    }

    public static ScoreHolderArgumentType scoreHolder() {
        return new ScoreHolderArgumentType(false);
    }

    public static ScoreHolderArgumentType scoreHolders() {
        return new ScoreHolderArgumentType(true);
    }

    public ScoreHolder method_9453(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '@') {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
            EntitySelector entitySelector = entitySelectorReader.read();
            if (!this.multiple && entitySelector.getCount() > 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
            }
            return new SelectorScoreHolder(entitySelector);
        }
        int i = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (string.equals("*")) {
            return (serverCommandSource, supplier) -> {
                Collection collection = (Collection)supplier.get();
                if (collection.isEmpty()) {
                    throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
                }
                return collection;
            };
        }
        Set<String> collection = Collections.singleton(string);
        return (serverCommandSource, supplier) -> collection;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9453(stringReader);
    }

    public static class Serializer
    implements ArgumentSerializer<ScoreHolderArgumentType> {
        public void method_9461(ScoreHolderArgumentType scoreHolderArgumentType, PacketByteBuf packetByteBuf) {
            int b = 0;
            if (scoreHolderArgumentType.multiple) {
                b = (byte)(b | 1);
            }
            packetByteBuf.writeByte(b);
        }

        public ScoreHolderArgumentType method_9460(PacketByteBuf packetByteBuf) {
            byte b = packetByteBuf.readByte();
            boolean bl = (b & 1) != 0;
            return new ScoreHolderArgumentType(bl);
        }

        public void method_9459(ScoreHolderArgumentType scoreHolderArgumentType, JsonObject jsonObject) {
            jsonObject.addProperty("amount", scoreHolderArgumentType.multiple ? "multiple" : "single");
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
            return this.method_9460(packetByteBuf);
        }
    }

    public static class SelectorScoreHolder
    implements ScoreHolder {
        private final EntitySelector selector;

        public SelectorScoreHolder(EntitySelector entitySelector) {
            this.selector = entitySelector;
        }

        @Override
        public Collection<String> getNames(ServerCommandSource serverCommandSource, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
            List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
            if (list.isEmpty()) {
                throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
            }
            ArrayList<String> list2 = Lists.newArrayList();
            for (Entity entity : list) {
                list2.add(entity.getEntityName());
            }
            return list2;
        }
    }

    @FunctionalInterface
    public static interface ScoreHolder {
        public Collection<String> getNames(ServerCommandSource var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
    }
}

