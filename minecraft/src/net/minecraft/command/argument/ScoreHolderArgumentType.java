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
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ScoreHolderArgumentType implements ArgumentType<ScoreHolderArgumentType.ScoreHolder> {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);

		try {
			entitySelectorReader.read();
		} catch (CommandSyntaxException var5) {
		}

		return entitySelectorReader.listSuggestions(
			suggestionsBuilder, suggestionsBuilderx -> CommandSource.suggestMatching(commandContext.getSource().getPlayerNames(), suggestionsBuilderx)
		);
	};
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
	private static final SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.scoreHolder.empty")
	);
	private static final byte field_32470 = 1;
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
				return (serverCommandSource, supplier) -> {
					Collection<String> collectionx = (Collection<String>)supplier.get();
					if (collectionx.isEmpty()) {
						throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
					} else {
						return collectionx;
					}
				};
			} else {
				Collection<String> collection = Collections.singleton(string);
				return (serverCommandSource, supplier) -> collection;
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	@FunctionalInterface
	public interface ScoreHolder {
		Collection<String> getNames(ServerCommandSource source, Supplier<Collection<String>> supplier) throws CommandSyntaxException;
	}

	public static class SelectorScoreHolder implements ScoreHolderArgumentType.ScoreHolder {
		private final EntitySelector selector;

		public SelectorScoreHolder(EntitySelector entitySelector) {
			this.selector = entitySelector;
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

	public static class Serializer implements ArgumentSerializer<ScoreHolderArgumentType> {
		public void toPacket(ScoreHolderArgumentType scoreHolderArgumentType, PacketByteBuf packetByteBuf) {
			byte b = 0;
			if (scoreHolderArgumentType.multiple) {
				b = (byte)(b | 1);
			}

			packetByteBuf.writeByte(b);
		}

		public ScoreHolderArgumentType fromPacket(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			boolean bl = (b & 1) != 0;
			return new ScoreHolderArgumentType(bl);
		}

		public void toJson(ScoreHolderArgumentType scoreHolderArgumentType, JsonObject jsonObject) {
			jsonObject.addProperty("amount", scoreHolderArgumentType.multiple ? "multiple" : "single");
		}
	}
}
