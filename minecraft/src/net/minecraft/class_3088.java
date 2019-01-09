package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;

public class class_3088 {
	private static final SimpleCommandExceptionType field_13671 = new SimpleCommandExceptionType(new class_2588("commands.pardonip.invalid"));
	private static final SimpleCommandExceptionType field_13672 = new SimpleCommandExceptionType(new class_2588("commands.pardonip.failed"));

	public static void method_13478(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("pardon-ip")
				.requires(arg -> arg.method_9211().method_3760().method_14585().method_14639() && arg.method_9259(3))
				.then(
					class_2170.method_9244("target", StringArgumentType.word())
						.suggests(
							(commandContext, suggestionsBuilder) -> class_2172.method_9253(
									commandContext.getSource().method_9211().method_3760().method_14585().method_14636(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_13482(commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))
				)
		);
	}

	private static int method_13482(class_2168 arg, String string) throws CommandSyntaxException {
		Matcher matcher = class_3012.field_13466.matcher(string);
		if (!matcher.matches()) {
			throw field_13671.create();
		} else {
			class_3317 lv = arg.method_9211().method_3760().method_14585();
			if (!lv.method_14529(string)) {
				throw field_13672.create();
			} else {
				lv.method_14635(string);
				arg.method_9226(new class_2588("commands.pardonip.success", string), true);
				return 1;
			}
		}
	}
}
