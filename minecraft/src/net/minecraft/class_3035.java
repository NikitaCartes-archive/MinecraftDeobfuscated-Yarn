package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;

public class class_3035 {
	public static void method_13166(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("defaultgamemode").requires(arg -> arg.method_9259(2));

		for (class_1934 lv : class_1934.values()) {
			if (lv != class_1934.field_9218) {
				literalArgumentBuilder.then(class_2170.method_9247(lv.method_8381()).executes(commandContext -> method_13167(commandContext.getSource(), lv)));
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_13167(class_2168 arg, class_1934 arg2) {
		int i = 0;
		MinecraftServer minecraftServer = arg.method_9211();
		minecraftServer.method_3838(arg2);
		if (minecraftServer.method_3761()) {
			for (class_3222 lv : minecraftServer.method_3760().method_14571()) {
				if (lv.field_13974.method_14257() != arg2) {
					lv.method_7336(arg2);
					i++;
				}
			}
		}

		arg.method_9226(new class_2588("commands.defaultgamemode.success", arg2.method_8383()), true);
		return i;
	}
}
