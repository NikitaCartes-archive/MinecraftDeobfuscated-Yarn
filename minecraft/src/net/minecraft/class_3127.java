package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;

public class class_3127 {
	public static void method_13641(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("spawnpoint")
				.requires(arg -> arg.method_9259(2))
				.executes(
					commandContext -> method_13645(
							commandContext.getSource(), Collections.singleton(commandContext.getSource().method_9207()), new class_2338(commandContext.getSource().method_9222())
						)
				)
				.then(
					class_2170.method_9244("targets", class_2186.method_9308())
						.executes(
							commandContext -> method_13645(
									commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), new class_2338(commandContext.getSource().method_9222())
								)
						)
						.then(
							class_2170.method_9244("pos", class_2262.method_9698())
								.executes(
									commandContext -> method_13645(
											commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), class_2262.method_9697(commandContext, "pos")
										)
								)
						)
				)
		);
	}

	private static int method_13645(class_2168 arg, Collection<class_3222> collection, class_2338 arg2) {
		for (class_3222 lv : collection) {
			lv.method_7289(arg2, true);
		}

		if (collection.size() == 1) {
			arg.method_9226(
				new class_2588(
					"commands.spawnpoint.success.single",
					arg2.method_10263(),
					arg2.method_10264(),
					arg2.method_10260(),
					((class_3222)collection.iterator().next()).method_5476()
				),
				true
			);
		} else {
			arg.method_9226(
				new class_2588("commands.spawnpoint.success.multiple", arg2.method_10263(), arg2.method_10264(), arg2.method_10260(), collection.size()), true
			);
		}

		return collection.size();
	}
}
