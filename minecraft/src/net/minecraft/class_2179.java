package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class class_2179 implements ArgumentType<class_2487> {
	private static final Collection<String> field_9843 = Arrays.asList("{}", "{foo=bar}");

	private class_2179() {
	}

	public static class_2179 method_9284() {
		return new class_2179();
	}

	public static <S> class_2487 method_9285(CommandContext<S> commandContext, String string) {
		return commandContext.getArgument(string, class_2487.class);
	}

	public class_2487 method_9286(StringReader stringReader) throws CommandSyntaxException {
		return new class_2522(stringReader).method_10727();
	}

	@Override
	public Collection<String> getExamples() {
		return field_9843;
	}
}
