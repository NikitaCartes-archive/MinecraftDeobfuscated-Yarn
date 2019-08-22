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

public class BlockStateArgumentType implements ArgumentType<BlockStateArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

	public static BlockStateArgumentType blockState() {
		return new BlockStateArgumentType();
	}

	public BlockStateArgument method_9654(StringReader stringReader) throws CommandSyntaxException {
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, false).parse(true);
		return new BlockStateArgument(blockArgumentParser.getBlockState(), blockArgumentParser.getBlockProperties().keySet(), blockArgumentParser.getNbtData());
	}

	public static BlockStateArgument getBlockState(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, BlockStateArgument.class);
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
