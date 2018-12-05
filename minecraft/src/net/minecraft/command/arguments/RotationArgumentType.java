package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.class_2267;
import net.minecraft.class_2278;
import net.minecraft.class_2280;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;

public class RotationArgumentType implements ArgumentType<class_2267> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
	public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.rotation.incomplete")
	);

	public static RotationArgumentType create() {
		return new RotationArgumentType();
	}

	public static class_2267 getRotationArgument(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, class_2267.class);
	}

	public class_2267 method_9718(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
		} else {
			class_2278 lv = class_2278.method_9743(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv2 = class_2278.method_9743(stringReader, false);
				return new class_2280(lv2, lv, new class_2278(true, 0.0));
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
