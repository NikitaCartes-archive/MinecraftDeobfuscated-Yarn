package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class SwizzleArgumentType implements ArgumentType<EnumSet<Direction.Axis>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
	private static final SimpleCommandExceptionType INVALID_SWIZZLE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.swizzle.invalid"));

	public static SwizzleArgumentType swizzle() {
		return new SwizzleArgumentType();
	}

	public static EnumSet<Direction.Axis> getSwizzle(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, EnumSet.class);
	}

	public EnumSet<Direction.Axis> parse(StringReader stringReader) throws CommandSyntaxException {
		EnumSet<Direction.Axis> enumSet = EnumSet.noneOf(Direction.Axis.class);

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			char c = stringReader.read();
			Direction.Axis axis;
			switch (c) {
				case 'x':
					axis = Direction.Axis.X;
					break;
				case 'y':
					axis = Direction.Axis.Y;
					break;
				case 'z':
					axis = Direction.Axis.Z;
					break;
				default:
					throw INVALID_SWIZZLE_EXCEPTION.create();
			}

			if (enumSet.contains(axis)) {
				throw INVALID_SWIZZLE_EXCEPTION.create();
			}

			enumSet.add(axis);
		}

		return enumSet;
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
