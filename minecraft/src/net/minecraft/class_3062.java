package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;

public class class_3062 {
	public static final SuggestionProvider<class_2168> field_13662 = (commandContext, suggestionsBuilder) -> {
		class_2991 lv = commandContext.getSource().method_9211().method_3740();
		class_2172.method_9258(lv.method_12901().method_15189(), suggestionsBuilder, "#");
		return class_2172.method_9270(lv.method_12912().keySet(), suggestionsBuilder);
	};

	public static void method_13380(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("function")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("name", class_2284.method_9760())
						.suggests(field_13662)
						.executes(commandContext -> method_13381(commandContext.getSource(), class_2284.method_9769(commandContext, "name")))
				)
		);
	}

	private static int method_13381(class_2168 arg, Collection<class_2158> collection) {
		int i = 0;

		for (class_2158 lv : collection) {
			i += arg.method_9211().method_3740().method_12904(lv, arg.method_9217().method_9230(2));
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.function.success.single", i, ((class_2158)collection.iterator().next()).method_9194()), true);
		} else {
			arg.method_9226(new class_2588("commands.function.success.multiple", i, collection.size()), true);
		}

		return i;
	}
}
