package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;

public class class_2270 implements ArgumentType<class_2267> {
	private static final Collection<String> field_10735 = Arrays.asList("0 0", "~ ~", "~-5 ~5");
	public static final SimpleCommandExceptionType field_10736 = new SimpleCommandExceptionType(new class_2588("argument.rotation.incomplete"));

	public static class_2270 method_9717() {
		return new class_2270();
	}

	public static class_2267 method_9716(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2267.class);
	}

	public class_2267 method_9718(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_10736.createWithContext(stringReader);
		} else {
			class_2278 lv = class_2278.method_9743(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv2 = class_2278.method_9743(stringReader, false);
				return new class_2280(lv2, lv, new class_2278(true, 0.0));
			} else {
				stringReader.setCursor(i);
				throw field_10736.createWithContext(stringReader);
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_10735;
	}
}
