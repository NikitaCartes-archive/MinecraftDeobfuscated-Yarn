package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;

public class class_3027 {
	private static final DynamicCommandExceptionType field_13503 = new DynamicCommandExceptionType(object -> new class_2588("commands.datapack.unknown", object));
	private static final DynamicCommandExceptionType field_13504 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.datapack.enable.failed", object)
	);
	private static final DynamicCommandExceptionType field_13505 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.datapack.disable.failed", object)
	);
	private static final SuggestionProvider<class_2168> field_13506 = (commandContext, suggestionsBuilder) -> class_2172.method_9264(
			commandContext.getSource().method_9211().method_3836().method_14444().stream().map(class_3288::method_14463).map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);
	private static final SuggestionProvider<class_2168> field_13502 = (commandContext, suggestionsBuilder) -> class_2172.method_9264(
			commandContext.getSource().method_9211().method_3836().method_14442().stream().map(class_3288::method_14463).map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);

	public static void method_13125(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("datapack")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("enable")
						.then(
							class_2170.method_9244("name", StringArgumentType.string())
								.suggests(field_13502)
								.executes(
									commandContext -> method_13114(
											commandContext.getSource(),
											method_13127(commandContext, "name", true),
											(list, arg) -> arg.method_14466().method_14468(list, arg, argx -> argx, false)
										)
								)
								.then(
									class_2170.method_9247("after")
										.then(
											class_2170.method_9244("existing", StringArgumentType.string())
												.suggests(field_13506)
												.executes(
													commandContext -> method_13114(
															commandContext.getSource(),
															method_13127(commandContext, "name", true),
															(list, arg) -> list.add(list.indexOf(method_13127(commandContext, "existing", false)) + 1, arg)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("before")
										.then(
											class_2170.method_9244("existing", StringArgumentType.string())
												.suggests(field_13506)
												.executes(
													commandContext -> method_13114(
															commandContext.getSource(),
															method_13127(commandContext, "name", true),
															(list, arg) -> list.add(list.indexOf(method_13127(commandContext, "existing", false)), arg)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("last")
										.executes(commandContext -> method_13114(commandContext.getSource(), method_13127(commandContext, "name", true), List::add))
								)
								.then(
									class_2170.method_9247("first")
										.executes(commandContext -> method_13114(commandContext.getSource(), method_13127(commandContext, "name", true), (list, arg) -> list.add(0, arg)))
								)
						)
				)
				.then(
					class_2170.method_9247("disable")
						.then(
							class_2170.method_9244("name", StringArgumentType.string())
								.suggests(field_13506)
								.executes(commandContext -> method_13140(commandContext.getSource(), method_13127(commandContext, "name", false)))
						)
				)
				.then(
					class_2170.method_9247("list")
						.executes(commandContext -> method_13121(commandContext.getSource()))
						.then(class_2170.method_9247("available").executes(commandContext -> method_13128(commandContext.getSource())))
						.then(class_2170.method_9247("enabled").executes(commandContext -> method_13126(commandContext.getSource())))
				)
		);
	}

	private static int method_13114(class_2168 arg, class_3288 arg2, class_3027.class_3028 arg3) throws CommandSyntaxException {
		class_3283<class_3288> lv = arg.method_9211().method_3836();
		List<class_3288> list = Lists.<class_3288>newArrayList(lv.method_14444());
		arg3.apply(list, arg2);
		lv.method_14447(list);
		class_31 lv2 = arg.method_9211().method_3847(class_2874.field_13072).method_8401();
		lv2.method_179().clear();
		lv.method_14444().forEach(arg2x -> lv2.method_179().add(arg2x.method_14463()));
		lv2.method_209().remove(arg2.method_14463());
		arg.method_9226(new class_2588("commands.datapack.enable.success", arg2.method_14461(true)), true);
		arg.method_9211().method_3848();
		return lv.method_14444().size();
	}

	private static int method_13140(class_2168 arg, class_3288 arg2) {
		class_3283<class_3288> lv = arg.method_9211().method_3836();
		List<class_3288> list = Lists.<class_3288>newArrayList(lv.method_14444());
		list.remove(arg2);
		lv.method_14447(list);
		class_31 lv2 = arg.method_9211().method_3847(class_2874.field_13072).method_8401();
		lv2.method_179().clear();
		lv.method_14444().forEach(arg2x -> lv2.method_179().add(arg2x.method_14463()));
		lv2.method_209().add(arg2.method_14463());
		arg.method_9226(new class_2588("commands.datapack.disable.success", arg2.method_14461(true)), true);
		arg.method_9211().method_3848();
		return lv.method_14444().size();
	}

	private static int method_13121(class_2168 arg) {
		return method_13126(arg) + method_13128(arg);
	}

	private static int method_13128(class_2168 arg) {
		class_3283<class_3288> lv = arg.method_9211().method_3836();
		if (lv.method_14442().isEmpty()) {
			arg.method_9226(new class_2588("commands.datapack.list.available.none"), false);
		} else {
			arg.method_9226(
				new class_2588(
					"commands.datapack.list.available.success", lv.method_14442().size(), class_2564.method_10884(lv.method_14442(), argx -> argx.method_14461(false))
				),
				false
			);
		}

		return lv.method_14442().size();
	}

	private static int method_13126(class_2168 arg) {
		class_3283<class_3288> lv = arg.method_9211().method_3836();
		if (lv.method_14444().isEmpty()) {
			arg.method_9226(new class_2588("commands.datapack.list.enabled.none"), false);
		} else {
			arg.method_9226(
				new class_2588(
					"commands.datapack.list.enabled.success", lv.method_14444().size(), class_2564.method_10884(lv.method_14444(), argx -> argx.method_14461(true))
				),
				false
			);
		}

		return lv.method_14444().size();
	}

	private static class_3288 method_13127(CommandContext<class_2168> commandContext, String string, boolean bl) throws CommandSyntaxException {
		String string2 = StringArgumentType.getString(commandContext, string);
		class_3283<class_3288> lv = commandContext.getSource().method_9211().method_3836();
		class_3288 lv2 = lv.method_14449(string2);
		if (lv2 == null) {
			throw field_13503.create(string2);
		} else {
			boolean bl2 = lv.method_14444().contains(lv2);
			if (bl && bl2) {
				throw field_13504.create(string2);
			} else if (!bl && !bl2) {
				throw field_13505.create(string2);
			} else {
				return lv2;
			}
		}
	}

	interface class_3028 {
		void apply(List<class_3288> list, class_3288 arg) throws CommandSyntaxException;
	}
}
