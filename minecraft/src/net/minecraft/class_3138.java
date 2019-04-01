package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class class_3138 {
	private static final SimpleCommandExceptionType field_13741 = new SimpleCommandExceptionType(new class_2588("commands.summon.failed"));

	public static void method_13690(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("summon")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("entity", class_2188.method_9324())
						.suggests(class_2321.field_10935)
						.executes(
							commandContext -> method_13694(
									commandContext.getSource(), class_2188.method_9322(commandContext, "entity"), commandContext.getSource().method_9222(), new class_2487(), true
								)
						)
						.then(
							class_2170.method_9244("pos", class_2277.method_9737())
								.executes(
									commandContext -> method_13694(
											commandContext.getSource(), class_2188.method_9322(commandContext, "entity"), class_2277.method_9736(commandContext, "pos"), new class_2487(), true
										)
								)
								.then(
									class_2170.method_9244("nbt", class_2179.method_9284())
										.executes(
											commandContext -> method_13694(
													commandContext.getSource(),
													class_2188.method_9322(commandContext, "entity"),
													class_2277.method_9736(commandContext, "pos"),
													class_2179.method_9285(commandContext, "nbt"),
													false
												)
										)
								)
						)
				)
		);
	}

	private static int method_13694(class_2168 arg, class_2960 arg2, class_243 arg3, class_2487 arg4, boolean bl) throws CommandSyntaxException {
		class_2487 lv = arg4.method_10553();
		lv.method_10582("id", arg2.toString());
		if (class_1299.method_5890(class_1299.field_6112).equals(arg2)) {
			class_1538 lv2 = new class_1538(arg.method_9225(), arg3.field_1352, arg3.field_1351, arg3.field_1350, false);
			arg.method_9225().method_8416(lv2);
			arg.method_9226(new class_2588("commands.summon.success", lv2.method_5476()), true);
			return 1;
		} else {
			class_3218 lv3 = arg.method_9225();
			class_1297 lv4 = class_1299.method_17842(lv, lv3, arg3x -> {
				arg3x.method_5808(arg3.field_1352, arg3.field_1351, arg3.field_1350, arg3x.field_6031, arg3x.field_5965);
				return !lv3.method_18768(arg3x) ? null : arg3x;
			});
			if (lv4 == null) {
				throw field_13741.create();
			} else {
				if (bl && lv4 instanceof class_1308) {
					((class_1308)lv4).method_5943(arg.method_9225(), arg.method_9225().method_8404(new class_2338(lv4)), class_3730.field_16462, null, null);
				}

				arg.method_9226(new class_2588("commands.summon.success", lv4.method_5476()), true);
				return 1;
			}
		}
	}
}
