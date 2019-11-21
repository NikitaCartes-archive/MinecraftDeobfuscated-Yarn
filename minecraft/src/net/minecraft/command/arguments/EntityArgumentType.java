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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class EntityArgumentType implements ArgumentType<EntitySelector> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
	public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.entity.toomany"));
	public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.player.toomany"));
	public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.player.entities")
	);
	public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.entity.notfound.entity")
	);
	public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.entity.notfound.player")
	);
	public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.entity.selector.not_allowed")
	);
	private final boolean singleTarget;
	private final boolean playersOnly;

	protected EntityArgumentType(boolean singleTarget, boolean playersOnly) {
		this.singleTarget = singleTarget;
		this.playersOnly = playersOnly;
	}

	public static EntityArgumentType entity() {
		return new EntityArgumentType(true, false);
	}

	public static Entity getEntity(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<EntitySelector>getArgument(name, EntitySelector.class).getEntity(context.getSource());
	}

	public static EntityArgumentType entities() {
		return new EntityArgumentType(false, false);
	}

	public static Collection<? extends Entity> getEntities(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		Collection<? extends Entity> collection = getOptionalEntities(context, name);
		if (collection.isEmpty()) {
			throw ENTITY_NOT_FOUND_EXCEPTION.create();
		} else {
			return collection;
		}
	}

	public static Collection<? extends Entity> getOptionalEntities(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getEntities(commandContext.getSource());
	}

	public static Collection<ServerPlayerEntity> getOptionalPlayers(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getPlayers(commandContext.getSource());
	}

	public static EntityArgumentType player() {
		return new EntityArgumentType(true, true);
	}

	public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getPlayer(commandContext.getSource());
	}

	public static EntityArgumentType players() {
		return new EntityArgumentType(false, true);
	}

	public static Collection<ServerPlayerEntity> getPlayers(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		List<ServerPlayerEntity> list = commandContext.<EntitySelector>getArgument(string, EntitySelector.class).getPlayers(commandContext.getSource());
		if (list.isEmpty()) {
			throw PLAYER_NOT_FOUND_EXCEPTION.create();
		} else {
			return list;
		}
	}

	public EntitySelector parse(StringReader stringReader) throws CommandSyntaxException {
		int i = 0;
		EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
		EntitySelector entitySelector = entitySelectorReader.read();
		if (entitySelector.getLimit() > 1 && this.singleTarget) {
			if (this.playersOnly) {
				stringReader.setCursor(0);
				throw TOO_MANY_PLAYERS_EXCEPTION.createWithContext(stringReader);
			} else {
				stringReader.setCursor(0);
				throw TOO_MANY_ENTITIES_EXCEPTION.createWithContext(stringReader);
			}
		} else if (entitySelector.includesNonPlayers() && this.playersOnly && !entitySelector.isSenderOnly()) {
			stringReader.setCursor(0);
			throw PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext(stringReader);
		} else {
			return entitySelector;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSource) {
			StringReader stringReader = new StringReader(builder.getInput());
			stringReader.setCursor(builder.getStart());
			CommandSource commandSource = (CommandSource)context.getSource();
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));

			try {
				entitySelectorReader.read();
			} catch (CommandSyntaxException var7) {
			}

			return entitySelectorReader.listSuggestions(builder, suggestionsBuilder -> {
				Collection<String> collection = commandSource.getPlayerNames();
				Iterable<String> iterable = (Iterable<String>)(this.playersOnly ? collection : Iterables.concat(collection, commandSource.getEntitySuggestions()));
				CommandSource.suggestMatching(iterable, suggestionsBuilder);
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
		public void toPacket(EntityArgumentType entityArgumentType, PacketByteBuf packetByteBuf) {
			byte b = 0;
			if (entityArgumentType.singleTarget) {
				b = (byte)(b | 1);
			}

			if (entityArgumentType.playersOnly) {
				b = (byte)(b | 2);
			}

			packetByteBuf.writeByte(b);
		}

		public EntityArgumentType fromPacket(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			return new EntityArgumentType((b & 1) != 0, (b & 2) != 0);
		}

		public void toJson(EntityArgumentType entityArgumentType, JsonObject jsonObject) {
			jsonObject.addProperty("amount", entityArgumentType.singleTarget ? "single" : "multiple");
			jsonObject.addProperty("type", entityArgumentType.playersOnly ? "players" : "entities");
		}
	}
}
