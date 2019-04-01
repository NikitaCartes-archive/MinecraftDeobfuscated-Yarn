package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;

public class class_3128 {
	public static void method_13647(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("setworldspawn")
				.requires(arg -> arg.method_9259(2))
				.executes(commandContext -> method_13650(commandContext.getSource(), new class_2338(commandContext.getSource().method_9222())))
				.then(
					class_2170.method_9244("pos", class_2262.method_9698())
						.executes(commandContext -> method_13650(commandContext.getSource(), class_2262.method_9697(commandContext, "pos")))
				)
		);
	}

	private static int method_13650(class_2168 arg, class_2338 arg2) {
		arg.method_9225().method_8554(arg2);
		arg.method_9211().method_3760().method_14581(new class_2759(arg2));
		arg.method_9226(new class_2588("commands.setworldspawn.success", arg2.method_10263(), arg2.method_10264(), arg2.method_10260()), true);
		return 1;
	}
}
