package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class class_3123 {
	public static void method_13631(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("setidletimeout")
				.requires(arg -> arg.method_9259(3))
				.then(
					class_2170.method_9244("minutes", IntegerArgumentType.integer(0))
						.executes(commandContext -> method_13630(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "minutes")))
				)
		);
	}

	private static int method_13630(class_2168 arg, int i) {
		arg.method_9211().method_3803(i);
		arg.method_9226(new class_2588("commands.setidletimeout.success", i), true);
		return i;
	}
}
