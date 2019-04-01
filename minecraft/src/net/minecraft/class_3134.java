package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;

public class class_3134 {
	public static void method_13675(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(class_2170.method_9247("stop").requires(arg -> arg.method_9259(4)).executes(commandContext -> {
			commandContext.getSource().method_9226(new class_2588("commands.stop.stopping"), true);
			commandContext.getSource().method_9211().method_3747(false);
			return 1;
		}));
	}
}
