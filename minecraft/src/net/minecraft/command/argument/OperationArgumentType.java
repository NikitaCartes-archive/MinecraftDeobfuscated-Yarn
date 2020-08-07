package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class OperationArgumentType implements ArgumentType<OperationArgumentType.Operation> {
	private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
	private static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(new TranslatableText("arguments.operation.invalid"));
	private static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.operation.div0"));

	public static OperationArgumentType operation() {
		return new OperationArgumentType();
	}

	public static OperationArgumentType.Operation getOperation(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.getArgument(string, OperationArgumentType.Operation.class);
	}

	public OperationArgumentType.Operation method_9412(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw INVALID_OPERATION.create();
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			return getOperator(stringReader.getString().substring(i, stringReader.getCursor()));
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	private static OperationArgumentType.Operation getOperator(String string) throws CommandSyntaxException {
		return (OperationArgumentType.Operation)(string.equals("><") ? (scoreboardPlayerScore, scoreboardPlayerScore2) -> {
			int i = scoreboardPlayerScore.getScore();
			scoreboardPlayerScore.setScore(scoreboardPlayerScore2.getScore());
			scoreboardPlayerScore2.setScore(i);
		} : getIntOperator(string));
	}

	private static OperationArgumentType.IntOperator getIntOperator(String string) throws CommandSyntaxException {
		switch (string) {
			case "=":
				return (i, j) -> j;
			case "+=":
				return (i, j) -> i + j;
			case "-=":
				return (i, j) -> i - j;
			case "*=":
				return (i, j) -> i * j;
			case "/=":
				return (i, j) -> {
					if (j == 0) {
						throw DIVISION_ZERO_EXCEPTION.create();
					} else {
						return MathHelper.floorDiv(i, j);
					}
				};
			case "%=":
				return (i, j) -> {
					if (j == 0) {
						throw DIVISION_ZERO_EXCEPTION.create();
					} else {
						return MathHelper.floorMod(i, j);
					}
				};
			case "<":
				return Math::min;
			case ">":
				return Math::max;
			default:
				throw INVALID_OPERATION.create();
		}
	}

	@FunctionalInterface
	interface IntOperator extends OperationArgumentType.Operation {
		int apply(int i, int j) throws CommandSyntaxException;

		@Override
		default void apply(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException {
			scoreboardPlayerScore.setScore(this.apply(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore()));
		}
	}

	@FunctionalInterface
	public interface Operation {
		void apply(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException;
	}
}
