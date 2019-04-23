/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

public class EntityArgumentType
implements ArgumentType<EntitySelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.toomany", new Object[0]));
    public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.toomany", new Object[0]));
    public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.entities", new Object[0]));
    public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.notfound.entity", new Object[0]));
    public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.notfound.player", new Object[0]));
    public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.not_allowed", new Object[0]));
    private final boolean singleTarget;
    private final boolean playersOnly;

    protected EntityArgumentType(boolean bl, boolean bl2) {
        this.singleTarget = bl;
        this.playersOnly = bl2;
    }

    public static EntityArgumentType entity() {
        return new EntityArgumentType(true, false);
    }

    public static Entity getEntity(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, EntitySelector.class).getEntity(commandContext.getSource());
    }

    public static EntityArgumentType entities() {
        return new EntityArgumentType(false, false);
    }

    public static Collection<? extends Entity> getEntities(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Collection<? extends Entity> collection = EntityArgumentType.getOptionalEntities(commandContext, string);
        if (collection.isEmpty()) {
            throw ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection;
    }

    public static Collection<? extends Entity> getOptionalEntities(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, EntitySelector.class).getEntities(commandContext.getSource());
    }

    public static Collection<ServerPlayerEntity> getOptionalPlayers(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, EntitySelector.class).getPlayers(commandContext.getSource());
    }

    public static EntityArgumentType player() {
        return new EntityArgumentType(true, true);
    }

    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, EntitySelector.class).getPlayer(commandContext.getSource());
    }

    public static EntityArgumentType players() {
        return new EntityArgumentType(false, true);
    }

    public static Collection<ServerPlayerEntity> getPlayers(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        List<ServerPlayerEntity> list = commandContext.getArgument(string, EntitySelector.class).getPlayers(commandContext.getSource());
        if (list.isEmpty()) {
            throw PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list;
    }

    public EntitySelector method_9318(StringReader stringReader) throws CommandSyntaxException {
        boolean i = false;
        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
        EntitySelector entitySelector = entitySelectorReader.read();
        if (entitySelector.getCount() > 1 && this.singleTarget) {
            if (this.playersOnly) {
                stringReader.setCursor(0);
                throw TOO_MANY_PLAYERS_EXCEPTION.createWithContext(stringReader);
            }
            stringReader.setCursor(0);
            throw TOO_MANY_ENTITIES_EXCEPTION.createWithContext(stringReader);
        }
        if (entitySelector.includesNonPlayers() && this.playersOnly && !entitySelector.isSenderOnly()) {
            stringReader.setCursor(0);
            throw PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext(stringReader);
        }
        return entitySelector;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder2) {
        if (commandContext.getSource() instanceof CommandSource) {
            StringReader stringReader = new StringReader(suggestionsBuilder2.getInput());
            stringReader.setCursor(suggestionsBuilder2.getStart());
            CommandSource commandSource = (CommandSource)commandContext.getSource();
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));
            try {
                entitySelectorReader.read();
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return entitySelectorReader.listSuggestions(suggestionsBuilder2, (SuggestionsBuilder suggestionsBuilder) -> {
                Collection<String> collection = commandSource.getPlayerNames();
                Collection<String> iterable = this.playersOnly ? collection : Iterables.concat(collection, commandSource.getEntitySuggestions());
                CommandSource.suggestMatching(iterable, suggestionsBuilder);
            });
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9318(stringReader);
    }

    public static class Serializer
    implements ArgumentSerializer<EntityArgumentType> {
        public void method_9320(EntityArgumentType entityArgumentType, PacketByteBuf packetByteBuf) {
            byte b = 0;
            if (entityArgumentType.singleTarget) {
                b = (byte)(b | 1);
            }
            if (entityArgumentType.playersOnly) {
                b = (byte)(b | 2);
            }
            packetByteBuf.writeByte(b);
        }

        public EntityArgumentType method_9321(PacketByteBuf packetByteBuf) {
            byte b = packetByteBuf.readByte();
            return new EntityArgumentType((b & 1) != 0, (b & 2) != 0);
        }

        public void method_9319(EntityArgumentType entityArgumentType, JsonObject jsonObject) {
            jsonObject.addProperty("amount", entityArgumentType.singleTarget ? "single" : "multiple");
            jsonObject.addProperty("type", entityArgumentType.playersOnly ? "players" : "entities");
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
            return this.method_9321(packetByteBuf);
        }
    }
}

