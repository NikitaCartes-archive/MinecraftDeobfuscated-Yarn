package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3089 {
	private static final SimpleCommandExceptionType field_13673 = new SimpleCommandExceptionType(new class_2588("commands.particle.failed"));

	public static void method_13486(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("particle")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("name", class_2223.method_9417())
						.executes(
							commandContext -> method_13491(
									commandContext.getSource(),
									class_2223.method_9421(commandContext, "name"),
									commandContext.getSource().method_9222(),
									class_243.field_1353,
									0.0F,
									0,
									false,
									commandContext.getSource().method_9211().method_3760().method_14571()
								)
						)
						.then(
							class_2170.method_9244("pos", class_2277.method_9737())
								.executes(
									commandContext -> method_13491(
											commandContext.getSource(),
											class_2223.method_9421(commandContext, "name"),
											class_2277.method_9736(commandContext, "pos"),
											class_243.field_1353,
											0.0F,
											0,
											false,
											commandContext.getSource().method_9211().method_3760().method_14571()
										)
								)
								.then(
									class_2170.method_9244("delta", class_2277.method_9735(false))
										.then(
											class_2170.method_9244("speed", FloatArgumentType.floatArg(0.0F))
												.then(
													class_2170.method_9244("count", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13491(
																	commandContext.getSource(),
																	class_2223.method_9421(commandContext, "name"),
																	class_2277.method_9736(commandContext, "pos"),
																	class_2277.method_9736(commandContext, "delta"),
																	FloatArgumentType.getFloat(commandContext, "speed"),
																	IntegerArgumentType.getInteger(commandContext, "count"),
																	false,
																	commandContext.getSource().method_9211().method_3760().method_14571()
																)
														)
														.then(
															class_2170.method_9247("force")
																.executes(
																	commandContext -> method_13491(
																			commandContext.getSource(),
																			class_2223.method_9421(commandContext, "name"),
																			class_2277.method_9736(commandContext, "pos"),
																			class_2277.method_9736(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			true,
																			commandContext.getSource().method_9211().method_3760().method_14571()
																		)
																)
																.then(
																	class_2170.method_9244("viewers", class_2186.method_9308())
																		.executes(
																			commandContext -> method_13491(
																					commandContext.getSource(),
																					class_2223.method_9421(commandContext, "name"),
																					class_2277.method_9736(commandContext, "pos"),
																					class_2277.method_9736(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					true,
																					class_2186.method_9312(commandContext, "viewers")
																				)
																		)
																)
														)
														.then(
															class_2170.method_9247("normal")
																.executes(
																	commandContext -> method_13491(
																			commandContext.getSource(),
																			class_2223.method_9421(commandContext, "name"),
																			class_2277.method_9736(commandContext, "pos"),
																			class_2277.method_9736(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			false,
																			commandContext.getSource().method_9211().method_3760().method_14571()
																		)
																)
																.then(
																	class_2170.method_9244("viewers", class_2186.method_9308())
																		.executes(
																			commandContext -> method_13491(
																					commandContext.getSource(),
																					class_2223.method_9421(commandContext, "name"),
																					class_2277.method_9736(commandContext, "pos"),
																					class_2277.method_9736(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					false,
																					class_2186.method_9312(commandContext, "viewers")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13491(class_2168 arg, class_2394 arg2, class_243 arg3, class_243 arg4, float f, int i, boolean bl, Collection<class_3222> collection) throws CommandSyntaxException {
		int j = 0;

		for (class_3222 lv : collection) {
			if (arg.method_9225()
				.method_14166(lv, arg2, bl, arg3.field_1352, arg3.field_1351, arg3.field_1350, i, arg4.field_1352, arg4.field_1351, arg4.field_1350, (double)f)) {
				j++;
			}
		}

		if (j == 0) {
			throw field_13673.create();
		} else {
			arg.method_9226(
				new class_2588("commands.particle.success", class_2378.field_11141.method_10221((class_2396<? extends class_2394>)arg2.method_10295()).toString()), true
			);
			return j;
		}
	}
}
