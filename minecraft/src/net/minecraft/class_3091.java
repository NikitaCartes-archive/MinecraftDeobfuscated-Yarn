package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3091 {
	private static final SimpleCommandExceptionType field_13678 = new SimpleCommandExceptionType(new class_2588("commands.playsound.failed"));

	public static void method_13500(CommandDispatcher<class_2168> commandDispatcher) {
		RequiredArgumentBuilder<class_2168, class_2960> requiredArgumentBuilder = class_2170.method_9244("sound", class_2232.method_9441())
			.suggests(class_2321.field_10934);

		for (class_3419 lv : class_3419.values()) {
			requiredArgumentBuilder.then(method_13497(lv));
		}

		commandDispatcher.register(class_2170.method_9247("playsound").requires(arg -> arg.method_9259(2)).then(requiredArgumentBuilder));
	}

	private static LiteralArgumentBuilder<class_2168> method_13497(class_3419 arg) {
		return class_2170.method_9247(arg.method_14840())
			.then(
				class_2170.method_9244("targets", class_2186.method_9308())
					.executes(
						commandContext -> method_13504(
								commandContext.getSource(),
								class_2186.method_9312(commandContext, "targets"),
								class_2232.method_9443(commandContext, "sound"),
								arg,
								commandContext.getSource().method_9222(),
								1.0F,
								1.0F,
								0.0F
							)
					)
					.then(
						class_2170.method_9244("pos", class_2277.method_9737())
							.executes(
								commandContext -> method_13504(
										commandContext.getSource(),
										class_2186.method_9312(commandContext, "targets"),
										class_2232.method_9443(commandContext, "sound"),
										arg,
										class_2277.method_9736(commandContext, "pos"),
										1.0F,
										1.0F,
										0.0F
									)
							)
							.then(
								class_2170.method_9244("volume", FloatArgumentType.floatArg(0.0F))
									.executes(
										commandContext -> method_13504(
												commandContext.getSource(),
												class_2186.method_9312(commandContext, "targets"),
												class_2232.method_9443(commandContext, "sound"),
												arg,
												class_2277.method_9736(commandContext, "pos"),
												commandContext.<Float>getArgument("volume", Float.class),
												1.0F,
												0.0F
											)
									)
									.then(
										class_2170.method_9244("pitch", FloatArgumentType.floatArg(0.0F, 2.0F))
											.executes(
												commandContext -> method_13504(
														commandContext.getSource(),
														class_2186.method_9312(commandContext, "targets"),
														class_2232.method_9443(commandContext, "sound"),
														arg,
														class_2277.method_9736(commandContext, "pos"),
														commandContext.<Float>getArgument("volume", Float.class),
														commandContext.<Float>getArgument("pitch", Float.class),
														0.0F
													)
											)
											.then(
												class_2170.method_9244("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F))
													.executes(
														commandContext -> method_13504(
																commandContext.getSource(),
																class_2186.method_9312(commandContext, "targets"),
																class_2232.method_9443(commandContext, "sound"),
																arg,
																class_2277.method_9736(commandContext, "pos"),
																commandContext.<Float>getArgument("volume", Float.class),
																commandContext.<Float>getArgument("pitch", Float.class),
																commandContext.<Float>getArgument("minVolume", Float.class)
															)
													)
											)
									)
							)
					)
			);
	}

	private static int method_13504(class_2168 arg, Collection<class_3222> collection, class_2960 arg2, class_3419 arg3, class_243 arg4, float f, float g, float h) throws CommandSyntaxException {
		double d = Math.pow(f > 1.0F ? (double)(f * 16.0F) : 16.0, 2.0);
		int i = 0;

		for (class_3222 lv : collection) {
			double e = arg4.field_1352 - lv.field_5987;
			double j = arg4.field_1351 - lv.field_6010;
			double k = arg4.field_1350 - lv.field_6035;
			double l = e * e + j * j + k * k;
			class_243 lv2 = arg4;
			float m = f;
			if (l > d) {
				if (h <= 0.0F) {
					continue;
				}

				double n = (double)class_3532.method_15368(l);
				lv2 = new class_243(lv.field_5987 + e / n * 2.0, lv.field_6010 + j / n * 2.0, lv.field_6035 + k / n * 2.0);
				m = h;
			}

			lv.field_13987.method_14364(new class_2660(arg2, arg3, lv2, m, g));
			i++;
		}

		if (i == 0) {
			throw field_13678.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.playsound.success.single", arg2, ((class_3222)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.playsound.success.single", arg2, ((class_3222)collection.iterator().next()).method_5476()), true);
			}

			return i;
		}
	}
}
