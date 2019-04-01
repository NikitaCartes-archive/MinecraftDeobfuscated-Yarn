package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;

public class class_3156 {
	private static final SimpleCommandExceptionType field_13767 = new SimpleCommandExceptionType(new class_2588("commands.whitelist.alreadyOn"));
	private static final SimpleCommandExceptionType field_13770 = new SimpleCommandExceptionType(new class_2588("commands.whitelist.alreadyOff"));
	private static final SimpleCommandExceptionType field_13768 = new SimpleCommandExceptionType(new class_2588("commands.whitelist.add.failed"));
	private static final SimpleCommandExceptionType field_13769 = new SimpleCommandExceptionType(new class_2588("commands.whitelist.remove.failed"));

	public static void method_13836(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("whitelist")
				.requires(arg -> arg.method_9259(3))
				.then(class_2170.method_9247("on").executes(commandContext -> method_13839(commandContext.getSource())))
				.then(class_2170.method_9247("off").executes(commandContext -> method_13837(commandContext.getSource())))
				.then(class_2170.method_9247("list").executes(commandContext -> method_13840(commandContext.getSource())))
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("targets", class_2191.method_9329())
								.suggests(
									(commandContext, suggestionsBuilder) -> {
										class_3324 lv = commandContext.getSource().method_9211().method_3760();
										return class_2172.method_9264(
											lv.method_14571().stream().filter(arg2 -> !lv.method_14590().method_14653(arg2.method_7334())).map(arg -> arg.method_7334().getName()),
											suggestionsBuilder
										);
									}
								)
								.executes(commandContext -> method_13838(commandContext.getSource(), class_2191.method_9330(commandContext, "targets")))
						)
				)
				.then(
					class_2170.method_9247("remove")
						.then(
							class_2170.method_9244("targets", class_2191.method_9329())
								.suggests(
									(commandContext, suggestionsBuilder) -> class_2172.method_9253(
											commandContext.getSource().method_9211().method_3760().method_14560(), suggestionsBuilder
										)
								)
								.executes(commandContext -> method_13845(commandContext.getSource(), class_2191.method_9330(commandContext, "targets")))
						)
				)
				.then(class_2170.method_9247("reload").executes(commandContext -> method_13850(commandContext.getSource())))
		);
	}

	private static int method_13850(class_2168 arg) {
		arg.method_9211().method_3760().method_14599();
		arg.method_9226(new class_2588("commands.whitelist.reloaded"), true);
		arg.method_9211().method_3728(arg);
		return 1;
	}

	private static int method_13838(class_2168 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		class_3337 lv = arg.method_9211().method_3760().method_14590();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!lv.method_14653(gameProfile)) {
				class_3340 lv2 = new class_3340(gameProfile);
				lv.method_14633(lv2);
				arg.method_9226(new class_2588("commands.whitelist.add.success", class_2564.method_10882(gameProfile)), true);
				i++;
			}
		}

		if (i == 0) {
			throw field_13768.create();
		} else {
			return i;
		}
	}

	private static int method_13845(class_2168 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		class_3337 lv = arg.method_9211().method_3760().method_14590();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (lv.method_14653(gameProfile)) {
				class_3340 lv2 = new class_3340(gameProfile);
				lv.method_14638(lv2);
				arg.method_9226(new class_2588("commands.whitelist.remove.success", class_2564.method_10882(gameProfile)), true);
				i++;
			}
		}

		if (i == 0) {
			throw field_13769.create();
		} else {
			arg.method_9211().method_3728(arg);
			return i;
		}
	}

	private static int method_13839(class_2168 arg) throws CommandSyntaxException {
		class_3324 lv = arg.method_9211().method_3760();
		if (lv.method_14614()) {
			throw field_13767.create();
		} else {
			lv.method_14557(true);
			arg.method_9226(new class_2588("commands.whitelist.enabled"), true);
			arg.method_9211().method_3728(arg);
			return 1;
		}
	}

	private static int method_13837(class_2168 arg) throws CommandSyntaxException {
		class_3324 lv = arg.method_9211().method_3760();
		if (!lv.method_14614()) {
			throw field_13770.create();
		} else {
			lv.method_14557(false);
			arg.method_9226(new class_2588("commands.whitelist.disabled"), true);
			return 1;
		}
	}

	private static int method_13840(class_2168 arg) {
		String[] strings = arg.method_9211().method_3760().method_14560();
		if (strings.length == 0) {
			arg.method_9226(new class_2588("commands.whitelist.none"), false);
		} else {
			arg.method_9226(new class_2588("commands.whitelist.list", strings.length, String.join(", ", strings)), false);
		}

		return strings.length;
	}
}
