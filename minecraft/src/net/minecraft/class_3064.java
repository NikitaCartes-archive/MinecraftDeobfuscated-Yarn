package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;

public class class_3064 {
	public static void method_13388(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("gamemode").requires(arg -> arg.method_9259(2));

		for (class_1934 lv : class_1934.values()) {
			if (lv != class_1934.field_9218) {
				literalArgumentBuilder.then(
					class_2170.method_9247(lv.method_8381())
						.executes(commandContext -> method_13387(commandContext, Collections.singleton(commandContext.getSource().method_9207()), lv))
						.then(
							class_2170.method_9244("target", class_2186.method_9308())
								.executes(commandContext -> method_13387(commandContext, class_2186.method_9312(commandContext, "target"), lv))
						)
				);
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static void method_13390(class_2168 arg, class_3222 arg2, class_1934 arg3) {
		class_2561 lv = new class_2588("gameMode." + arg3.method_8381());
		if (arg.method_9228() == arg2) {
			arg.method_9226(new class_2588("commands.gamemode.success.self", lv), true);
		} else {
			if (arg.method_9225().method_8450().method_8355(class_1928.field_19400)) {
				arg2.method_9203(new class_2588("gameMode.changed", lv));
			}

			arg.method_9226(new class_2588("commands.gamemode.success.other", arg2.method_5476(), lv), true);
		}
	}

	private static int method_13387(CommandContext<class_2168> commandContext, Collection<class_3222> collection, class_1934 arg) {
		int i = 0;

		for (class_3222 lv : collection) {
			if (lv.field_13974.method_14257() != arg) {
				lv.method_7336(arg);
				method_13390(commandContext.getSource(), lv, arg);
				i++;
			}
		}

		return i;
	}
}
