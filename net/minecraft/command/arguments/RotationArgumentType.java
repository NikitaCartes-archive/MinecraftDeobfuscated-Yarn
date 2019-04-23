/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.arguments.CoordinateArgument;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.ServerCommandSource;

public class RotationArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
    public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.rotation.incomplete", new Object[0]));

    public static RotationArgumentType create() {
        return new RotationArgumentType();
    }

    public static PosArgument getRotation(CommandContext<ServerCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, PosArgument.class);
    }

    public PosArgument method_9718(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
        }
        CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, false);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
        }
        stringReader.skip();
        CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, false);
        return new DefaultPosArgument(coordinateArgument2, coordinateArgument, new CoordinateArgument(true, 0.0));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9718(stringReader);
    }
}

