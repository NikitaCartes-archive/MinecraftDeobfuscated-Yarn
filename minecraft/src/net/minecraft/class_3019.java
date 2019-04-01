package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;

public class class_3019 {
	private static final DynamicCommandExceptionType field_13478 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.bossbar.create.failed", object)
	);
	private static final DynamicCommandExceptionType field_13486 = new DynamicCommandExceptionType(object -> new class_2588("commands.bossbar.unknown", object));
	private static final SimpleCommandExceptionType field_13483 = new SimpleCommandExceptionType(new class_2588("commands.bossbar.set.players.unchanged"));
	private static final SimpleCommandExceptionType field_13476 = new SimpleCommandExceptionType(new class_2588("commands.bossbar.set.name.unchanged"));
	private static final SimpleCommandExceptionType field_13480 = new SimpleCommandExceptionType(new class_2588("commands.bossbar.set.color.unchanged"));
	private static final SimpleCommandExceptionType field_13481 = new SimpleCommandExceptionType(new class_2588("commands.bossbar.set.style.unchanged"));
	private static final SimpleCommandExceptionType field_13477 = new SimpleCommandExceptionType(new class_2588("commands.bossbar.set.value.unchanged"));
	private static final SimpleCommandExceptionType field_13484 = new SimpleCommandExceptionType(new class_2588("commands.bossbar.set.max.unchanged"));
	private static final SimpleCommandExceptionType field_13479 = new SimpleCommandExceptionType(
		new class_2588("commands.bossbar.set.visibility.unchanged.hidden")
	);
	private static final SimpleCommandExceptionType field_13485 = new SimpleCommandExceptionType(
		new class_2588("commands.bossbar.set.visibility.unchanged.visible")
	);
	public static final SuggestionProvider<class_2168> field_13482 = (commandContext, suggestionsBuilder) -> class_2172.method_9270(
			commandContext.getSource().method_9211().method_3837().method_12968(), suggestionsBuilder
		);

	public static void method_13053(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("bossbar")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("id", class_2232.method_9441())
								.then(
									class_2170.method_9244("name", class_2178.method_9281())
										.executes(
											commandContext -> method_13049(
													commandContext.getSource(), class_2232.method_9443(commandContext, "id"), class_2178.method_9280(commandContext, "name")
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("remove")
						.then(
							class_2170.method_9244("id", class_2232.method_9441())
								.suggests(field_13482)
								.executes(commandContext -> method_13069(commandContext.getSource(), method_13054(commandContext)))
						)
				)
				.then(class_2170.method_9247("list").executes(commandContext -> method_13045(commandContext.getSource())))
				.then(
					class_2170.method_9247("set")
						.then(
							class_2170.method_9244("id", class_2232.method_9441())
								.suggests(field_13482)
								.then(
									class_2170.method_9247("name")
										.then(
											class_2170.method_9244("name", class_2178.method_9281())
												.executes(commandContext -> method_13071(commandContext.getSource(), method_13054(commandContext), class_2178.method_9280(commandContext, "name")))
										)
								)
								.then(
									class_2170.method_9247("color")
										.then(
											class_2170.method_9247("pink")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5788))
										)
										.then(
											class_2170.method_9247("blue")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5780))
										)
										.then(
											class_2170.method_9247("red")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5784))
										)
										.then(
											class_2170.method_9247("green")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5785))
										)
										.then(
											class_2170.method_9247("yellow")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5782))
										)
										.then(
											class_2170.method_9247("purple")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5783))
										)
										.then(
											class_2170.method_9247("white")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), class_1259.class_1260.field_5786))
										)
								)
								.then(
									class_2170.method_9247("style")
										.then(
											class_2170.method_9247("progress")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), class_1259.class_1261.field_5795))
										)
										.then(
											class_2170.method_9247("notched_6")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), class_1259.class_1261.field_5796))
										)
										.then(
											class_2170.method_9247("notched_10")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), class_1259.class_1261.field_5791))
										)
										.then(
											class_2170.method_9247("notched_12")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), class_1259.class_1261.field_5793))
										)
										.then(
											class_2170.method_9247("notched_20")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), class_1259.class_1261.field_5790))
										)
								)
								.then(
									class_2170.method_9247("value")
										.then(
											class_2170.method_9244("value", IntegerArgumentType.integer(0))
												.executes(
													commandContext -> method_13036(commandContext.getSource(), method_13054(commandContext), IntegerArgumentType.getInteger(commandContext, "value"))
												)
										)
								)
								.then(
									class_2170.method_9247("max")
										.then(
											class_2170.method_9244("max", IntegerArgumentType.integer(1))
												.executes(
													commandContext -> method_13066(commandContext.getSource(), method_13054(commandContext), IntegerArgumentType.getInteger(commandContext, "max"))
												)
										)
								)
								.then(
									class_2170.method_9247("visible")
										.then(
											class_2170.method_9244("visible", BoolArgumentType.bool())
												.executes(
													commandContext -> method_13068(commandContext.getSource(), method_13054(commandContext), BoolArgumentType.getBool(commandContext, "visible"))
												)
										)
								)
								.then(
									class_2170.method_9247("players")
										.executes(commandContext -> method_13031(commandContext.getSource(), method_13054(commandContext), Collections.emptyList()))
										.then(
											class_2170.method_9244("targets", class_2186.method_9308())
												.executes(
													commandContext -> method_13031(commandContext.getSource(), method_13054(commandContext), class_2186.method_9310(commandContext, "targets"))
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("get")
						.then(
							class_2170.method_9244("id", class_2232.method_9441())
								.suggests(field_13482)
								.then(class_2170.method_9247("value").executes(commandContext -> method_13065(commandContext.getSource(), method_13054(commandContext))))
								.then(class_2170.method_9247("max").executes(commandContext -> method_13056(commandContext.getSource(), method_13054(commandContext))))
								.then(class_2170.method_9247("visible").executes(commandContext -> method_13041(commandContext.getSource(), method_13054(commandContext))))
								.then(class_2170.method_9247("players").executes(commandContext -> method_13030(commandContext.getSource(), method_13054(commandContext))))
						)
				)
		);
	}

	private static int method_13065(class_2168 arg, class_3002 arg2) {
		arg.method_9226(new class_2588("commands.bossbar.get.value", arg2.method_12965(), arg2.method_12955()), true);
		return arg2.method_12955();
	}

	private static int method_13056(class_2168 arg, class_3002 arg2) {
		arg.method_9226(new class_2588("commands.bossbar.get.max", arg2.method_12965(), arg2.method_12960()), true);
		return arg2.method_12960();
	}

	private static int method_13041(class_2168 arg, class_3002 arg2) {
		if (arg2.method_14093()) {
			arg.method_9226(new class_2588("commands.bossbar.get.visible.visible", arg2.method_12965()), true);
			return 1;
		} else {
			arg.method_9226(new class_2588("commands.bossbar.get.visible.hidden", arg2.method_12965()), true);
			return 0;
		}
	}

	private static int method_13030(class_2168 arg, class_3002 arg2) {
		if (arg2.method_14092().isEmpty()) {
			arg.method_9226(new class_2588("commands.bossbar.get.players.none", arg2.method_12965()), true);
		} else {
			arg.method_9226(
				new class_2588(
					"commands.bossbar.get.players.some",
					arg2.method_12965(),
					arg2.method_14092().size(),
					class_2564.method_10884(arg2.method_14092(), class_1657::method_5476)
				),
				true
			);
		}

		return arg2.method_14092().size();
	}

	private static int method_13068(class_2168 arg, class_3002 arg2, boolean bl) throws CommandSyntaxException {
		if (arg2.method_14093() == bl) {
			if (bl) {
				throw field_13485.create();
			} else {
				throw field_13479.create();
			}
		} else {
			arg2.method_14091(bl);
			if (bl) {
				arg.method_9226(new class_2588("commands.bossbar.set.visible.success.visible", arg2.method_12965()), true);
			} else {
				arg.method_9226(new class_2588("commands.bossbar.set.visible.success.hidden", arg2.method_12965()), true);
			}

			return 0;
		}
	}

	private static int method_13036(class_2168 arg, class_3002 arg2, int i) throws CommandSyntaxException {
		if (arg2.method_12955() == i) {
			throw field_13477.create();
		} else {
			arg2.method_12954(i);
			arg.method_9226(new class_2588("commands.bossbar.set.value.success", arg2.method_12965(), i), true);
			return i;
		}
	}

	private static int method_13066(class_2168 arg, class_3002 arg2, int i) throws CommandSyntaxException {
		if (arg2.method_12960() == i) {
			throw field_13484.create();
		} else {
			arg2.method_12956(i);
			arg.method_9226(new class_2588("commands.bossbar.set.max.success", arg2.method_12965(), i), true);
			return i;
		}
	}

	private static int method_13028(class_2168 arg, class_3002 arg2, class_1259.class_1260 arg3) throws CommandSyntaxException {
		if (arg2.method_5420().equals(arg3)) {
			throw field_13480.create();
		} else {
			arg2.method_5416(arg3);
			arg.method_9226(new class_2588("commands.bossbar.set.color.success", arg2.method_12965()), true);
			return 0;
		}
	}

	private static int method_13050(class_2168 arg, class_3002 arg2, class_1259.class_1261 arg3) throws CommandSyntaxException {
		if (arg2.method_5415().equals(arg3)) {
			throw field_13481.create();
		} else {
			arg2.method_5409(arg3);
			arg.method_9226(new class_2588("commands.bossbar.set.style.success", arg2.method_12965()), true);
			return 0;
		}
	}

	private static int method_13071(class_2168 arg, class_3002 arg2, class_2561 arg3) throws CommandSyntaxException {
		class_2561 lv = class_2564.method_10881(arg, arg3, null);
		if (arg2.method_5414().equals(lv)) {
			throw field_13476.create();
		} else {
			arg2.method_5413(lv);
			arg.method_9226(new class_2588("commands.bossbar.set.name.success", arg2.method_12965()), true);
			return 0;
		}
	}

	private static int method_13031(class_2168 arg, class_3002 arg2, Collection<class_3222> collection) throws CommandSyntaxException {
		boolean bl = arg2.method_12962(collection);
		if (!bl) {
			throw field_13483.create();
		} else {
			if (arg2.method_14092().isEmpty()) {
				arg.method_9226(new class_2588("commands.bossbar.set.players.success.none", arg2.method_12965()), true);
			} else {
				arg.method_9226(
					new class_2588(
						"commands.bossbar.set.players.success.some", arg2.method_12965(), collection.size(), class_2564.method_10884(collection, class_1657::method_5476)
					),
					true
				);
			}

			return arg2.method_14092().size();
		}
	}

	private static int method_13045(class_2168 arg) {
		Collection<class_3002> collection = arg.method_9211().method_3837().method_12969();
		if (collection.isEmpty()) {
			arg.method_9226(new class_2588("commands.bossbar.list.bars.none"), false);
		} else {
			arg.method_9226(new class_2588("commands.bossbar.list.bars.some", collection.size(), class_2564.method_10884(collection, class_3002::method_12965)), false);
		}

		return collection.size();
	}

	private static int method_13049(class_2168 arg, class_2960 arg2, class_2561 arg3) throws CommandSyntaxException {
		class_3004 lv = arg.method_9211().method_3837();
		if (lv.method_12971(arg2) != null) {
			throw field_13478.create(arg2.toString());
		} else {
			class_3002 lv2 = lv.method_12970(arg2, class_2564.method_10881(arg, arg3, null));
			arg.method_9226(new class_2588("commands.bossbar.create.success", lv2.method_12965()), true);
			return lv.method_12969().size();
		}
	}

	private static int method_13069(class_2168 arg, class_3002 arg2) {
		class_3004 lv = arg.method_9211().method_3837();
		arg2.method_14094();
		lv.method_12973(arg2);
		arg.method_9226(new class_2588("commands.bossbar.remove.success", arg2.method_12965()), true);
		return lv.method_12969().size();
	}

	public static class_3002 method_13054(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
		class_2960 lv = class_2232.method_9443(commandContext, "id");
		class_3002 lv2 = commandContext.getSource().method_9211().method_3837().method_12971(lv);
		if (lv2 == null) {
			throw field_13486.create(lv.toString());
		} else {
			return lv2;
		}
	}
}
