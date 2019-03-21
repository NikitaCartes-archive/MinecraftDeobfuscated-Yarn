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
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.PacketByteBuf;

public class EntityArgumentType implements ArgumentType<EntitySelector> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
	public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.toomany")
	);
	public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.player.toomany")
	);
	public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.player.entities")
	);
	public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.notfound.entity")
	);
	public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.notfound.player")
	);
	public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.selector.not_allowed")
	);
	private final boolean singleTarget;
	private final boolean playerOnly;

	protected EntityArgumentType(boolean bl, boolean bl2) {
		this.singleTarget = bl;
		this.playerOnly = bl2;
	}

	public static EntityArgumentType oneEntity() {
		return new EntityArgumentType(true, false);
	}

	public static Entity getEntityArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getEntity(commandContext.getSource());
	}

	public static EntityArgumentType multipleEntities() {
		return new EntityArgumentType(false, false);
	}

	public static Collection<? extends Entity> method_9317(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Collection<? extends Entity> collection = method_9307(commandContext, string);
		if (collection.isEmpty()) {
			throw ENTITY_NOT_FOUND_EXCEPTION.create();
		} else {
			return collection;
		}
	}

	public static Collection<? extends Entity> method_9307(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getEntities(commandContext.getSource());
	}

	public static Collection<ServerPlayerEntity> method_9310(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getPlayers(commandContext.getSource());
	}

	public static EntityArgumentType onePlayer() {
		return new EntityArgumentType(true, true);
	}

	public static ServerPlayerEntity getServerPlayerArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getPlayer(commandContext.getSource());
	}

	public static EntityArgumentType multiplePlayer() {
		return new EntityArgumentType(false, true);
	}

	public static Collection<ServerPlayerEntity> method_9312(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		List<ServerPlayerEntity> list = commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getPlayers(commandContext.getSource());
		if (list.isEmpty()) {
			throw PLAYER_NOT_FOUND_EXCEPTION.create();
		} else {
			return list;
		}
	}

	public EntitySelector method_9318(StringReader stringReader) throws CommandSyntaxException {
		int i = 0;
		EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
		EntitySelector entitySelector = entitySelectorReader.read();
		if (entitySelector.getCount() > 1 && this.singleTarget) {
			if (this.playerOnly) {
				stringReader.setCursor(0);
				throw TOO_MANY_PLAYERS_EXCEPTION.createWithContext(stringReader);
			} else {
				stringReader.setCursor(0);
				throw TOO_MANY_ENTITIES_EXCEPTION.createWithContext(stringReader);
			}
		} else if (entitySelector.includesEntities() && this.playerOnly && !entitySelector.method_9820()) {
			stringReader.setCursor(0);
			throw PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext(stringReader);
		} else {
			return entitySelector;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof CommandSource) {
			StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
			stringReader.setCursor(suggestionsBuilder.getStart());
			CommandSource commandSource = (CommandSource)commandContext.getSource();
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));

			try {
				entitySelectorReader.read();
			} catch (CommandSyntaxException var7) {
			}

			return entitySelectorReader.listSuggestions(suggestionsBuilder, suggestionsBuilderx -> {
				Collection<String> collection = commandSource.getPlayerNames();
				Iterable<String> iterable = (Iterable<String>)(this.playerOnly ? collection : Iterables.concat(collection, commandSource.method_9269()));
				CommandSource.suggestMatching(iterable, suggestionsBuilderx);
			});
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Serializer implements ArgumentSerializer<EntityArgumentType> {
		public void method_9320(EntityArgumentType entityArgumentType, PacketByteBuf packetByteBuf) {
			byte b = 0;
			if (entityArgumentType.singleTarget) {
				b = (byte)(b | 1);
			}

			if (entityArgumentType.playerOnly) {
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
			jsonObject.addProperty("type", entityArgumentType.playerOnly ? "players" : "entities");
		}
	}
}
