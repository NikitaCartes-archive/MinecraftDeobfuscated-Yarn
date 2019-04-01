package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;

public class class_3036 {
	private static final DynamicCommandExceptionType field_13602 = new DynamicCommandExceptionType(object -> new class_2588("commands.difficulty.failure", object));

	public static void method_13169(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("difficulty");

		for (class_1267 lv : class_1267.values()) {
			literalArgumentBuilder.then(class_2170.method_9247(lv.method_5460()).executes(commandContext -> method_13173(commandContext.getSource(), lv)));
		}

		commandDispatcher.register(literalArgumentBuilder.requires(arg -> arg.method_9259(2)).executes(commandContext -> {
			class_1267 lvx = commandContext.getSource().method_9225().method_8407();
			commandContext.getSource().method_9226(new class_2588("commands.difficulty.query", lvx.method_5463()), false);
			return lvx.method_5461();
		}));
	}

	public static int method_13173(class_2168 arg, class_1267 arg2) throws CommandSyntaxException {
		MinecraftServer minecraftServer = arg.method_9211();
		if (minecraftServer.method_3847(class_2874.field_13072).method_8407() == arg2) {
			throw field_13602.create(arg2.method_5460());
		} else {
			minecraftServer.method_3776(arg2, true);
			arg.method_9226(new class_2588("commands.difficulty.success", arg2.method_5463()), true);
			return 0;
		}
	}
}
