package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;

public class class_3043 {
	private static final SimpleCommandExceptionType field_13607 = new SimpleCommandExceptionType(new class_2588("commands.effect.give.failed"));
	private static final SimpleCommandExceptionType field_13609 = new SimpleCommandExceptionType(new class_2588("commands.effect.clear.everything.failed"));
	private static final SimpleCommandExceptionType field_13608 = new SimpleCommandExceptionType(new class_2588("commands.effect.clear.specific.failed"));

	public static void method_13229(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("effect")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("clear")
						.then(
							class_2170.method_9244("targets", class_2186.method_9306())
								.executes(commandContext -> method_13230(commandContext.getSource(), class_2186.method_9317(commandContext, "targets")))
								.then(
									class_2170.method_9244("effect", class_2201.method_9350())
										.executes(
											commandContext -> method_13231(
													commandContext.getSource(), class_2186.method_9317(commandContext, "targets"), class_2201.method_9347(commandContext, "effect")
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("give")
						.then(
							class_2170.method_9244("targets", class_2186.method_9306())
								.then(
									class_2170.method_9244("effect", class_2201.method_9350())
										.executes(
											commandContext -> method_13227(
													commandContext.getSource(), class_2186.method_9317(commandContext, "targets"), class_2201.method_9347(commandContext, "effect"), null, 0, true
												)
										)
										.then(
											class_2170.method_9244("seconds", IntegerArgumentType.integer(1, 1000000))
												.executes(
													commandContext -> method_13227(
															commandContext.getSource(),
															class_2186.method_9317(commandContext, "targets"),
															class_2201.method_9347(commandContext, "effect"),
															IntegerArgumentType.getInteger(commandContext, "seconds"),
															0,
															true
														)
												)
												.then(
													class_2170.method_9244("amplifier", IntegerArgumentType.integer(0, 255))
														.executes(
															commandContext -> method_13227(
																	commandContext.getSource(),
																	class_2186.method_9317(commandContext, "targets"),
																	class_2201.method_9347(commandContext, "effect"),
																	IntegerArgumentType.getInteger(commandContext, "seconds"),
																	IntegerArgumentType.getInteger(commandContext, "amplifier"),
																	true
																)
														)
														.then(
															class_2170.method_9244("hideParticles", BoolArgumentType.bool())
																.executes(
																	commandContext -> method_13227(
																			commandContext.getSource(),
																			class_2186.method_9317(commandContext, "targets"),
																			class_2201.method_9347(commandContext, "effect"),
																			IntegerArgumentType.getInteger(commandContext, "seconds"),
																			IntegerArgumentType.getInteger(commandContext, "amplifier"),
																			!BoolArgumentType.getBool(commandContext, "hideParticles")
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

	private static int method_13227(class_2168 arg, Collection<? extends class_1297> collection, class_1291 arg2, @Nullable Integer integer, int i, boolean bl) throws CommandSyntaxException {
		int j = 0;
		int k;
		if (integer != null) {
			if (arg2.method_5561()) {
				k = integer;
			} else {
				k = integer * 20;
			}
		} else if (arg2.method_5561()) {
			k = 1;
		} else {
			k = 600;
		}

		for (class_1297 lv : collection) {
			if (lv instanceof class_1309) {
				class_1293 lv2 = new class_1293(arg2, k, i, false, bl);
				if (((class_1309)lv).method_6092(lv2)) {
					j++;
				}
			}
		}

		if (j == 0) {
			throw field_13607.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(
					new class_2588("commands.effect.give.success.single", arg2.method_5560(), ((class_1297)collection.iterator().next()).method_5476(), k / 20), true
				);
			} else {
				arg.method_9226(new class_2588("commands.effect.give.success.multiple", arg2.method_5560(), collection.size(), k / 20), true);
			}

			return j;
		}
	}

	private static int method_13230(class_2168 arg, Collection<? extends class_1297> collection) throws CommandSyntaxException {
		int i = 0;

		for (class_1297 lv : collection) {
			if (lv instanceof class_1309 && ((class_1309)lv).method_6012()) {
				i++;
			}
		}

		if (i == 0) {
			throw field_13609.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.effect.clear.everything.success.single", ((class_1297)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.effect.clear.everything.success.multiple", collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13231(class_2168 arg, Collection<? extends class_1297> collection, class_1291 arg2) throws CommandSyntaxException {
		int i = 0;

		for (class_1297 lv : collection) {
			if (lv instanceof class_1309 && ((class_1309)lv).method_6016(arg2)) {
				i++;
			}
		}

		if (i == 0) {
			throw field_13608.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(
					new class_2588("commands.effect.clear.specific.success.single", arg2.method_5560(), ((class_1297)collection.iterator().next()).method_5476()), true
				);
			} else {
				arg.method_9226(new class_2588("commands.effect.clear.specific.success.multiple", arg2.method_5560(), collection.size()), true);
			}

			return i;
		}
	}
}
