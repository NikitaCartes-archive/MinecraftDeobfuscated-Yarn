package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;

public class class_3112 {
	private static final SimpleCommandExceptionType field_13706 = new SimpleCommandExceptionType(new class_2588("commands.schedule.same_tick"));

	public static void method_13567(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("schedule")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("function")
						.then(
							class_2170.method_9244("function", class_2284.method_9760())
								.suggests(class_3062.field_13662)
								.then(
									class_2170.method_9244("time", class_2245.method_9489())
										.executes(
											commandContext -> method_13566(
													commandContext.getSource(), class_2284.method_9768(commandContext, "function"), IntegerArgumentType.getInteger(commandContext, "time")
												)
										)
								)
						)
				)
		);
	}

	private static int method_13566(class_2168 arg, Either<class_2158, class_3494<class_2158>> either, int i) throws CommandSyntaxException {
		if (i == 0) {
			throw field_13706.create();
		} else {
			long l = arg.method_9225().method_8510() + (long)i;
			either.ifLeft(arg2 -> {
				class_2960 lv = arg2.method_9194();
				arg.method_9225().method_8401().method_143().method_984(lv.toString(), l, new class_231(lv));
				arg.method_9226(new class_2588("commands.schedule.created.function", lv, i, l), true);
			}).ifRight(arg2 -> {
				class_2960 lv = arg2.method_15143();
				arg.method_9225().method_8401().method_143().method_984("#" + lv.toString(), l, new class_229(lv));
				arg.method_9226(new class_2588("commands.schedule.created.tag", lv, i, l), true);
			});
			return (int)Math.floorMod(l, 2147483647L);
		}
	}
}
