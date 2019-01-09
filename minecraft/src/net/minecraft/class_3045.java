package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class class_3045 {
	public static void method_13237(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("me")
				.then(
					class_2170.method_9244("action", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								commandContext.getSource()
									.method_9211()
									.method_3760()
									.method_14593(new class_2588("chat.type.emote", commandContext.getSource().method_9223(), StringArgumentType.getString(commandContext, "action")));
								return 1;
							}
						)
				)
		);
	}
}
