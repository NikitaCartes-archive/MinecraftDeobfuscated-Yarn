package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;

public class class_3102 {
	public static final SimpleCommandExceptionType field_13696 = new SimpleCommandExceptionType(new class_2588("commands.replaceitem.block.failed"));
	public static final DynamicCommandExceptionType field_13695 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.replaceitem.slot.inapplicable", object)
	);
	public static final Dynamic2CommandExceptionType field_13697 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.replaceitem.entity.failed", object, object2)
	);

	public static void method_13541(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("replaceitem")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("block")
						.then(
							class_2170.method_9244("pos", class_2262.method_9698())
								.then(
									class_2170.method_9244("slot", class_2240.method_9473())
										.then(
											class_2170.method_9244("item", class_2287.method_9776())
												.executes(
													commandContext -> method_13539(
															commandContext.getSource(),
															class_2262.method_9696(commandContext, "pos"),
															class_2240.method_9469(commandContext, "slot"),
															class_2287.method_9777(commandContext, "item").method_9781(1, false)
														)
												)
												.then(
													class_2170.method_9244("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> method_13539(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "pos"),
																	class_2240.method_9469(commandContext, "slot"),
																	class_2287.method_9777(commandContext, "item").method_9781(IntegerArgumentType.getInteger(commandContext, "count"), true)
																)
														)
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("entity")
						.then(
							class_2170.method_9244("targets", class_2186.method_9306())
								.then(
									class_2170.method_9244("slot", class_2240.method_9473())
										.then(
											class_2170.method_9244("item", class_2287.method_9776())
												.executes(
													commandContext -> method_13537(
															commandContext.getSource(),
															class_2186.method_9317(commandContext, "targets"),
															class_2240.method_9469(commandContext, "slot"),
															class_2287.method_9777(commandContext, "item").method_9781(1, false)
														)
												)
												.then(
													class_2170.method_9244("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> method_13537(
																	commandContext.getSource(),
																	class_2186.method_9317(commandContext, "targets"),
																	class_2240.method_9469(commandContext, "slot"),
																	class_2287.method_9777(commandContext, "item").method_9781(IntegerArgumentType.getInteger(commandContext, "count"), true)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13539(class_2168 arg, class_2338 arg2, int i, class_1799 arg3) throws CommandSyntaxException {
		class_2586 lv = arg.method_9225().method_8321(arg2);
		if (!(lv instanceof class_1263)) {
			throw field_13696.create();
		} else {
			class_1263 lv2 = (class_1263)lv;
			if (i >= 0 && i < lv2.method_5439()) {
				lv2.method_5447(i, arg3);
				arg.method_9226(
					new class_2588("commands.replaceitem.block.success", arg2.method_10263(), arg2.method_10264(), arg2.method_10260(), arg3.method_7954()), true
				);
				return 1;
			} else {
				throw field_13695.create(i);
			}
		}
	}

	private static int method_13537(class_2168 arg, Collection<? extends class_1297> collection, int i, class_1799 arg2) throws CommandSyntaxException {
		List<class_1297> list = Lists.<class_1297>newArrayListWithCapacity(collection.size());

		for (class_1297 lv : collection) {
			if (lv instanceof class_3222) {
				((class_3222)lv).field_7498.method_7623();
			}

			if (lv.method_5758(i, arg2.method_7972())) {
				list.add(lv);
				if (lv instanceof class_3222) {
					((class_3222)lv).field_7498.method_7623();
				}
			}
		}

		if (list.isEmpty()) {
			throw field_13697.create(arg2.method_7954(), i);
		} else {
			if (list.size() == 1) {
				arg.method_9226(new class_2588("commands.replaceitem.entity.success.single", ((class_1297)list.iterator().next()).method_5476(), arg2.method_7954()), true);
			} else {
				arg.method_9226(new class_2588("commands.replaceitem.entity.success.multiple", list.size(), arg2.method_7954()), true);
			}

			return list.size();
		}
	}
}
