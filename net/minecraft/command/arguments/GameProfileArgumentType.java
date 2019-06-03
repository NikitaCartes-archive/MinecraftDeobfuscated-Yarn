/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class GameProfileArgumentType
implements ArgumentType<GameProfileArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
    public static final SimpleCommandExceptionType UNKNOWN_PLAYER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.player.unknown", new Object[0]));

    public static Collection<GameProfile> getProfileArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, GameProfileArgument.class).getNames(commandContext.getSource());
    }

    public static GameProfileArgumentType create() {
        return new GameProfileArgumentType();
    }

    public GameProfileArgument method_9331(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '@') {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
            EntitySelector entitySelector = entitySelectorReader.read();
            if (entitySelector.includesNonPlayers()) {
                throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.create();
            }
            return new class_2193(entitySelector);
        }
        int i = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        return serverCommandSource -> {
            GameProfile gameProfile = serverCommandSource.getMinecraftServer().getUserCache().findByName(string);
            if (gameProfile == null) {
                throw UNKNOWN_PLAYER_EXCEPTION.create();
            }
            return Collections.singleton(gameProfile);
        };
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder2) {
        if (commandContext.getSource() instanceof CommandSource) {
            StringReader stringReader = new StringReader(suggestionsBuilder2.getInput());
            stringReader.setCursor(suggestionsBuilder2.getStart());
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
            try {
                entitySelectorReader.read();
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return entitySelectorReader.listSuggestions(suggestionsBuilder2, (SuggestionsBuilder suggestionsBuilder) -> CommandSource.suggestMatching(((CommandSource)commandContext.getSource()).getPlayerNames(), suggestionsBuilder));
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9331(stringReader);
    }

    public static class class_2193
    implements GameProfileArgument {
        private final EntitySelector field_9870;

        public class_2193(EntitySelector entitySelector) {
            this.field_9870 = entitySelector;
        }

        @Override
        public Collection<GameProfile> getNames(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            List<ServerPlayerEntity> list = this.field_9870.getPlayers(serverCommandSource);
            if (list.isEmpty()) {
                throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
            }
            ArrayList<GameProfile> list2 = Lists.newArrayList();
            for (ServerPlayerEntity serverPlayerEntity : list) {
                list2.add(serverPlayerEntity.getGameProfile());
            }
            return list2;
        }
    }

    @FunctionalInterface
    public static interface GameProfileArgument {
        public Collection<GameProfile> getNames(ServerCommandSource var1) throws CommandSyntaxException;
    }
}

