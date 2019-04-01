package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class class_3149 {
	public static void method_13786(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("time")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("set")
						.then(class_2170.method_9247("day").executes(commandContext -> method_13784(commandContext.getSource(), 1000)))
						.then(class_2170.method_9247("noon").executes(commandContext -> method_13784(commandContext.getSource(), 6000)))
						.then(class_2170.method_9247("night").executes(commandContext -> method_13784(commandContext.getSource(), 13000)))
						.then(class_2170.method_9247("midnight").executes(commandContext -> method_13784(commandContext.getSource(), 18000)))
						.then(
							class_2170.method_9244("time", class_2245.method_9489())
								.executes(commandContext -> method_13784(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
						)
				)
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("time", class_2245.method_9489())
								.executes(commandContext -> method_13788(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
						)
				)
				.then(
					class_2170.method_9247("query")
						.then(
							class_2170.method_9247("daytime")
								.executes(commandContext -> method_13796(commandContext.getSource(), method_13787(commandContext.getSource().method_9225())))
						)
						.then(
							class_2170.method_9247("gametime")
								.executes(commandContext -> method_13796(commandContext.getSource(), (int)(commandContext.getSource().method_9225().method_8510() % 2147483647L)))
						)
						.then(
							class_2170.method_9247("day")
								.executes(
									commandContext -> method_13796(commandContext.getSource(), (int)(commandContext.getSource().method_9225().method_8532() / 24000L % 2147483647L))
								)
						)
				)
		);
	}

	private static int method_13787(class_3218 arg) {
		return (int)(arg.method_8532() % 24000L);
	}

	private static int method_13796(class_2168 arg, int i) {
		arg.method_9226(new class_2588("commands.time.query", i), false);
		return i;
	}

	public static int method_13784(class_2168 arg, int i) {
		for (class_3218 lv : arg.method_9211().method_3738()) {
			lv.method_8435((long)i);
		}

		arg.method_9226(new class_2588("commands.time.set", i), true);
		return method_13787(arg.method_9225());
	}

	public static int method_13788(class_2168 arg, int i) {
		for (class_3218 lv : arg.method_9211().method_3738()) {
			lv.method_8435(lv.method_8532() + (long)i);
		}

		int j = method_13787(arg.method_9225());
		arg.method_9226(new class_2588("commands.time.set", j), true);
		return j;
	}
}
