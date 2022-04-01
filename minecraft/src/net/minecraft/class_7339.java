package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class class_7339 implements ArgumentType<GameMode> {
	private static final Collection<String> field_38618 = Arrays.asList("survival", "creative");
	public static final DynamicCommandExceptionType field_38617 = new DynamicCommandExceptionType(
		object -> new TranslatableText("gamemode.gamemodeNotFound", object)
	);

	public static class_7339 method_42960() {
		return new class_7339();
	}

	public static GameMode method_42962(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, GameMode.class);
	}

	public GameMode parse(StringReader stringReader) throws CommandSyntaxException {
		String string = Identifier.fromCommandInput(stringReader).getPath();
		GameMode gameMode = GameMode.byName(string, null);
		if (gameMode == null) {
			throw field_38617.create(string);
		} else {
			return gameMode;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(Arrays.stream(GameMode.values()).map(GameMode::getName), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_38618;
	}
}
