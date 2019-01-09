package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3048 {
	private static final DynamicCommandExceptionType field_13629 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.enchant.failed.entity", object)
	);
	private static final DynamicCommandExceptionType field_13631 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.enchant.failed.itemless", object)
	);
	private static final DynamicCommandExceptionType field_13633 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.enchant.failed.incompatible", object)
	);
	private static final Dynamic2CommandExceptionType field_13632 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.enchant.failed.level", object, object2)
	);
	private static final SimpleCommandExceptionType field_13630 = new SimpleCommandExceptionType(new class_2588("commands.enchant.failed"));

	public static void method_13243(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("enchant")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9306())
						.then(
							class_2170.method_9244("enchantment", class_2194.method_9336())
								.executes(
									commandContext -> method_13241(
											commandContext.getSource(), class_2186.method_9317(commandContext, "targets"), class_2194.method_9334(commandContext, "enchantment"), 1
										)
								)
								.then(
									class_2170.method_9244("level", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13241(
													commandContext.getSource(),
													class_2186.method_9317(commandContext, "targets"),
													class_2194.method_9334(commandContext, "enchantment"),
													IntegerArgumentType.getInteger(commandContext, "level")
												)
										)
								)
						)
				)
		);
	}

	private static int method_13241(class_2168 arg, Collection<? extends class_1297> collection, class_1887 arg2, int i) throws CommandSyntaxException {
		if (i > arg2.method_8183()) {
			throw field_13632.create(i, arg2.method_8183());
		} else {
			int j = 0;

			for (class_1297 lv : collection) {
				if (lv instanceof class_1309) {
					class_1309 lv2 = (class_1309)lv;
					class_1799 lv3 = lv2.method_6047();
					if (!lv3.method_7960()) {
						if (arg2.method_8192(lv3) && class_1890.method_8201(class_1890.method_8222(lv3).keySet(), arg2)) {
							lv3.method_7978(arg2, i);
							j++;
						} else if (collection.size() == 1) {
							throw field_13633.create(lv3.method_7909().method_7864(lv3).getString());
						}
					} else if (collection.size() == 1) {
						throw field_13631.create(lv2.method_5477().getString());
					}
				} else if (collection.size() == 1) {
					throw field_13629.create(lv.method_5477().getString());
				}
			}

			if (j == 0) {
				throw field_13630.create();
			} else {
				if (collection.size() == 1) {
					arg.method_9226(new class_2588("commands.enchant.success.single", arg2.method_8179(i), ((class_1297)collection.iterator().next()).method_5476()), true);
				} else {
					arg.method_9226(new class_2588("commands.enchant.success.multiple", arg2.method_8179(i), collection.size()), true);
				}

				return j;
			}
		}
	}
}
