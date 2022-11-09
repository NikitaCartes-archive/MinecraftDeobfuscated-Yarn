package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;

public class BlockStateArgumentType implements ArgumentType<BlockStateArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
	private final RegistryWrapper<Block> registryWrapper;

	public BlockStateArgumentType(CommandRegistryAccess commandRegistryAccess) {
		this.registryWrapper = commandRegistryAccess.createWrapper(RegistryKeys.BLOCK);
	}

	public static BlockStateArgumentType blockState(CommandRegistryAccess commandRegistryAccess) {
		return new BlockStateArgumentType(commandRegistryAccess);
	}

	public BlockStateArgument parse(StringReader stringReader) throws CommandSyntaxException {
		BlockArgumentParser.BlockResult blockResult = BlockArgumentParser.block(this.registryWrapper, stringReader, true);
		return new BlockStateArgument(blockResult.blockState(), blockResult.properties().keySet(), blockResult.nbt());
	}

	public static BlockStateArgument getBlockState(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, BlockStateArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return BlockArgumentParser.getSuggestions(this.registryWrapper, builder, false, true);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
