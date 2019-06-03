package net.minecraft.command.arguments;

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
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

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
	private final boolean multiple;

	public ScoreHolderArgumentType(boolean bl) {
		this.multiple = bl;
	}

	public static String getScoreHolder(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return (String)getScoreHolders(commandContext, string).iterator().next();
	}

	public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return getScoreHolders(commandContext, string, Collections::emptyList);
	}

	public static Collection<String> getScoreboardScoreHolders(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return getScoreHolders(commandContext, string, commandContext.getSource().getMinecraftServer().getScoreboard()::getKnownPlayers);
	}

	public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> commandContext, String string, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
		Collection<String> collection = commandContext.<ScoreHolderArgumentType.ScoreHolder>getArgument(string, ScoreHolderArgumentType.ScoreHolder.class)
			.getNames(commandContext.getSource(), supplier);
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

	public ScoreHolderArgumentType.ScoreHolder method_9453(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
			EntitySelector entitySelector = entitySelectorReader.read();
			if (!this.multiple && entitySelector.getCount() > 1) {
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
		Collection<String> getNames(ServerCommandSource serverCommandSource, Supplier<Collection<String>> supplier) throws CommandSyntaxException;
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
		public void method_9461(ScoreHolderArgumentType scoreHolderArgumentType, PacketByteBuf packetByteBuf) {
			byte b = 0;
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
	}
}
