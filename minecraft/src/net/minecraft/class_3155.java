package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class class_3155 {
	public static void method_13827(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("weather")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("clear")
						.executes(commandContext -> method_13824(commandContext.getSource(), 6000))
						.then(
							class_2170.method_9244("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_13824(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
				.then(
					class_2170.method_9247("rain")
						.executes(commandContext -> method_13828(commandContext.getSource(), 6000))
						.then(
							class_2170.method_9244("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_13828(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
				.then(
					class_2170.method_9247("thunder")
						.executes(commandContext -> method_13833(commandContext.getSource(), 6000))
						.then(
							class_2170.method_9244("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_13833(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
		);
	}

	private static int method_13824(class_2168 arg, int i) {
		arg.method_9225().method_8401().method_167(i);
		arg.method_9225().method_8401().method_164(0);
		arg.method_9225().method_8401().method_173(0);
		arg.method_9225().method_8401().method_157(false);
		arg.method_9225().method_8401().method_147(false);
		arg.method_9226(new class_2588("commands.weather.set.clear"), true);
		return i;
	}

	private static int method_13828(class_2168 arg, int i) {
		arg.method_9225().method_8401().method_167(0);
		arg.method_9225().method_8401().method_164(i);
		arg.method_9225().method_8401().method_173(i);
		arg.method_9225().method_8401().method_157(true);
		arg.method_9225().method_8401().method_147(false);
		arg.method_9226(new class_2588("commands.weather.set.rain"), true);
		return i;
	}

	private static int method_13833(class_2168 arg, int i) {
		arg.method_9225().method_8401().method_167(0);
		arg.method_9225().method_8401().method_164(i);
		arg.method_9225().method_8401().method_173(i);
		arg.method_9225().method_8401().method_157(true);
		arg.method_9225().method_8401().method_147(true);
		arg.method_9226(new class_2588("commands.weather.set.thunder"), true);
		return i;
	}
}
