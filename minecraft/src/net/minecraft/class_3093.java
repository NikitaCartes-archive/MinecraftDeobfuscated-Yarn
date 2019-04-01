package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class class_3093 {
	private static final SimpleCommandExceptionType field_13680 = new SimpleCommandExceptionType(new class_2588("commands.publish.failed"));
	private static final DynamicCommandExceptionType field_13679 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.publish.alreadyPublished", object)
	);

	public static void method_13510(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("publish")
				.requires(arg -> arg.method_9211().method_3724() && arg.method_9259(4))
				.executes(commandContext -> method_13509(commandContext.getSource(), class_3521.method_15302()))
				.then(
					class_2170.method_9244("port", IntegerArgumentType.integer(0, 65535))
						.executes(commandContext -> method_13509(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))
				)
		);
	}

	private static int method_13509(class_2168 arg, int i) throws CommandSyntaxException {
		if (arg.method_9211().method_3860()) {
			throw field_13679.create(arg.method_9211().method_3756());
		} else if (!arg.method_9211().method_3763(arg.method_9211().method_3790(), false, i)) {
			throw field_13680.create();
		} else {
			arg.method_9226(new class_2588("commands.publish.success", i), true);
			return i;
		}
	}
}
