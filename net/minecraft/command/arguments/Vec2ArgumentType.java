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
import net.minecraft.command.arguments.CoordinateArgument;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Vec2ArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos2d.incomplete", new Object[0]));
    private final boolean centerIntegers;

    public Vec2ArgumentType(boolean bl) {
        this.centerIntegers = bl;
    }

    public static Vec2ArgumentType create() {
        return new Vec2ArgumentType(true);
    }

    public static Vec2f getVec2(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Vec3d vec3d = commandContext.getArgument(string, PosArgument.class).toAbsolutePos(commandContext.getSource());
        return new Vec2f((float)vec3d.x, (float)vec3d.z);
    }

    public PosArgument method_9725(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
        }
        CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, this.centerIntegers);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
        }
        stringReader.skip();
        CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, this.centerIntegers);
        return new DefaultPosArgument(coordinateArgument, new CoordinateArgument(true, 0.0), coordinateArgument2);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof CommandSource) {
            String string = suggestionsBuilder.getRemaining();
            Collection<CommandSource.RelativePosition> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL) : ((CommandSource)commandContext.getSource()).getPositionSuggestions();
            return CommandSource.suggestColumnPositions(string, collection, suggestionsBuilder, CommandManager.getCommandValidator(this::method_9725));
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9725(stringReader);
    }
}

