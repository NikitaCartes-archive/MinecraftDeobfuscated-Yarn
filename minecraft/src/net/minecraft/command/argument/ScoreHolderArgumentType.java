package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class ScoreHolderArgumentType implements ArgumentType<ScoreHolderArgumentType.ScoreHolders> {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, EntitySelectorReader.shouldAllowAtSelectors(context.getSource()));

		try {
			entitySelectorReader.read();
		} catch (CommandSyntaxException var5) {
		}

		return entitySelectorReader.listSuggestions(builder, builderx -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builderx));
	};
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
	private static final SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.scoreHolder.empty"));
	final boolean multiple;

	public ScoreHolderArgumentType(boolean multiple) {
		this.multiple = multiple;
	}

	public static ScoreHolder getScoreHolder(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return (ScoreHolder)getScoreHolders(context, name).iterator().next();
	}

	public static Collection<ScoreHolder> getScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getScoreHolders(context, name, Collections::emptyList);
	}

	public static Collection<ScoreHolder> getScoreboardScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getScoreHolders(context, name, context.getSource().getServer().getScoreboard()::getKnownScoreHolders);
	}

	public static Collection<ScoreHolder> getScoreHolders(CommandContext<ServerCommandSource> context, String name, Supplier<Collection<ScoreHolder>> players) throws CommandSyntaxException {
		Collection<ScoreHolder> collection = context.<ScoreHolderArgumentType.ScoreHolders>getArgument(name, ScoreHolderArgumentType.ScoreHolders.class)
			.getNames(context.getSource(), players);
		if (collection.isEmpty()) {
			throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
		} else {
			return collection;
		}
	}

	public static ScoreHolderArgumentType scoreHolder() {
		return new ScoreHolderArgumentType(false);
	}

	public static ScoreHolderArgumentType scoreHolders() {
		return new ScoreHolderArgumentType(true);
	}

	public ScoreHolderArgumentType.ScoreHolders parse(StringReader stringReader) throws CommandSyntaxException {
		return this.parse(stringReader, true);
	}

	public <S> ScoreHolderArgumentType.ScoreHolders parse(StringReader stringReader, S object) throws CommandSyntaxException {
		return this.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
	}

	private ScoreHolderArgumentType.ScoreHolders parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == '@') {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, allowAtSelectors);
			EntitySelector entitySelector = entitySelectorReader.read();
			if (!this.multiple && entitySelector.getLimit() > 1) {
				throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.createWithContext(reader);
			} else {
				return new ScoreHolderArgumentType.SelectorScoreHolders(entitySelector);
			}
		} else {
			int i = reader.getCursor();

			while (reader.canRead() && reader.peek() != ' ') {
				reader.skip();
			}

			String string = reader.getString().substring(i, reader.getCursor());
			if (string.equals("*")) {
				return (source, players) -> {
					Collection<ScoreHolder> collection = (Collection<ScoreHolder>)players.get();
					if (collection.isEmpty()) {
						throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
					} else {
						return collection;
					}
				};
			} else {
				List<ScoreHolder> list = List.of(ScoreHolder.fromName(string));
				if (string.startsWith("#")) {
					return (source, players) -> list;
				} else {
					try {
						UUID uUID = UUID.fromString(string);
						return (source, holders) -> {
							MinecraftServer minecraftServer = source.getServer();
							ScoreHolder scoreHolder = null;
							List<ScoreHolder> list2 = null;

							for (ServerWorld serverWorld : minecraftServer.getWorlds()) {
								Entity entity = serverWorld.getEntity(uUID);
								if (entity != null) {
									if (scoreHolder == null) {
										scoreHolder = entity;
									} else {
										if (list2 == null) {
											list2 = new ArrayList();
											list2.add(scoreHolder);
										}

										list2.add(entity);
									}
								}
							}

							if (list2 != null) {
								return list2;
							} else {
								return scoreHolder != null ? List.of(scoreHolder) : list;
							}
						};
					} catch (IllegalArgumentException var7) {
						return (source, holders) -> {
							MinecraftServer minecraftServer = source.getServer();
							ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(string);
							return serverPlayerEntity != null ? List.of(serverPlayerEntity) : list;
						};
					}
				}
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	@FunctionalInterface
	public interface ScoreHolders {
		Collection<ScoreHolder> getNames(ServerCommandSource source, Supplier<Collection<ScoreHolder>> holders) throws CommandSyntaxException;
	}

	public static class SelectorScoreHolders implements ScoreHolderArgumentType.ScoreHolders {
		private final EntitySelector selector;

		public SelectorScoreHolders(EntitySelector selector) {
			this.selector = selector;
		}

		@Override
		public Collection<ScoreHolder> getNames(ServerCommandSource serverCommandSource, Supplier<Collection<ScoreHolder>> supplier) throws CommandSyntaxException {
			List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
			if (list.isEmpty()) {
				throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
			} else {
				return List.copyOf(list);
			}
		}
	}

	public static class Serializer implements ArgumentSerializer<ScoreHolderArgumentType, ScoreHolderArgumentType.Serializer.Properties> {
		private static final byte MULTIPLE_FLAG = 1;

		public void writePacket(ScoreHolderArgumentType.Serializer.Properties properties, PacketByteBuf packetByteBuf) {
			int i = 0;
			if (properties.multiple) {
				i |= 1;
			}

			packetByteBuf.writeByte(i);
		}

		public ScoreHolderArgumentType.Serializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			boolean bl = (b & 1) != 0;
			return new ScoreHolderArgumentType.Serializer.Properties(bl);
		}

		public void writeJson(ScoreHolderArgumentType.Serializer.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("amount", properties.multiple ? "multiple" : "single");
		}

		public ScoreHolderArgumentType.Serializer.Properties getArgumentTypeProperties(ScoreHolderArgumentType scoreHolderArgumentType) {
			return new ScoreHolderArgumentType.Serializer.Properties(scoreHolderArgumentType.multiple);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<ScoreHolderArgumentType> {
			final boolean multiple;

			Properties(final boolean multiple) {
				this.multiple = multiple;
			}

			public ScoreHolderArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
				return new ScoreHolderArgumentType(this.multiple);
			}

			@Override
			public ArgumentSerializer<ScoreHolderArgumentType, ?> getSerializer() {
				return Serializer.this;
			}
		}
	}
}
