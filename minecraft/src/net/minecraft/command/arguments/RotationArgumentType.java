package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.ServerCommandSource;

public class RotationArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
	public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("argument.rotation.incomplete")
	);

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
