package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3086 {
	private static final SimpleCommandExceptionType field_13669 = new SimpleCommandExceptionType(new class_2588("commands.pardon.failed"));

	public static void method_13472(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("pardon")
				.requires(arg -> arg.method_9211().method_3760().method_14585().method_14639() && arg.method_9259(3))
				.then(
					class_2170.method_9244("targets", class_2191.method_9329())
						.suggests(
							(commandContext, suggestionsBuilder) -> class_2172.method_9253(
									commandContext.getSource().method_9211().method_3760().method_14563().method_14636(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_13473(commandContext.getSource(), class_2191.method_9330(commandContext, "targets")))
				)
		);
	}

	private static int method_13473(class_2168 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		class_3335 lv = arg.method_9211().method_3760().method_14563();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (lv.method_14650(gameProfile)) {
				lv.method_14635(gameProfile);
				i++;
				arg.method_9226(new class_2588("commands.pardon.success", class_2564.method_10882(gameProfile)), true);
			}
		}

		if (i == 0) {
			throw field_13669.create();
		} else {
			return i;
		}
	}
}
