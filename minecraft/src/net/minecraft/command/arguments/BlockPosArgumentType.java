package net.minecraft.command.arguments;

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
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class BlockPosArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.pos.outofworld")
	);

	public static BlockPosArgumentType create() {
		return new BlockPosArgumentType();
	}

	public static BlockPos method_9696(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		BlockPos blockPos = commandContext.<PosArgument>getArgument(string, PosArgument.class).method_9704(commandContext.getSource());
		if (!commandContext.getSource().method_9225().method_8591(blockPos)) {
			throw UNLOADED_EXCEPTION.create();
		} else {
			commandContext.getSource().method_9225();
			if (!ServerWorld.method_8558(blockPos)) {
				throw OUT_OF_WORLD_EXCEPTION.create();
			} else {
				return blockPos;
			}
		}
	}

	public static BlockPos method_9697(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<PosArgument>getArgument(string, PosArgument.class).method_9704(commandContext.getSource());
	}

	public PosArgument method_9699(StringReader stringReader) throws CommandSyntaxException {
		return (PosArgument)(stringReader.canRead() && stringReader.peek() == '^' ? LookingPosArgument.parse(stringReader) : DefaultPosArgument.parse(stringReader));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof CommandSource)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<CommandSource.RelativePosition> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
			} else {
				collection = ((CommandSource)commandContext.getSource()).method_17771();
			}

			return CommandSource.method_9260(string, collection, suggestionsBuilder, ServerCommandManager.getCommandValidator(this::method_9699));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
