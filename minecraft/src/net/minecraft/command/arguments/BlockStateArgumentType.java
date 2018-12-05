package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2247;
import net.minecraft.class_2259;
import net.minecraft.server.command.ServerCommandSource;

public class BlockStateArgumentType implements ArgumentType<class_2247> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

	public static BlockStateArgumentType create() {
		return new BlockStateArgumentType();
	}

	public class_2247 method_9654(StringReader stringReader) throws CommandSyntaxException {
		class_2259 lv = new class_2259(stringReader, false).method_9678(true);
		return new class_2247(lv.method_9669(), lv.method_9692().keySet(), lv.method_9694());
	}

	public static class_2247 method_9655(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, class_2247.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2259 lv = new class_2259(stringReader, false);

		try {
			lv.method_9678(true);
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_9666(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
