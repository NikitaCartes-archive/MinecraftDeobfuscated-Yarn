package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map.Entry;

public class class_3065 {
	public static void method_13392(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("gamerule").requires(arg -> arg.method_9259(2));

		for (Entry<String, class_1928.class_1930> entry : class_1928.method_8354().entrySet()) {
			literalArgumentBuilder.then(
				class_2170.method_9247((String)entry.getKey())
					.executes(commandContext -> method_13397(commandContext.getSource(), (String)entry.getKey()))
					.then(
						((class_1928.class_1930)entry.getValue())
							.method_8367()
							.method_8371("value")
							.executes(commandContext -> method_13394(commandContext.getSource(), (String)entry.getKey(), commandContext))
					)
			);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_13394(class_2168 arg, String string, CommandContext<class_2168> commandContext) {
		class_1928.class_1929 lv = arg.method_9211().method_3767().method_8360(string);
		lv.method_8364().method_8370(commandContext, "value", lv);
		arg.method_9226(new class_2588("commands.gamerule.set", string, lv.method_8362()), true);
		return lv.method_8363();
	}

	private static int method_13397(class_2168 arg, String string) {
		class_1928.class_1929 lv = arg.method_9211().method_3767().method_8360(string);
		arg.method_9226(new class_2588("commands.gamerule.query", string, lv.method_8362()), false);
		return lv.method_8363();
	}
}
