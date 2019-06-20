package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;

public class class_3146 {
	public static void method_13776(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("tellraw")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9308()).then(class_2170.method_9244("message", class_2178.method_9281()).executes(commandContext -> {
						int i = 0;

						for (class_3222 lv : class_2186.method_9312(commandContext, "targets")) {
							lv.method_9203(class_2564.method_10881(commandContext.getSource(), class_2178.method_9280(commandContext, "message"), lv, 0));
							i++;
						}

						return i;
					}))
				)
		);
	}
}
