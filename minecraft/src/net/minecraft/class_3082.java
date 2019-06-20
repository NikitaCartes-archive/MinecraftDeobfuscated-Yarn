package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;

public class class_3082 {
	public static void method_13461(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralCommandNode<class_2168> literalCommandNode = commandDispatcher.register(
			class_2170.method_9247("msg")
				.then(
					class_2170.method_9244("targets", class_2186.method_9308())
						.then(
							class_2170.method_9244("message", class_2196.method_9340())
								.executes(
									commandContext -> method_13462(
											commandContext.getSource(), class_2186.method_9312(commandContext, "targets"), class_2196.method_9339(commandContext, "message")
										)
								)
						)
				)
		);
		commandDispatcher.register(class_2170.method_9247("tell").redirect(literalCommandNode));
		commandDispatcher.register(class_2170.method_9247("w").redirect(literalCommandNode));
	}

	private static int method_13462(class_2168 arg, Collection<class_3222> collection, class_2561 arg2) {
		for (class_3222 lv : collection) {
			lv.method_9203(
				new class_2588("commands.message.display.incoming", arg.method_9223(), arg2.method_10853())
					.method_10856(new class_124[]{class_124.field_1080, class_124.field_1056})
			);
			arg.method_9226(
				new class_2588("commands.message.display.outgoing", lv.method_5476(), arg2.method_10853())
					.method_10856(new class_124[]{class_124.field_1080, class_124.field_1056}),
				false
			);
		}

		return collection.size();
	}
}
