package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3030 {
	private static final SimpleCommandExceptionType field_13507 = new SimpleCommandExceptionType(new class_2588("commands.deop.failed"));

	public static void method_13143(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("deop")
				.requires(arg -> arg.method_9259(3))
				.then(
					class_2170.method_9244("targets", class_2191.method_9329())
						.suggests(
							(commandContext, suggestionsBuilder) -> class_2172.method_9253(commandContext.getSource().method_9211().method_3760().method_14584(), suggestionsBuilder)
						)
						.executes(commandContext -> method_13144(commandContext.getSource(), class_2191.method_9330(commandContext, "targets")))
				)
		);
	}

	private static int method_13144(class_2168 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		class_3324 lv = arg.method_9211().method_3760();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (lv.method_14569(gameProfile)) {
				lv.method_14604(gameProfile);
				i++;
				arg.method_9226(new class_2588("commands.deop.success", ((GameProfile)collection.iterator().next()).getName()), true);
			}
		}

		if (i == 0) {
			throw field_13507.create();
		} else {
			arg.method_9211().method_3728(arg);
			return i;
		}
	}
}
