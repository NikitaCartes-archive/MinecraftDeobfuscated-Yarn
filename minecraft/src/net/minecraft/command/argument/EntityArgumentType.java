package net.minecraft.command.argument;

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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class EntityArgumentType implements ArgumentType<EntitySelector> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
	public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.toomany"));
	public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.player.toomany"));
	public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.player.entities")
	);
	public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.entity.notfound.entity")
	);
	public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.entity.notfound.player")
	);
	public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.entity.selector.not_allowed")
	);
	final boolean singleTarget;
	final boolean playersOnly;

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

	public static Collection<? extends Entity> getOptionalEntities(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<EntitySelector>getArgument(name, EntitySelector.class).getEntities(context.getSource());
	}

	public static Collection<ServerPlayerEntity> getOptionalPlayers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<EntitySelector>getArgument(name, EntitySelector.class).getPlayers(context.getSource());
	}

	public static EntityArgumentType player() {
		return new EntityArgumentType(true, true);
	}

	public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<EntitySelector>getArgument(name, EntitySelector.class).getPlayer(context.getSource());
	}

	public static EntityArgumentType players() {
		return new EntityArgumentType(false, true);
	}

	public static Collection<ServerPlayerEntity> getPlayers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		List<ServerPlayerEntity> list = context.<EntitySelector>getArgument(name, EntitySelector.class).getPlayers(context.getSource());
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
		if (context.getSource() instanceof CommandSource commandSource) {
			StringReader stringReader = new StringReader(builder.getInput());
			stringReader.setCursor(builder.getStart());
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));

			try {
				entitySelectorReader.read();
			} catch (CommandSyntaxException var7) {
			}

			return entitySelectorReader.listSuggestions(builder, builderx -> {
				Collection<String> collection = commandSource.getPlayerNames();
				Iterable<String> iterable = (Iterable<String>)(this.playersOnly ? collection : Iterables.concat(collection, commandSource.getEntitySuggestions()));
				CommandSource.suggestMatching(iterable, builderx);
			});
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Serializer implements ArgumentSerializer<EntityArgumentType, EntityArgumentType.Serializer.Properties> {
		private static final byte SINGLE_FLAG = 1;
		private static final byte PLAYERS_ONLY_FLAG = 2;

		public void writePacket(EntityArgumentType.Serializer.Properties properties, PacketByteBuf packetByteBuf) {
			int i = 0;
			if (properties.single) {
				i |= 1;
			}

			if (properties.playersOnly) {
				i |= 2;
			}

			packetByteBuf.writeByte(i);
		}

		public EntityArgumentType.Serializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			return new EntityArgumentType.Serializer.Properties((b & 1) != 0, (b & 2) != 0);
		}

		public void writeJson(EntityArgumentType.Serializer.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("amount", properties.single ? "single" : "multiple");
			jsonObject.addProperty("type", properties.playersOnly ? "players" : "entities");
		}

		public EntityArgumentType.Serializer.Properties getArgumentTypeProperties(EntityArgumentType entityArgumentType) {
			return new EntityArgumentType.Serializer.Properties(entityArgumentType.singleTarget, entityArgumentType.playersOnly);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<EntityArgumentType> {
			final boolean single;
			final boolean playersOnly;

			Properties(boolean single, boolean playersOnly) {
				this.single = single;
				this.playersOnly = playersOnly;
			}

			public EntityArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
				return new EntityArgumentType(this.single, this.playersOnly);
			}

			@Override
			public ArgumentSerializer<EntityArgumentType, ?> getSerializer() {
				return Serializer.this;
			}
		}
	}
}
