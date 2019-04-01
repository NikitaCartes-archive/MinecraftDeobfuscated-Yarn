package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;

public class class_3073 {
	public static void method_13410(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("kick")
				.requires(arg -> arg.method_9259(3))
				.then(
					class_2170.method_9244("targets", class_2186.method_9308())
						.executes(
							commandContext -> method_13411(
									commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), new class_2588("multiplayer.disconnect.kicked")
								)
						)
						.then(
							class_2170.method_9244("reason", class_2196.method_9340())
								.executes(
									commandContext -> method_13411(
											commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), class_2196.method_9339(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_13411(class_2168 arg, Collection<class_3222> collection, class_2561 arg2) {
		for (class_3222 lv : collection) {
			lv.field_13987.method_14367(arg2);
			arg.method_9226(new class_2588("commands.kick.success", lv.method_5476(), arg2), true);
		}

		return collection.size();
	}
}
