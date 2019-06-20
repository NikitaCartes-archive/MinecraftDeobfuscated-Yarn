package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;

public class class_3016 {
	private static final SimpleCommandExceptionType field_13473 = new SimpleCommandExceptionType(new class_2588("commands.ban.failed"));

	public static void method_13021(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("ban")
				.requires(arg -> arg.method_9211().method_3760().method_14563().method_14639() && arg.method_9259(3))
				.then(
					class_2170.method_9244("targets", class_2191.method_9329())
						.executes(commandContext -> method_13022(commandContext.getSource(), class_2191.method_9330(commandContext, "targets"), null))
						.then(
							class_2170.method_9244("reason", class_2196.method_9340())
								.executes(
									commandContext -> method_13022(
											commandContext.getSource(), class_2191.method_9330(commandContext, "targets"), class_2196.method_9339(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_13022(class_2168 arg, Collection<GameProfile> collection, @Nullable class_2561 arg2) throws CommandSyntaxException {
		class_3335 lv = arg.method_9211().method_3760().method_14563();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!lv.method_14650(gameProfile)) {
				class_3336 lv2 = new class_3336(gameProfile, null, arg.method_9214(), null, arg2 == null ? null : arg2.getString());
				lv.method_14633(lv2);
				i++;
				arg.method_9226(new class_2588("commands.ban.success", class_2564.method_10882(gameProfile), lv2.method_14503()), true);
				class_3222 lv3 = arg.method_9211().method_3760().method_14602(gameProfile.getId());
				if (lv3 != null) {
					lv3.field_13987.method_14367(new class_2588("multiplayer.disconnect.banned"));
				}
			}
		}

		if (i == 0) {
			throw field_13473.create();
		} else {
			return i;
		}
	}
}
