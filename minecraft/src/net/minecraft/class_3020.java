package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class class_3020 {
	private static final DynamicCommandExceptionType field_13487 = new DynamicCommandExceptionType(object -> new class_2588("clear.failed.single", object));
	private static final DynamicCommandExceptionType field_13488 = new DynamicCommandExceptionType(object -> new class_2588("clear.failed.multiple", object));

	public static void method_13076(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("clear")
				.requires(arg -> arg.method_9259(2))
				.executes(commandContext -> method_13077(commandContext.getSource(), Collections.singleton(commandContext.getSource().method_9207()), arg -> true, -1))
				.then(
					class_2170.method_9244("targets", class_2186.method_9308())
						.executes(commandContext -> method_13077(commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), arg -> true, -1))
						.then(
							class_2170.method_9244("item", class_2293.method_9801())
								.executes(
									commandContext -> method_13077(
											commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), class_2293.method_9804(commandContext, "item"), -1
										)
								)
								.then(
									class_2170.method_9244("maxCount", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13077(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													class_2293.method_9804(commandContext, "item"),
													IntegerArgumentType.getInteger(commandContext, "maxCount")
												)
										)
								)
						)
				)
		);
	}

	private static int method_13077(class_2168 arg, Collection<class_3222> collection, Predicate<class_1799> predicate, int i) throws CommandSyntaxException {
		int j = 0;

		for (class_3222 lv : collection) {
			j += lv.field_7514.method_7369(predicate, i);
		}

		if (j == 0) {
			if (collection.size() == 1) {
				throw field_13487.create(((class_3222)collection.iterator().next()).method_5477().method_10863());
			} else {
				throw field_13488.create(collection.size());
			}
		} else {
			if (i == 0) {
				if (collection.size() == 1) {
					arg.method_9226(new class_2588("commands.clear.test.single", j, ((class_3222)collection.iterator().next()).method_5476()), true);
				} else {
					arg.method_9226(new class_2588("commands.clear.test.multiple", j, collection.size()), true);
				}
			} else if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.clear.success.single", j, ((class_3222)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.clear.success.multiple", j, collection.size()), true);
			}

			return j;
		}
	}
}
