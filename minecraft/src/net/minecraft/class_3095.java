package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;

public class class_3095 {
	private static final SimpleCommandExceptionType field_13681 = new SimpleCommandExceptionType(new class_2588("commands.recipe.give.failed"));
	private static final SimpleCommandExceptionType field_13682 = new SimpleCommandExceptionType(new class_2588("commands.recipe.take.failed"));

	public static void method_13517(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("recipe")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("give")
						.then(
							class_2170.method_9244("targets", class_2186.method_9308())
								.then(
									class_2170.method_9244("recipe", class_2232.method_9441())
										.suggests(class_2321.field_10932)
										.executes(
											commandContext -> method_13520(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													Collections.singleton(class_2232.method_9442(commandContext, "recipe"))
												)
										)
								)
								.then(
									class_2170.method_9247("*")
										.executes(
											commandContext -> method_13520(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													commandContext.getSource().method_9211().method_3772().method_8126()
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("take")
						.then(
							class_2170.method_9244("targets", class_2186.method_9308())
								.then(
									class_2170.method_9244("recipe", class_2232.method_9441())
										.suggests(class_2321.field_10932)
										.executes(
											commandContext -> method_13518(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													Collections.singleton(class_2232.method_9442(commandContext, "recipe"))
												)
										)
								)
								.then(
									class_2170.method_9247("*")
										.executes(
											commandContext -> method_13518(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													commandContext.getSource().method_9211().method_3772().method_8126()
												)
										)
								)
						)
				)
		);
	}

	private static int method_13520(class_2168 arg, Collection<class_3222> collection, Collection<class_1860<?>> collection2) throws CommandSyntaxException {
		int i = 0;

		for (class_3222 lv : collection) {
			i += lv.method_7254(collection2);
		}

		if (i == 0) {
			throw field_13681.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.recipe.give.success.single", collection2.size(), ((class_3222)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13518(class_2168 arg, Collection<class_3222> collection, Collection<class_1860<?>> collection2) throws CommandSyntaxException {
		int i = 0;

		for (class_3222 lv : collection) {
			i += lv.method_7333(collection2);
		}

		if (i == 0) {
			throw field_13682.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.recipe.take.success.single", collection2.size(), ((class_3222)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}
}
