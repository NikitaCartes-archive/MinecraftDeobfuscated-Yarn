package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;

public class class_3136 {
	public static void method_13681(CommandDispatcher<class_2168> commandDispatcher) {
		RequiredArgumentBuilder<class_2168, class_2300> requiredArgumentBuilder = class_2170.method_9244("targets", class_2186.method_9308())
			.executes(commandContext -> method_13685(commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), null, null))
			.then(
				class_2170.method_9247("*")
					.then(
						class_2170.method_9244("sound", class_2232.method_9441())
							.suggests(class_2321.field_10934)
							.executes(
								commandContext -> method_13685(
										commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), null, class_2232.method_9443(commandContext, "sound")
									)
							)
					)
			);

		for (class_3419 lv : class_3419.values()) {
			requiredArgumentBuilder.then(
				class_2170.method_9247(lv.method_14840())
					.executes(commandContext -> method_13685(commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), lv, null))
					.then(
						class_2170.method_9244("sound", class_2232.method_9441())
							.suggests(class_2321.field_10934)
							.executes(
								commandContext -> method_13685(
										commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), lv, class_2232.method_9443(commandContext, "sound")
									)
							)
					)
			);
		}

		commandDispatcher.register(class_2170.method_9247("stopsound").requires(arg -> arg.method_9259(2)).then(requiredArgumentBuilder));
	}

	private static int method_13685(class_2168 arg, Collection<class_3222> collection, @Nullable class_3419 arg2, @Nullable class_2960 arg3) {
		class_2770 lv = new class_2770(arg3, arg2);

		for (class_3222 lv2 : collection) {
			lv2.field_13987.method_14364(lv);
		}

		if (arg2 != null) {
			if (arg3 != null) {
				arg.method_9226(new class_2588("commands.stopsound.success.source.sound", arg3, arg2.method_14840()), true);
			} else {
				arg.method_9226(new class_2588("commands.stopsound.success.source.any", arg2.method_14840()), true);
			}
		} else if (arg3 != null) {
			arg.method_9226(new class_2588("commands.stopsound.success.sourceless.sound", arg3), true);
		} else {
			arg.method_9226(new class_2588("commands.stopsound.success.sourceless.any"), true);
		}

		return collection.size();
	}
}
