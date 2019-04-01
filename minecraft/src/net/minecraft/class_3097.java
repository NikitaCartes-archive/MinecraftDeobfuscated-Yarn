package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;

public class class_3097 {
	public static void method_13529(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(class_2170.method_9247("reload").requires(arg -> arg.method_9259(3)).executes(commandContext -> {
			commandContext.getSource().method_9226(new class_2588("commands.reload.success"), true);
			commandContext.getSource().method_9211().method_3848();
			return 0;
		}));
	}
}
