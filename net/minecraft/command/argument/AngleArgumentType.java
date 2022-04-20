/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AngleArgumentType
implements ArgumentType<Angle> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
    public static final SimpleCommandExceptionType INCOMPLETE_ANGLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.angle.incomplete"));
    public static final SimpleCommandExceptionType INVALID_ANGLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.angle.invalid"));

    public static AngleArgumentType angle() {
        return new AngleArgumentType();
    }

    public static float getAngle(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Angle.class).getAngle(context.getSource());
    }

    @Override
    public Angle parse(StringReader stringReader) throws CommandSyntaxException {
        float f;
        if (!stringReader.canRead()) {
            throw INCOMPLETE_ANGLE_EXCEPTION.createWithContext(stringReader);
        }
        boolean bl = CoordinateArgument.isRelative(stringReader);
        float f2 = f = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0f;
        if (Float.isNaN(f) || Float.isInfinite(f)) {
            throw INVALID_ANGLE_EXCEPTION.createWithContext(stringReader);
        }
        return new Angle(f, bl);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static final class Angle {
        private final float angle;
        private final boolean relative;

        Angle(float angle, boolean relative) {
            this.angle = angle;
            this.relative = relative;
        }

        public float getAngle(ServerCommandSource source) {
            return MathHelper.wrapDegrees(this.relative ? this.angle + source.getRotation().y : this.angle);
        }
    }
}

