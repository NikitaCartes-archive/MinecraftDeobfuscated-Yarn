package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;

public class class_3118 {
	public static void method_13616(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("seed")
				.requires(arg -> arg.method_9211().method_3724() || arg.method_9259(2))
				.executes(
					commandContext -> {
						long l = commandContext.getSource().method_9225().method_8412();
						class_2561 lv = class_2564.method_10885(
							new class_2585(String.valueOf(l))
								.method_10859(
									arg -> arg.method_10977(class_124.field_1060)
											.method_10958(new class_2558(class_2558.class_2559.field_11745, String.valueOf(l)))
											.method_10975(String.valueOf(l))
								)
						);
						commandContext.getSource().method_9226(new class_2588("commands.seed.success", lv), false);
						return (int)l;
					}
				)
		);
	}
}
