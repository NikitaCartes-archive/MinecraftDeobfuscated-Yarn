package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;

public class class_3078 {
	public static void method_13435(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("list")
				.executes(commandContext -> method_13437(commandContext.getSource()))
				.then(class_2170.method_9247("uuids").executes(commandContext -> method_13436(commandContext.getSource())))
		);
	}

	private static int method_13437(class_2168 arg) {
		return method_13434(arg, class_1657::method_5476);
	}

	private static int method_13436(class_2168 arg) {
		return method_13434(arg, class_1657::method_7306);
	}

	private static int method_13434(class_2168 arg, Function<class_3222, class_2561> function) {
		class_3324 lv = arg.method_9211().method_3760();
		List<class_3222> list = lv.method_14571();
		class_2561 lv2 = class_2564.method_10884(list, function);
		arg.method_9226(new class_2588("commands.list.players", list.size(), lv.method_14592(), lv2), false);
		return list.size();
	}
}
