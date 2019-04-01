package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class class_3106 {
	private static final SimpleCommandExceptionType field_13703 = new SimpleCommandExceptionType(new class_2588("commands.save.alreadyOff"));

	public static void method_13556(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(class_2170.method_9247("save-off").requires(arg -> arg.method_9259(4)).executes(commandContext -> {
			class_2168 lv = commandContext.getSource();
			boolean bl = false;

			for (class_3218 lv2 : lv.method_9211().method_3738()) {
				if (lv2 != null && !lv2.field_13957) {
					lv2.field_13957 = true;
					bl = true;
				}
			}

			if (!bl) {
				throw field_13703.create();
			} else {
				lv.method_9226(new class_2588("commands.save.disabled"), true);
				return 1;
			}
		}));
	}
}
