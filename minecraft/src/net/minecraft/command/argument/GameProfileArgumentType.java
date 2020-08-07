package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class GameProfileArgumentType implements ArgumentType<GameProfileArgumentType.GameProfileArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
	public static final SimpleCommandExceptionType UNKNOWN_PLAYER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.player.unknown"));

	public static Collection<GameProfile> getProfileArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<GameProfileArgumentType.GameProfileArgument>getArgument(string, GameProfileArgumentType.GameProfileArgument.class)
			.getNames(commandContext.getSource());
	}

	public static GameProfileArgumentType gameProfile() {
		return new GameProfileArgumentType();
	}

	public GameProfileArgumentType.GameProfileArgument method_9331(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
			EntitySelector entitySelector = entitySelectorReader.read();
			if (entitySelector.includesNonPlayers()) {
				throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.create();
			} else {
				return new GameProfileArgumentType.SelectorBacked(entitySelector);
			}
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String string = stringReader.getString().substring(i, stringReader.getCursor());
			return serverCommandSource -> {
				GameProfile gameProfile = serverCommandSource.getMinecraftServer().getUserCache().findByName(string);
				if (gameProfile == null) {
					throw UNKNOWN_PLAYER_EXCEPTION.create();
				} else {
					return Collections.singleton(gameProfile);
				}
			};
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSource) {
			StringReader stringReader = new StringReader(builder.getInput());
			stringReader.setCursor(builder.getStart());
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);

			try {
				entitySelectorReader.read();
			} catch (CommandSyntaxException var6) {
			}

			return entitySelectorReader.listSuggestions(
				builder, suggestionsBuilder -> CommandSource.suggestMatching(((CommandSource)context.getSource()).getPlayerNames(), suggestionsBuilder)
			);
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	@FunctionalInterface
	public interface GameProfileArgument {
		Collection<GameProfile> getNames(ServerCommandSource serverCommandSource) throws CommandSyntaxException;
	}

	public static class SelectorBacked implements GameProfileArgumentType.GameProfileArgument {
		private final EntitySelector selector;

		public SelectorBacked(EntitySelector entitySelector) {
			this.selector = entitySelector;
		}

		@Override
		public Collection<GameProfile> getNames(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
			List<ServerPlayerEntity> list = this.selector.getPlayers(serverCommandSource);
			if (list.isEmpty()) {
				throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
			} else {
				List<GameProfile> list2 = Lists.<GameProfile>newArrayList();

				for (ServerPlayerEntity serverPlayerEntity : list) {
					list2.add(serverPlayerEntity.getGameProfile());
				}

				return list2;
			}
		}
	}
}
