package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public class class_3065 {
	public static void method_13392(CommandDispatcher<class_2168> commandDispatcher) {
		final LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("gamerule").requires(arg -> arg.method_9259(2));
		class_1928.method_20744(
			new class_1928.class_4311() {
				@Override
				public <T extends class_1928.class_4315<T>> void method_20762(class_1928.class_4313<T> arg, class_1928.class_4314<T> arg2) {
					literalArgumentBuilder.then(
						class_2170.method_9247(arg.method_20771())
							.executes(commandContext -> class_3065.method_13397(commandContext.getSource(), arg))
							.then(arg2.method_20775("value").executes(commandContext -> class_3065.method_13394(commandContext, arg)))
					);
				}
			}
		);
		commandDispatcher.register(literalArgumentBuilder);
	}

	private static <T extends class_1928.class_4315<T>> int method_13394(CommandContext<class_2168> commandContext, class_1928.class_4313<T> arg) {
		class_2168 lv = commandContext.getSource();
		T lv2 = lv.method_9211().method_3767().method_20746(arg);
		lv2.method_20780(commandContext, "value");
		lv.method_9226(new class_2588("commands.gamerule.set", arg.method_20771(), lv2.toString()), true);
		return lv2.method_20781();
	}

	private static <T extends class_1928.class_4315<T>> int method_13397(class_2168 arg, class_1928.class_4313<T> arg2) {
		T lv = arg.method_9211().method_3767().method_20746(arg2);
		arg.method_9226(new class_2588("commands.gamerule.query", arg2.method_20771(), lv.toString()), false);
		return lv.method_20781();
	}
}
