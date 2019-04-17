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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class ColumnPosArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
	public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.pos2d.incomplete")
	);

	public static ColumnPosArgumentType create() {
		return new ColumnPosArgumentType();
	}

	public static ColumnPosArgumentType.ColumnPos getColumnPos(CommandContext<ServerCommandSource> commandContext, String string) {
		BlockPos blockPos = commandContext.<PosArgument>getArgument(string, PosArgument.class).toAbsoluteBlockPos(commandContext.getSource());
		return new ColumnPosArgumentType.ColumnPos(blockPos.getX(), blockPos.getZ());
	}

	public PosArgument method_9703(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
		} else {
			CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader);
				return new DefaultPosArgument(coordinateArgument, new CoordinateArgument(true, 0.0), coordinateArgument2);
			} else {
				stringReader.setCursor(i);
				throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
			}
		}
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
				collection = ((CommandSource)commandContext.getSource()).getBlockPositionSuggestions();
			}

			return CommandSource.suggestColumnPositions(string, collection, suggestionsBuilder, CommandManager.getCommandValidator(this::method_9703));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class ColumnPos {
		public final int x;
		public final int z;

		public ColumnPos(int i, int j) {
			this.x = i;
			this.z = j;
		}

		public String toString() {
			return "[" + this.x + ", " + this.z + "]";
		}
	}
}
