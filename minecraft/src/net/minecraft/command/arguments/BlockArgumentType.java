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
import net.minecraft.server.command.ServerCommandSource;

public class BlockArgumentType implements ArgumentType<BlockArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

	public static BlockArgumentType create() {
		return new BlockArgumentType();
	}

	public BlockArgument method_9654(StringReader stringReader) throws CommandSyntaxException {
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, false).parse(true);
		return new BlockArgument(blockArgumentParser.getBlockState(), blockArgumentParser.method_9692().keySet(), blockArgumentParser.getNbtData());
	}

	public static BlockArgument getBlockArgument(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, BlockArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, false);

		try {
			blockArgumentParser.parse(true);
		} catch (CommandSyntaxException var6) {
		}

		return blockArgumentParser.getSuggestions(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
