package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_3153 {
	private static final SimpleCommandExceptionType field_13763 = new SimpleCommandExceptionType(new class_2588("commands.trigger.failed.unprimed"));
	private static final SimpleCommandExceptionType field_13764 = new SimpleCommandExceptionType(new class_2588("commands.trigger.failed.invalid"));

	public static void method_13813(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("trigger")
				.then(
					class_2170.method_9244("objective", class_2214.method_9391())
						.suggests((commandContext, suggestionsBuilder) -> method_13819(commandContext.getSource(), suggestionsBuilder))
						.executes(
							commandContext -> method_13818(
									commandContext.getSource(), method_13821(commandContext.getSource().method_9207(), class_2214.method_9395(commandContext, "objective"))
								)
						)
						.then(
							class_2170.method_9247("add")
								.then(
									class_2170.method_9244("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_13817(
													commandContext.getSource(),
													method_13821(commandContext.getSource().method_9207(), class_2214.method_9395(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
						.then(
							class_2170.method_9247("set")
								.then(
									class_2170.method_9244("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_13820(
													commandContext.getSource(),
													method_13821(commandContext.getSource().method_9207(), class_2214.method_9395(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
				)
		);
	}

	public static CompletableFuture<Suggestions> method_13819(class_2168 arg, SuggestionsBuilder suggestionsBuilder) {
		class_1297 lv = arg.method_9228();
		List<String> list = Lists.<String>newArrayList();
		if (lv != null) {
			class_269 lv2 = arg.method_9211().method_3845();
			String string = lv.method_5820();

			for (class_266 lv3 : lv2.method_1151()) {
				if (lv3.method_1116() == class_274.field_1462 && lv2.method_1183(string, lv3)) {
					class_267 lv4 = lv2.method_1180(string, lv3);
					if (!lv4.method_1131()) {
						list.add(lv3.method_1113());
					}
				}
			}
		}

		return class_2172.method_9265(list, suggestionsBuilder);
	}

	private static int method_13817(class_2168 arg, class_267 arg2, int i) {
		arg2.method_1124(i);
		arg.method_9226(new class_2588("commands.trigger.add.success", arg2.method_1127().method_1120(), i), true);
		return arg2.method_1126();
	}

	private static int method_13820(class_2168 arg, class_267 arg2, int i) {
		arg2.method_1128(i);
		arg.method_9226(new class_2588("commands.trigger.set.success", arg2.method_1127().method_1120(), i), true);
		return i;
	}

	private static int method_13818(class_2168 arg, class_267 arg2) {
		arg2.method_1124(1);
		arg.method_9226(new class_2588("commands.trigger.simple.success", arg2.method_1127().method_1120()), true);
		return arg2.method_1126();
	}

	private static class_267 method_13821(class_3222 arg, class_266 arg2) throws CommandSyntaxException {
		if (arg2.method_1116() != class_274.field_1462) {
			throw field_13764.create();
		} else {
			class_269 lv = arg.method_7327();
			String string = arg.method_5820();
			if (!lv.method_1183(string, arg2)) {
				throw field_13763.create();
			} else {
				class_267 lv2 = lv.method_1180(string, arg2);
				if (lv2.method_1131()) {
					throw field_13763.create();
				} else {
					lv2.method_1125(true);
					return lv2;
				}
			}
		}
	}
}
