package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;

public class class_3075 {
	public static void method_13429(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("kill")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9306())
						.executes(commandContext -> method_13430(commandContext.getSource(), class_2186.method_9317(commandContext, "targets")))
				)
		);
	}

	private static int method_13430(class_2168 arg, Collection<? extends class_1297> collection) {
		for (class_1297 lv : collection) {
			lv.method_5768();
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.kill.success.single", ((class_1297)collection.iterator().next()).method_5476()), true);
		} else {
			arg.method_9226(new class_2588("commands.kill.success.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
