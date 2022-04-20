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

public class RotationArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
	public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.rotation.incomplete")
	);

	public static RotationArgumentType rotation() {
		return new RotationArgumentType();
	}

	public static PosArgument getRotation(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, PosArgument.class);
	}

	public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
		} else {
			CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, false);
				return new DefaultPosArgument(coordinateArgument2, coordinateArgument, new CoordinateArgument(true, 0.0));
			} else {
				stringReader.setCursor(i);
				throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
