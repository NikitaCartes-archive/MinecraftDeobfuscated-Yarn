package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;

public class class_3068 {
	public static void method_13402(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("give")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9308())
						.then(
							class_2170.method_9244("item", class_2287.method_9776())
								.executes(
									commandContext -> method_13401(
											commandContext.getSource(), class_2287.method_9777(commandContext, "item"), class_2186.method_9312(commandContext, "targets"), 1
										)
								)
								.then(
									class_2170.method_9244("count", IntegerArgumentType.integer(1))
										.executes(
											commandContext -> method_13401(
													commandContext.getSource(),
													class_2287.method_9777(commandContext, "item"),
													class_2186.method_9312(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "count")
												)
										)
								)
						)
				)
		);
	}

	private static int method_13401(class_2168 arg, class_2290 arg2, Collection<class_3222> collection, int i) throws CommandSyntaxException {
		for (class_3222 lv : collection) {
			int j = i;

			while (j > 0) {
				int k = Math.min(arg2.method_9785().method_7882(), j);
				j -= k;
				class_1799 lv2 = arg2.method_9781(k, false);
				boolean bl = lv.field_7514.method_7394(lv2);
				if (bl && lv2.method_7960()) {
					lv2.method_7939(1);
					class_1542 lv3 = lv.method_7328(lv2, false);
					if (lv3 != null) {
						lv3.method_6987();
					}

					lv.field_6002
						.method_8465(
							null,
							lv.field_5987,
							lv.field_6010,
							lv.field_6035,
							class_3417.field_15197,
							class_3419.field_15248,
							0.2F,
							((lv.method_6051().nextFloat() - lv.method_6051().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					lv.field_7498.method_7623();
				} else {
					class_1542 lv3 = lv.method_7328(lv2, false);
					if (lv3 != null) {
						lv3.method_6975();
						lv3.method_6984(lv.method_5667());
					}
				}
			}
		}

		if (collection.size() == 1) {
			arg.method_9226(
				new class_2588("commands.give.success.single", i, arg2.method_9781(i, false).method_7954(), ((class_3222)collection.iterator().next()).method_5476()), true
			);
		} else {
			arg.method_9226(new class_2588("commands.give.success.single", i, arg2.method_9781(i, false).method_7954(), collection.size()), true);
		}

		return collection.size();
	}
}
