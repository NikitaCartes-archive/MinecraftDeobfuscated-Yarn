/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.LookingPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;

public class Vec3ArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos3d.incomplete", new Object[0]));
    public static final SimpleCommandExceptionType MIXED_COORDINATE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.mixed", new Object[0]));
    private final boolean centerIntegers;

    public Vec3ArgumentType(boolean bl) {
        this.centerIntegers = bl;
    }

    public static Vec3ArgumentType create() {
        return new Vec3ArgumentType(true);
    }

    public static Vec3ArgumentType create(boolean bl) {
        return new Vec3ArgumentType(bl);
    }

    public static Vec3d getVec3(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, PosArgument.class).toAbsolutePos(commandContext.getSource());
    }

    public static PosArgument getPosArgument(CommandContext<ServerCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, PosArgument.class);
    }

    public PosArgument method_9738(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LookingPosArgument.parse(stringReader);
        }
        return DefaultPosArgument.parse(stringReader, this.centerIntegers);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof CommandSource) {
            String string = suggestionsBuilder.getRemaining();
            Collection<CommandSource.RelativePosition> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL) : ((CommandSource)commandContext.getSource()).getPositionSuggestions();
            return CommandSource.suggestPositions(string, collection, suggestionsBuilder, CommandManager.getCommandValidator(this::method_9738));
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9738(stringReader);
    }
}

