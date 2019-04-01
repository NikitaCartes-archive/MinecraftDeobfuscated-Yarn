package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class class_2212 implements ArgumentType<class_2520> {
	private static final Collection<String> field_9918 = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

	private class_2212() {
	}

	public static class_2212 method_9389() {
		return new class_2212();
	}

	public static <S> class_2520 method_9390(CommandContext<S> commandContext, String string) {
		return commandContext.getArgument(string, class_2520.class);
	}

	public class_2520 method_9388(StringReader stringReader) throws CommandSyntaxException {
		return new class_2522(stringReader).method_10723();
	}

	@Override
	public Collection<String> getExamples() {
		return field_9918;
	}
}
