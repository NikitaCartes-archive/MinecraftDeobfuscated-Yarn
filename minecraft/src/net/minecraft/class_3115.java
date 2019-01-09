package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class class_3115 {
	private static final SimpleCommandExceptionType field_13712 = new SimpleCommandExceptionType(new class_2588("commands.scoreboard.objectives.add.duplicate"));
	private static final SimpleCommandExceptionType field_13715 = new SimpleCommandExceptionType(
		new class_2588("commands.scoreboard.objectives.display.alreadyEmpty")
	);
	private static final SimpleCommandExceptionType field_13713 = new SimpleCommandExceptionType(
		new class_2588("commands.scoreboard.objectives.display.alreadySet")
	);
	private static final SimpleCommandExceptionType field_13714 = new SimpleCommandExceptionType(new class_2588("commands.scoreboard.players.enable.failed"));
	private static final SimpleCommandExceptionType field_13710 = new SimpleCommandExceptionType(new class_2588("commands.scoreboard.players.enable.invalid"));
	private static final Dynamic2CommandExceptionType field_13711 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.scoreboard.players.get.null", object, object2)
	);

	public static void method_13595(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("scoreboard")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("objectives")
						.then(class_2170.method_9247("list").executes(commandContext -> method_13597(commandContext.getSource())))
						.then(
							class_2170.method_9247("add")
								.then(
									class_2170.method_9244("objective", StringArgumentType.word())
										.then(
											class_2170.method_9244("criteria", class_2216.method_9399())
												.executes(
													commandContext -> method_13611(
															commandContext.getSource(),
															StringArgumentType.getString(commandContext, "objective"),
															class_2216.method_9402(commandContext, "criteria"),
															new class_2585(StringArgumentType.getString(commandContext, "objective"))
														)
												)
												.then(
													class_2170.method_9244("displayName", class_2178.method_9281())
														.executes(
															commandContext -> method_13611(
																	commandContext.getSource(),
																	StringArgumentType.getString(commandContext, "objective"),
																	class_2216.method_9402(commandContext, "criteria"),
																	class_2178.method_9280(commandContext, "displayName")
																)
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("modify")
								.then(
									class_2170.method_9244("objective", class_2214.method_9391())
										.then(
											class_2170.method_9247("displayname")
												.then(
													class_2170.method_9244("displayName", class_2178.method_9281())
														.executes(
															commandContext -> method_13576(
																	commandContext.getSource(), class_2214.method_9395(commandContext, "objective"), class_2178.method_9280(commandContext, "displayName")
																)
														)
												)
										)
										.then(method_13606())
								)
						)
						.then(
							class_2170.method_9247("remove")
								.then(
									class_2170.method_9244("objective", class_2214.method_9391())
										.executes(commandContext -> method_13602(commandContext.getSource(), class_2214.method_9395(commandContext, "objective")))
								)
						)
						.then(
							class_2170.method_9247("setdisplay")
								.then(
									class_2170.method_9244("slot", class_2239.method_9468())
										.executes(commandContext -> method_13592(commandContext.getSource(), class_2239.method_9465(commandContext, "slot")))
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.executes(
													commandContext -> method_13596(
															commandContext.getSource(), class_2239.method_9465(commandContext, "slot"), class_2214.method_9395(commandContext, "objective")
														)
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("players")
						.then(
							class_2170.method_9247("list")
								.executes(commandContext -> method_13589(commandContext.getSource()))
								.then(
									class_2170.method_9244("target", class_2233.method_9447())
										.suggests(class_2233.field_9951)
										.executes(commandContext -> method_13614(commandContext.getSource(), class_2233.method_9452(commandContext, "target")))
								)
						)
						.then(
							class_2170.method_9247("set")
								.then(
									class_2170.method_9244("targets", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.then(
													class_2170.method_9244("score", IntegerArgumentType.integer())
														.executes(
															commandContext -> method_13604(
																	commandContext.getSource(),
																	class_2233.method_9449(commandContext, "targets"),
																	class_2214.method_9393(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("get")
								.then(
									class_2170.method_9244("target", class_2233.method_9447())
										.suggests(class_2233.field_9951)
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.executes(
													commandContext -> method_13607(
															commandContext.getSource(), class_2233.method_9452(commandContext, "target"), class_2214.method_9395(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("add")
								.then(
									class_2170.method_9244("targets", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.then(
													class_2170.method_9244("score", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13578(
																	commandContext.getSource(),
																	class_2233.method_9449(commandContext, "targets"),
																	class_2214.method_9393(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("remove")
								.then(
									class_2170.method_9244("targets", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.then(
													class_2170.method_9244("score", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13600(
																	commandContext.getSource(),
																	class_2233.method_9449(commandContext, "targets"),
																	class_2214.method_9393(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("reset")
								.then(
									class_2170.method_9244("targets", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.executes(commandContext -> method_13575(commandContext.getSource(), class_2233.method_9449(commandContext, "targets")))
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.executes(
													commandContext -> method_13586(
															commandContext.getSource(), class_2233.method_9449(commandContext, "targets"), class_2214.method_9395(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("enable")
								.then(
									class_2170.method_9244("targets", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.then(
											class_2170.method_9244("objective", class_2214.method_9391())
												.suggests(
													(commandContext, suggestionsBuilder) -> method_13613(
															commandContext.getSource(), class_2233.method_9449(commandContext, "targets"), suggestionsBuilder
														)
												)
												.executes(
													commandContext -> method_13609(
															commandContext.getSource(), class_2233.method_9449(commandContext, "targets"), class_2214.method_9395(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("operation")
								.then(
									class_2170.method_9244("targets", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.then(
											class_2170.method_9244("targetObjective", class_2214.method_9391())
												.then(
													class_2170.method_9244("operation", class_2218.method_9404())
														.then(
															class_2170.method_9244("source", class_2233.method_9451())
																.suggests(class_2233.field_9951)
																.then(
																	class_2170.method_9244("sourceObjective", class_2214.method_9391())
																		.executes(
																			commandContext -> method_13584(
																					commandContext.getSource(),
																					class_2233.method_9449(commandContext, "targets"),
																					class_2214.method_9393(commandContext, "targetObjective"),
																					class_2218.method_9409(commandContext, "operation"),
																					class_2233.method_9449(commandContext, "source"),
																					class_2214.method_9395(commandContext, "sourceObjective")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static LiteralArgumentBuilder<class_2168> method_13606() {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("rendertype");

		for (class_274.class_275 lv : class_274.class_275.values()) {
			literalArgumentBuilder.then(
				class_2170.method_9247(lv.method_1228())
					.executes(commandContext -> method_13581(commandContext.getSource(), class_2214.method_9395(commandContext, "objective"), lv))
			);
		}

		return literalArgumentBuilder;
	}

	private static CompletableFuture<Suggestions> method_13613(class_2168 arg, Collection<String> collection, SuggestionsBuilder suggestionsBuilder) {
		List<String> list = Lists.<String>newArrayList();
		class_269 lv = arg.method_9211().method_3845();

		for (class_266 lv2 : lv.method_1151()) {
			if (lv2.method_1116() == class_274.field_1462) {
				boolean bl = false;

				for (String string : collection) {
					if (!lv.method_1183(string, lv2) || lv.method_1180(string, lv2).method_1131()) {
						bl = true;
						break;
					}
				}

				if (bl) {
					list.add(lv2.method_1113());
				}
			}
		}

		return class_2172.method_9265(list, suggestionsBuilder);
	}

	private static int method_13607(class_2168 arg, String string, class_266 arg2) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		if (!lv.method_1183(string, arg2)) {
			throw field_13711.create(arg2.method_1113(), string);
		} else {
			class_267 lv2 = lv.method_1180(string, arg2);
			arg.method_9226(new class_2588("commands.scoreboard.players.get.success", string, lv2.method_1126(), arg2.method_1120()), false);
			return lv2.method_1126();
		}
	}

	private static int method_13584(
		class_2168 arg, Collection<String> collection, class_266 arg2, class_2218.class_2219 arg3, Collection<String> collection2, class_266 arg4
	) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		int i = 0;

		for (String string : collection) {
			class_267 lv2 = lv.method_1180(string, arg2);

			for (String string2 : collection2) {
				class_267 lv3 = lv.method_1180(string2, arg4);
				arg3.apply(lv2, lv3);
			}

			i += lv2.method_1126();
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.scoreboard.players.operation.success.single", arg2.method_1120(), collection.iterator().next(), i), true);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.operation.success.multiple", arg2.method_1120(), collection.size()), true);
		}

		return i;
	}

	private static int method_13609(class_2168 arg, Collection<String> collection, class_266 arg2) throws CommandSyntaxException {
		if (arg2.method_1116() != class_274.field_1462) {
			throw field_13710.create();
		} else {
			class_269 lv = arg.method_9211().method_3845();
			int i = 0;

			for (String string : collection) {
				class_267 lv2 = lv.method_1180(string, arg2);
				if (lv2.method_1131()) {
					lv2.method_1125(false);
					i++;
				}
			}

			if (i == 0) {
				throw field_13714.create();
			} else {
				if (collection.size() == 1) {
					arg.method_9226(new class_2588("commands.scoreboard.players.enable.success.single", arg2.method_1120(), collection.iterator().next()), true);
				} else {
					arg.method_9226(new class_2588("commands.scoreboard.players.enable.success.multiple", arg2.method_1120(), collection.size()), true);
				}

				return i;
			}
		}
	}

	private static int method_13575(class_2168 arg, Collection<String> collection) {
		class_269 lv = arg.method_9211().method_3845();

		for (String string : collection) {
			lv.method_1155(string, null);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.scoreboard.players.reset.all.single", collection.iterator().next()), true);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.reset.all.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13586(class_2168 arg, Collection<String> collection, class_266 arg2) {
		class_269 lv = arg.method_9211().method_3845();

		for (String string : collection) {
			lv.method_1155(string, arg2);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.scoreboard.players.reset.specific.single", arg2.method_1120(), collection.iterator().next()), true);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.reset.specific.multiple", arg2.method_1120(), collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13604(class_2168 arg, Collection<String> collection, class_266 arg2, int i) {
		class_269 lv = arg.method_9211().method_3845();

		for (String string : collection) {
			class_267 lv2 = lv.method_1180(string, arg2);
			lv2.method_1128(i);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.scoreboard.players.set.success.single", arg2.method_1120(), collection.iterator().next(), i), true);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.set.success.multiple", arg2.method_1120(), collection.size(), i), true);
		}

		return i * collection.size();
	}

	private static int method_13578(class_2168 arg, Collection<String> collection, class_266 arg2, int i) {
		class_269 lv = arg.method_9211().method_3845();
		int j = 0;

		for (String string : collection) {
			class_267 lv2 = lv.method_1180(string, arg2);
			lv2.method_1128(lv2.method_1126() + i);
			j += lv2.method_1126();
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.scoreboard.players.add.success.single", i, arg2.method_1120(), collection.iterator().next(), j), true);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.add.success.multiple", i, arg2.method_1120(), collection.size()), true);
		}

		return j;
	}

	private static int method_13600(class_2168 arg, Collection<String> collection, class_266 arg2, int i) {
		class_269 lv = arg.method_9211().method_3845();
		int j = 0;

		for (String string : collection) {
			class_267 lv2 = lv.method_1180(string, arg2);
			lv2.method_1128(lv2.method_1126() - i);
			j += lv2.method_1126();
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.scoreboard.players.remove.success.single", i, arg2.method_1120(), collection.iterator().next(), j), true);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.remove.success.multiple", i, arg2.method_1120(), collection.size()), true);
		}

		return j;
	}

	private static int method_13589(class_2168 arg) {
		Collection<String> collection = arg.method_9211().method_3845().method_1178();
		if (collection.isEmpty()) {
			arg.method_9226(new class_2588("commands.scoreboard.players.list.empty"), false);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.list.success", collection.size(), class_2564.method_10888(collection)), false);
		}

		return collection.size();
	}

	private static int method_13614(class_2168 arg, String string) {
		Map<class_266, class_267> map = arg.method_9211().method_3845().method_1166(string);
		if (map.isEmpty()) {
			arg.method_9226(new class_2588("commands.scoreboard.players.list.entity.empty", string), false);
		} else {
			arg.method_9226(new class_2588("commands.scoreboard.players.list.entity.success", string, map.size()), false);

			for (Entry<class_266, class_267> entry : map.entrySet()) {
				arg.method_9226(
					new class_2588("commands.scoreboard.players.list.entity.entry", ((class_266)entry.getKey()).method_1120(), ((class_267)entry.getValue()).method_1126()),
					false
				);
			}
		}

		return map.size();
	}

	private static int method_13592(class_2168 arg, int i) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		if (lv.method_1189(i) == null) {
			throw field_13715.create();
		} else {
			lv.method_1158(i, null);
			arg.method_9226(new class_2588("commands.scoreboard.objectives.display.cleared", class_269.method_1186()[i]), true);
			return 0;
		}
	}

	private static int method_13596(class_2168 arg, int i, class_266 arg2) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		if (lv.method_1189(i) == arg2) {
			throw field_13713.create();
		} else {
			lv.method_1158(i, arg2);
			arg.method_9226(new class_2588("commands.scoreboard.objectives.display.set", class_269.method_1186()[i], arg2.method_1114()), true);
			return 0;
		}
	}

	private static int method_13576(class_2168 arg, class_266 arg2, class_2561 arg3) {
		if (!arg2.method_1114().equals(arg3)) {
			arg2.method_1121(arg3);
			arg.method_9226(new class_2588("commands.scoreboard.objectives.modify.displayname", arg2.method_1113(), arg2.method_1120()), true);
		}

		return 0;
	}

	private static int method_13581(class_2168 arg, class_266 arg2, class_274.class_275 arg3) {
		if (arg2.method_1118() != arg3) {
			arg2.method_1115(arg3);
			arg.method_9226(new class_2588("commands.scoreboard.objectives.modify.rendertype", arg2.method_1120()), true);
		}

		return 0;
	}

	private static int method_13602(class_2168 arg, class_266 arg2) {
		class_269 lv = arg.method_9211().method_3845();
		lv.method_1194(arg2);
		arg.method_9226(new class_2588("commands.scoreboard.objectives.remove.success", arg2.method_1120()), true);
		return lv.method_1151().size();
	}

	private static int method_13611(class_2168 arg, String string, class_274 arg2, class_2561 arg3) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		if (lv.method_1170(string) != null) {
			throw field_13712.create();
		} else if (string.length() > 16) {
			throw class_2214.field_9920.create(16);
		} else {
			lv.method_1168(string, arg2, arg3, arg2.method_1227());
			class_266 lv2 = lv.method_1170(string);
			arg.method_9226(new class_2588("commands.scoreboard.objectives.add.success", lv2.method_1120()), true);
			return lv.method_1151().size();
		}
	}

	private static int method_13597(class_2168 arg) {
		Collection<class_266> collection = arg.method_9211().method_3845().method_1151();
		if (collection.isEmpty()) {
			arg.method_9226(new class_2588("commands.scoreboard.objectives.list.empty"), false);
		} else {
			arg.method_9226(
				new class_2588("commands.scoreboard.objectives.list.success", collection.size(), class_2564.method_10884(collection, class_266::method_1120)), false
			);
		}

		return collection.size();
	}
}
