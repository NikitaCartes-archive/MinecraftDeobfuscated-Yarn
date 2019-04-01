package net.minecraft;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;

public class class_2178 implements ArgumentType<class_2561> {
	private static final Collection<String> field_9841 = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType field_9842 = new DynamicCommandExceptionType(object -> new class_2588("argument.component.invalid", object));

	private class_2178() {
	}

	public static class_2561 method_9280(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2561.class);
	}

	public static class_2178 method_9281() {
		return new class_2178();
	}

	public class_2561 method_9283(StringReader stringReader) throws CommandSyntaxException {
		try {
			class_2561 lv = class_2561.class_2562.method_10879(stringReader);
			if (lv == null) {
				throw field_9842.createWithContext(stringReader, "empty");
			} else {
				return lv;
			}
		} catch (JsonParseException var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw field_9842.createWithContext(stringReader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_9841;
	}
}
