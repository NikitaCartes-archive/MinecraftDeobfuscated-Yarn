package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;

public class class_3014 {
	public static void method_13014(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("banlist")
				.requires(
					arg -> (arg.method_9211().method_3760().method_14563().method_14639() || arg.method_9211().method_3760().method_14585().method_14639())
							&& arg.method_9259(3)
				)
				.executes(commandContext -> {
					class_3324 lv = commandContext.getSource().method_9211().method_3760();
					return method_13015(commandContext.getSource(), Lists.newArrayList(Iterables.concat(lv.method_14563().method_14632(), lv.method_14585().method_14632())));
				})
				.then(
					class_2170.method_9247("ips")
						.executes(
							commandContext -> method_13015(commandContext.getSource(), commandContext.getSource().method_9211().method_3760().method_14585().method_14632())
						)
				)
				.then(
					class_2170.method_9247("players")
						.executes(
							commandContext -> method_13015(commandContext.getSource(), commandContext.getSource().method_9211().method_3760().method_14563().method_14632())
						)
				)
		);
	}

	private static int method_13015(class_2168 arg, Collection<? extends class_3309<?>> collection) {
		if (collection.isEmpty()) {
			arg.method_9226(new class_2588("commands.banlist.none"), false);
		} else {
			arg.method_9226(new class_2588("commands.banlist.list", collection.size()), false);

			for (class_3309<?> lv : collection) {
				arg.method_9226(new class_2588("commands.banlist.entry", lv.method_14504(), lv.method_14501(), lv.method_14503()), false);
			}
		}

		return collection.size();
	}
}
