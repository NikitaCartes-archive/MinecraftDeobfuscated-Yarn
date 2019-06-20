package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class class_3107 {
	private static final SimpleCommandExceptionType field_13704 = new SimpleCommandExceptionType(new class_2588("commands.save.alreadyOn"));

	public static void method_13559(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(class_2170.method_9247("save-on").requires(arg -> arg.method_9259(4)).executes(commandContext -> {
			class_2168 lv = commandContext.getSource();
			boolean bl = false;

			for (class_3218 lv2 : lv.method_9211().method_3738()) {
				if (lv2 != null && lv2.field_13957) {
					lv2.field_13957 = false;
					bl = true;
				}
			}

			if (!bl) {
				throw field_13704.create();
			} else {
				lv.method_9226(new class_2588("commands.save.enabled"), true);
				return 1;
			}
		}));
	}
}
