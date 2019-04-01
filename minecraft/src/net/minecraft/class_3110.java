package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;

public class class_3110 {
	public static void method_13562(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("say")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("message", class_2196.method_9340())
						.executes(
							commandContext -> {
								class_2561 lv = class_2196.method_9339(commandContext, "message");
								commandContext.getSource()
									.method_9211()
									.method_3760()
									.method_14593(new class_2588("chat.type.announcement", commandContext.getSource().method_9223(), lv));
								return 1;
							}
						)
				)
		);
	}
}
