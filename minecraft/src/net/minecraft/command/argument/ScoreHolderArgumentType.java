package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ScoreHolderArgumentType implements ArgumentType<ScoreHolderArgumentType.ScoreHolder> {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);

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

	public static String getScoreHolder(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return (String)getScoreHolders(context, name).iterator().next();
	}

	public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getScoreHolders(context, name, Collections::emptyList);
	}

	public static Collection<String> getScoreboardScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getScoreHolders(context, name, context.getSource().getServer().getScoreboard()::getKnownPlayers);
	}

	public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> context, String name, Supplier<Collection<String>> players) throws CommandSyntaxException {
		Collection<String> collection = context.<ScoreHolderArgumentType.ScoreHolder>getArgument(name, ScoreHolderArgumentType.ScoreHolder.class)
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

	public ScoreHolderArgumentType.ScoreHolder parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
			EntitySelector entitySelector = entitySelectorReader.read();
			if (!this.multiple && entitySelector.getLimit() > 1) {
				throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
			} else {
				return new ScoreHolderArgumentType.SelectorScoreHolder(entitySelector);
			}
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String string = stringReader.getString().substring(i, stringReader.getCursor());
			if (string.equals("*")) {
				return (source, players) -> {
					Collection<String> collectionx = (Collection<String>)players.get();
					if (collectionx.isEmpty()) {
						throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
					} else {
						return collectionx;
					}
				};
			} else {
				Collection<String> collection = Collections.singleton(string);
				return (source, players) -> collection;
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	@FunctionalInterface
	public interface ScoreHolder {
		Collection<String> getNames(ServerCommandSource source, Supplier<Collection<String>> players) throws CommandSyntaxException;
	}

	public static class SelectorScoreHolder implements ScoreHolderArgumentType.ScoreHolder {
		private final EntitySelector selector;

		public SelectorScoreHolder(EntitySelector selector) {
			this.selector = selector;
		}

		@Override
		public Collection<String> getNames(ServerCommandSource serverCommandSource, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
			List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
			if (list.isEmpty()) {
				throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
			} else {
				List<String> list2 = Lists.<String>newArrayList();

				for (Entity entity : list) {
					list2.add(entity.getEntityName());
				}

				return list2;
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

			Properties(boolean multiple) {
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
