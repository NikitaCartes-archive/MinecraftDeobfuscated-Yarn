package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

public class class_2273 implements ArgumentType<EnumSet<class_2350.class_2351>> {
	private static final Collection<String> field_10740 = Arrays.asList("xyz", "x");
	private static final SimpleCommandExceptionType field_10741 = new SimpleCommandExceptionType(new class_2588("arguments.swizzle.invalid"));

	public static class_2273 method_9721() {
		return new class_2273();
	}

	public static EnumSet<class_2350.class_2351> method_9720(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, EnumSet.class);
	}

	public EnumSet<class_2350.class_2351> method_9722(StringReader stringReader) throws CommandSyntaxException {
		EnumSet<class_2350.class_2351> enumSet = EnumSet.noneOf(class_2350.class_2351.class);

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			char c = stringReader.read();
			class_2350.class_2351 lv;
			switch (c) {
				case 'x':
					lv = class_2350.class_2351.field_11048;
					break;
				case 'y':
					lv = class_2350.class_2351.field_11052;
					break;
				case 'z':
					lv = class_2350.class_2351.field_11051;
					break;
				default:
					throw field_10741.create();
			}

			if (enumSet.contains(lv)) {
				throw field_10741.create();
			}

			enumSet.add(lv);
		}

		return enumSet;
	}

	@Override
	public Collection<String> getExamples() {
		return field_10740;
	}
}
