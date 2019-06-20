package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class class_3012 {
	public static final Pattern field_13466 = Pattern.compile(
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
	);
	private static final SimpleCommandExceptionType field_13468 = new SimpleCommandExceptionType(new class_2588("commands.banip.invalid"));
	private static final SimpleCommandExceptionType field_13467 = new SimpleCommandExceptionType(new class_2588("commands.banip.failed"));

	public static void method_13008(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("ban-ip")
				.requires(arg -> arg.method_9211().method_3760().method_14585().method_14639() && arg.method_9259(3))
				.then(
					class_2170.method_9244("target", StringArgumentType.word())
						.executes(commandContext -> method_13009(commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), null))
						.then(
							class_2170.method_9244("reason", class_2196.method_9340())
								.executes(
									commandContext -> method_13009(
											commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), class_2196.method_9339(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_13009(class_2168 arg, String string, @Nullable class_2561 arg2) throws CommandSyntaxException {
		Matcher matcher = field_13466.matcher(string);
		if (matcher.matches()) {
			return method_13007(arg, string, arg2);
		} else {
			class_3222 lv = arg.method_9211().method_3760().method_14566(string);
			if (lv != null) {
				return method_13007(arg, lv.method_14209(), arg2);
			} else {
				throw field_13468.create();
			}
		}
	}

	private static int method_13007(class_2168 arg, String string, @Nullable class_2561 arg2) throws CommandSyntaxException {
		class_3317 lv = arg.method_9211().method_3760().method_14585();
		if (lv.method_14529(string)) {
			throw field_13467.create();
		} else {
			List<class_3222> list = arg.method_9211().method_3760().method_14559(string);
			class_3320 lv2 = new class_3320(string, null, arg.method_9214(), null, arg2 == null ? null : arg2.getString());
			lv.method_14633(lv2);
			arg.method_9226(new class_2588("commands.banip.success", string, lv2.method_14503()), true);
			if (!list.isEmpty()) {
				arg.method_9226(new class_2588("commands.banip.info", list.size(), class_2300.method_9822(list)), true);
			}

			for (class_3222 lv3 : list) {
				lv3.field_13987.method_14367(new class_2588("multiplayer.disconnect.ip_banned"));
			}

			return list.size();
		}
	}
}
