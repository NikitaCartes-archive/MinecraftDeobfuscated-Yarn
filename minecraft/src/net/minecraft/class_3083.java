package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3083 {
	private static final SimpleCommandExceptionType field_13667 = new SimpleCommandExceptionType(new class_2588("commands.op.failed"));

	public static void method_13464(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("op")
				.requires(arg -> arg.method_9259(3))
				.then(
					class_2170.method_9244("targets", class_2191.method_9329())
						.suggests(
							(commandContext, suggestionsBuilder) -> {
								class_3324 lv = commandContext.getSource().method_9211().method_3760();
								return class_2172.method_9264(
									lv.method_14571().stream().filter(arg2 -> !lv.method_14569(arg2.method_7334())).map(arg -> arg.method_7334().getName()), suggestionsBuilder
								);
							}
						)
						.executes(commandContext -> method_13465(commandContext.getSource(), class_2191.method_9330(commandContext, "targets")))
				)
		);
	}

	private static int method_13465(class_2168 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		class_3324 lv = arg.method_9211().method_3760();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!lv.method_14569(gameProfile)) {
				lv.method_14582(gameProfile);
				i++;
				arg.method_9226(new class_2588("commands.op.success", ((GameProfile)collection.iterator().next()).getName()), true);
			}
		}

		if (i == 0) {
			throw field_13667.create();
		} else {
			return i;
		}
	}
}
