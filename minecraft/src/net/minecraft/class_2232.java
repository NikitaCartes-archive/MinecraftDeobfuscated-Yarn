package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;

public class class_2232 implements ArgumentType<class_2960> {
	private static final Collection<String> field_9946 = Arrays.asList("foo", "foo:bar", "012");
	public static final DynamicCommandExceptionType field_9944 = new DynamicCommandExceptionType(object -> new class_2588("argument.id.unknown", object));
	public static final DynamicCommandExceptionType field_9945 = new DynamicCommandExceptionType(
		object -> new class_2588("advancement.advancementNotFound", object)
	);
	public static final DynamicCommandExceptionType field_9947 = new DynamicCommandExceptionType(object -> new class_2588("recipe.notFound", object));

	public static class_2232 method_9441() {
		return new class_2232();
	}

	public static class_161 method_9439(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		class_2960 lv = commandContext.getArgument(string, class_2960.class);
		class_161 lv2 = commandContext.getSource().method_9211().method_3851().method_12896(lv);
		if (lv2 == null) {
			throw field_9945.create(lv);
		} else {
			return lv2;
		}
	}

	public static class_1860<?> method_9442(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		class_1863 lv = commandContext.getSource().method_9211().method_3772();
		class_2960 lv2 = commandContext.getArgument(string, class_2960.class);
		return (class_1860<?>)lv.method_8130(lv2).orElseThrow(() -> field_9947.create(lv2));
	}

	public static class_2960 method_9443(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2960.class);
	}

	public class_2960 method_9446(StringReader stringReader) throws CommandSyntaxException {
		return class_2960.method_12835(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9946;
	}
}
