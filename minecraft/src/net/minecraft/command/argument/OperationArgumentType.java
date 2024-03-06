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
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class OperationArgumentType implements ArgumentType<OperationArgumentType.Operation> {
	private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
	private static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(Text.translatable("arguments.operation.invalid"));
	private static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("arguments.operation.div0"));

	public static OperationArgumentType operation() {
		return new OperationArgumentType();
	}

	public static OperationArgumentType.Operation getOperation(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, OperationArgumentType.Operation.class);
	}

	public OperationArgumentType.Operation parse(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw INVALID_OPERATION.createWithContext(stringReader);
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

	private static OperationArgumentType.Operation getOperator(String operator) throws CommandSyntaxException {
		return (OperationArgumentType.Operation)(operator.equals("><") ? (a, b) -> {
			int i = a.getScore();
			a.setScore(b.getScore());
			b.setScore(i);
		} : getIntOperator(operator));
	}

	private static OperationArgumentType.IntOperator getIntOperator(String operator) throws CommandSyntaxException {
		return switch (operator) {
			case "=" -> (a, b) -> b;
			case "+=" -> Integer::sum;
			case "-=" -> (a, b) -> a - b;
			case "*=" -> (a, b) -> a * b;
			case "/=" -> (a, b) -> {
			if (b == 0) {
				throw DIVISION_ZERO_EXCEPTION.create();
			} else {
				return MathHelper.floorDiv(a, b);
			}
		};
			case "%=" -> (a, b) -> {
			if (b == 0) {
				throw DIVISION_ZERO_EXCEPTION.create();
			} else {
				return MathHelper.floorMod(a, b);
			}
		};
			case "<" -> Math::min;
			case ">" -> Math::max;
			default -> throw INVALID_OPERATION.create();
		};
	}

	@FunctionalInterface
	interface IntOperator extends OperationArgumentType.Operation {
		int apply(int a, int b) throws CommandSyntaxException;

		@Override
		default void apply(ScoreAccess scoreAccess, ScoreAccess scoreAccess2) throws CommandSyntaxException {
			scoreAccess.setScore(this.apply(scoreAccess.getScore(), scoreAccess2.getScore()));
		}
	}

	@FunctionalInterface
	public interface Operation {
		void apply(ScoreAccess a, ScoreAccess b) throws CommandSyntaxException;
	}
}
