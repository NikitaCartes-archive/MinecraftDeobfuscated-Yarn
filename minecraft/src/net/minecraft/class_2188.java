package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;

public class class_2188 implements ArgumentType<class_2960> {
	private static final Collection<String> field_9865 = Arrays.asList("minecraft:pig", "cow");
	public static final DynamicCommandExceptionType field_9866 = new DynamicCommandExceptionType(object -> new class_2588("entity.notFound", object));

	public static class_2188 method_9324() {
		return new class_2188();
	}

	public static class_2960 method_9322(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return method_9326(commandContext.getArgument(string, class_2960.class));
	}

	private static class_2960 method_9326(class_2960 arg) throws CommandSyntaxException {
		class_2378.field_11145.method_17966(arg).filter(class_1299::method_5896).orElseThrow(() -> field_9866.create(arg));
		return arg;
	}

	public class_2960 method_9325(StringReader stringReader) throws CommandSyntaxException {
		return method_9326(class_2960.method_12835(stringReader));
	}

	@Override
	public Collection<String> getExamples() {
		return field_9865;
	}
}
