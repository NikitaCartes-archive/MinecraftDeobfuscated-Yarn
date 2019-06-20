package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.function.Consumer;

public class class_3945 {
	private static final SimpleCommandExceptionType field_17440 = new SimpleCommandExceptionType(new class_2588("commands.teammsg.failed.noteam"));

	public static void method_17600(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralCommandNode<class_2168> literalCommandNode = commandDispatcher.register(
			class_2170.method_9247("teammsg")
				.then(
					class_2170.method_9244("message", class_2196.method_9340())
						.executes(commandContext -> method_17599(commandContext.getSource(), class_2196.method_9339(commandContext, "message")))
				)
		);
		commandDispatcher.register(class_2170.method_9247("tm").redirect(literalCommandNode));
	}

	private static int method_17599(class_2168 arg, class_2561 arg2) throws CommandSyntaxException {
		class_1297 lv = arg.method_9229();
		class_268 lv2 = (class_268)lv.method_5781();
		if (lv2 == null) {
			throw field_17440.create();
		} else {
			Consumer<class_2583> consumer = argx -> argx.method_10949(new class_2568(class_2568.class_2569.field_11762, new class_2588("chat.type.team.hover")))
					.method_10958(new class_2558(class_2558.class_2559.field_11745, "/teammsg "));
			class_2561 lv3 = lv2.method_1148().method_10859(consumer);

			for (class_2561 lv4 : lv3.method_10855()) {
				lv4.method_10859(consumer);
			}

			List<class_3222> list = arg.method_9211().method_3760().method_14571();

			for (class_3222 lv5 : list) {
				if (lv5 == lv) {
					lv5.method_9203(new class_2588("chat.type.team.sent", lv3, arg.method_9223(), arg2.method_10853()));
				} else if (lv5.method_5781() == lv2) {
					lv5.method_9203(new class_2588("chat.type.team.text", lv3, arg.method_9223(), arg2.method_10853()));
				}
			}

			return list.size();
		}
	}
}
