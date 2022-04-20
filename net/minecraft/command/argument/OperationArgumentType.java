/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class OperationArgumentType
implements ArgumentType<Operation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
    private static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(Text.method_43471("arguments.operation.invalid"));
    private static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("arguments.operation.div0"));

    public static OperationArgumentType operation() {
        return new OperationArgumentType();
    }

    public static Operation getOperation(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Operation.class);
    }

    @Override
    public Operation parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead()) {
            int i = stringReader.getCursor();
            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }
            return OperationArgumentType.getOperator(stringReader.getString().substring(i, stringReader.getCursor()));
        }
        throw INVALID_OPERATION.create();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static Operation getOperator(String operator) throws CommandSyntaxException {
        if (operator.equals("><")) {
            return (a, b) -> {
                int i = a.getScore();
                a.setScore(b.getScore());
                b.setScore(i);
            };
        }
        return OperationArgumentType.getIntOperator(operator);
    }

    private static IntOperator getIntOperator(String operator) throws CommandSyntaxException {
        switch (operator) {
            case "=": {
                return (a, b) -> b;
            }
            case "+=": {
                return (a, b) -> a + b;
            }
            case "-=": {
                return (a, b) -> a - b;
            }
            case "*=": {
                return (a, b) -> a * b;
            }
            case "/=": {
                return (a, b) -> {
                    if (b == 0) {
                        throw DIVISION_ZERO_EXCEPTION.create();
                    }
                    return MathHelper.floorDiv(a, b);
                };
            }
            case "%=": {
                return (a, b) -> {
                    if (b == 0) {
                        throw DIVISION_ZERO_EXCEPTION.create();
                    }
                    return MathHelper.floorMod(a, b);
                };
            }
            case "<": {
                return Math::min;
            }
            case ">": {
                return Math::max;
            }
        }
        throw INVALID_OPERATION.create();
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    @FunctionalInterface
    public static interface Operation {
        public void apply(ScoreboardPlayerScore var1, ScoreboardPlayerScore var2) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface IntOperator
    extends Operation {
        public int apply(int var1, int var2) throws CommandSyntaxException;

        @Override
        default public void apply(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException {
            scoreboardPlayerScore.setScore(this.apply(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore()));
        }
    }
}

