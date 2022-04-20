package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AngleArgumentType implements ArgumentType<AngleArgumentType.Angle> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
	public static final SimpleCommandExceptionType INCOMPLETE_ANGLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.angle.incomplete"));
	public static final SimpleCommandExceptionType INVALID_ANGLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.angle.invalid"));

	public static AngleArgumentType angle() {
		return new AngleArgumentType();
	}

	public static float getAngle(CommandContext<ServerCommandSource> context, String name) {
		return context.<AngleArgumentType.Angle>getArgument(name, AngleArgumentType.Angle.class).getAngle(context.getSource());
	}

	public AngleArgumentType.Angle parse(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw INCOMPLETE_ANGLE_EXCEPTION.createWithContext(stringReader);
		} else {
			boolean bl = CoordinateArgument.isRelative(stringReader);
			float f = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0F;
			if (!Float.isNaN(f) && !Float.isInfinite(f)) {
				return new AngleArgumentType.Angle(f, bl);
			} else {
				throw INVALID_ANGLE_EXCEPTION.createWithContext(stringReader);
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
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
