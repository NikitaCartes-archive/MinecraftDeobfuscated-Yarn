package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;

public class class_3142 {
	private static final SimpleCommandExceptionType field_13749 = new SimpleCommandExceptionType(new class_2588("commands.team.add.duplicate"));
	private static final DynamicCommandExceptionType field_13748 = new DynamicCommandExceptionType(object -> new class_2588("commands.team.add.longName", object));
	private static final SimpleCommandExceptionType field_13751 = new SimpleCommandExceptionType(new class_2588("commands.team.empty.unchanged"));
	private static final SimpleCommandExceptionType field_13755 = new SimpleCommandExceptionType(new class_2588("commands.team.option.name.unchanged"));
	private static final SimpleCommandExceptionType field_13746 = new SimpleCommandExceptionType(new class_2588("commands.team.option.color.unchanged"));
	private static final SimpleCommandExceptionType field_13753 = new SimpleCommandExceptionType(
		new class_2588("commands.team.option.friendlyfire.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType field_13754 = new SimpleCommandExceptionType(
		new class_2588("commands.team.option.friendlyfire.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType field_13747 = new SimpleCommandExceptionType(
		new class_2588("commands.team.option.seeFriendlyInvisibles.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType field_13756 = new SimpleCommandExceptionType(
		new class_2588("commands.team.option.seeFriendlyInvisibles.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType field_13752 = new SimpleCommandExceptionType(
		new class_2588("commands.team.option.nametagVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType field_13757 = new SimpleCommandExceptionType(
		new class_2588("commands.team.option.deathMessageVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType field_13750 = new SimpleCommandExceptionType(new class_2588("commands.team.option.collisionRule.unchanged"));

	public static void method_13736(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("team")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("list")
						.executes(commandContext -> method_13728(commandContext.getSource()))
						.then(
							class_2170.method_9244("team", class_2243.method_9482())
								.executes(commandContext -> method_13748(commandContext.getSource(), class_2243.method_9480(commandContext, "team")))
						)
				)
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("team", StringArgumentType.word())
								.executes(commandContext -> method_13757(commandContext.getSource(), StringArgumentType.getString(commandContext, "team")))
								.then(
									class_2170.method_9244("displayName", class_2178.method_9281())
										.executes(
											commandContext -> method_13715(
													commandContext.getSource(), StringArgumentType.getString(commandContext, "team"), class_2178.method_9280(commandContext, "displayName")
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("remove")
						.then(
							class_2170.method_9244("team", class_2243.method_9482())
								.executes(commandContext -> method_13747(commandContext.getSource(), class_2243.method_9480(commandContext, "team")))
						)
				)
				.then(
					class_2170.method_9247("empty")
						.then(
							class_2170.method_9244("team", class_2243.method_9482())
								.executes(commandContext -> method_13723(commandContext.getSource(), class_2243.method_9480(commandContext, "team")))
						)
				)
				.then(
					class_2170.method_9247("join")
						.then(
							class_2170.method_9244("team", class_2243.method_9482())
								.executes(
									commandContext -> method_13720(
											commandContext.getSource(),
											class_2243.method_9480(commandContext, "team"),
											Collections.singleton(commandContext.getSource().method_9229().method_5820())
										)
								)
								.then(
									class_2170.method_9244("members", class_2233.method_9451())
										.suggests(class_2233.field_9951)
										.executes(
											commandContext -> method_13720(
													commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_2233.method_9449(commandContext, "members")
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("leave")
						.then(
							class_2170.method_9244("members", class_2233.method_9451())
								.suggests(class_2233.field_9951)
								.executes(commandContext -> method_13714(commandContext.getSource(), class_2233.method_9449(commandContext, "members")))
						)
				)
				.then(
					class_2170.method_9247("modify")
						.then(
							class_2170.method_9244("team", class_2243.method_9482())
								.then(
									class_2170.method_9247("displayName")
										.then(
											class_2170.method_9244("displayName", class_2178.method_9281())
												.executes(
													commandContext -> method_13711(
															commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_2178.method_9280(commandContext, "displayName")
														)
												)
										)
								)
								.then(
									class_2170.method_9247("color")
										.then(
											class_2170.method_9244("value", class_2177.method_9276())
												.executes(
													commandContext -> method_13745(
															commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_2177.method_9277(commandContext, "value")
														)
												)
										)
								)
								.then(
									class_2170.method_9247("friendlyFire")
										.then(
											class_2170.method_9244("allowed", BoolArgumentType.bool())
												.executes(
													commandContext -> method_13754(
															commandContext.getSource(), class_2243.method_9480(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")
														)
												)
										)
								)
								.then(
									class_2170.method_9247("seeFriendlyInvisibles")
										.then(
											class_2170.method_9244("allowed", BoolArgumentType.bool())
												.executes(
													commandContext -> method_13751(
															commandContext.getSource(), class_2243.method_9480(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")
														)
												)
										)
								)
								.then(
									class_2170.method_9247("nametagVisibility")
										.then(
											class_2170.method_9247("never")
												.executes(
													commandContext -> method_13732(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1443)
												)
										)
										.then(
											class_2170.method_9247("hideForOtherTeams")
												.executes(
													commandContext -> method_13732(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1444)
												)
										)
										.then(
											class_2170.method_9247("hideForOwnTeam")
												.executes(
													commandContext -> method_13732(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1446)
												)
										)
										.then(
											class_2170.method_9247("always")
												.executes(
													commandContext -> method_13732(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1442)
												)
										)
								)
								.then(
									class_2170.method_9247("deathMessageVisibility")
										.then(
											class_2170.method_9247("never")
												.executes(
													commandContext -> method_13735(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1443)
												)
										)
										.then(
											class_2170.method_9247("hideForOtherTeams")
												.executes(
													commandContext -> method_13735(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1444)
												)
										)
										.then(
											class_2170.method_9247("hideForOwnTeam")
												.executes(
													commandContext -> method_13735(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1446)
												)
										)
										.then(
											class_2170.method_9247("always")
												.executes(
													commandContext -> method_13735(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_272.field_1442)
												)
										)
								)
								.then(
									class_2170.method_9247("collisionRule")
										.then(
											class_2170.method_9247("never")
												.executes(
													commandContext -> method_13713(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_271.field_1435)
												)
										)
										.then(
											class_2170.method_9247("pushOwnTeam")
												.executes(
													commandContext -> method_13713(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_271.field_1440)
												)
										)
										.then(
											class_2170.method_9247("pushOtherTeams")
												.executes(
													commandContext -> method_13713(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_271.field_1434)
												)
										)
										.then(
											class_2170.method_9247("always")
												.executes(
													commandContext -> method_13713(commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_270.class_271.field_1437)
												)
										)
								)
								.then(
									class_2170.method_9247("prefix")
										.then(
											class_2170.method_9244("prefix", class_2178.method_9281())
												.executes(
													commandContext -> method_13743(
															commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_2178.method_9280(commandContext, "prefix")
														)
												)
										)
								)
								.then(
									class_2170.method_9247("suffix")
										.then(
											class_2170.method_9244("suffix", class_2178.method_9281())
												.executes(
													commandContext -> method_13756(
															commandContext.getSource(), class_2243.method_9480(commandContext, "team"), class_2178.method_9280(commandContext, "suffix")
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13714(class_2168 arg, Collection<String> collection) {
		class_269 lv = arg.method_9211().method_3845();

		for (String string : collection) {
			lv.method_1195(string);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.team.leave.success.single", collection.iterator().next()), true);
		} else {
			arg.method_9226(new class_2588("commands.team.leave.success.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13720(class_2168 arg, class_268 arg2, Collection<String> collection) {
		class_269 lv = arg.method_9211().method_3845();

		for (String string : collection) {
			lv.method_1172(string, arg2);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.team.join.success.single", collection.iterator().next(), arg2.method_1148()), true);
		} else {
			arg.method_9226(new class_2588("commands.team.join.success.multiple", collection.size(), arg2.method_1148()), true);
		}

		return collection.size();
	}

	private static int method_13732(class_2168 arg, class_268 arg2, class_270.class_272 arg3) throws CommandSyntaxException {
		if (arg2.method_1201() == arg3) {
			throw field_13752.create();
		} else {
			arg2.method_1149(arg3);
			arg.method_9226(new class_2588("commands.team.option.nametagVisibility.success", arg2.method_1148(), arg3.method_1214()), true);
			return 0;
		}
	}

	private static int method_13735(class_2168 arg, class_268 arg2, class_270.class_272 arg3) throws CommandSyntaxException {
		if (arg2.method_1200() == arg3) {
			throw field_13757.create();
		} else {
			arg2.method_1133(arg3);
			arg.method_9226(new class_2588("commands.team.option.deathMessageVisibility.success", arg2.method_1148(), arg3.method_1214()), true);
			return 0;
		}
	}

	private static int method_13713(class_2168 arg, class_268 arg2, class_270.class_271 arg3) throws CommandSyntaxException {
		if (arg2.method_1203() == arg3) {
			throw field_13750.create();
		} else {
			arg2.method_1145(arg3);
			arg.method_9226(new class_2588("commands.team.option.collisionRule.success", arg2.method_1148(), arg3.method_1209()), true);
			return 0;
		}
	}

	private static int method_13751(class_2168 arg, class_268 arg2, boolean bl) throws CommandSyntaxException {
		if (arg2.method_1199() == bl) {
			if (bl) {
				throw field_13747.create();
			} else {
				throw field_13756.create();
			}
		} else {
			arg2.method_1143(bl);
			arg.method_9226(new class_2588("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), arg2.method_1148()), true);
			return 0;
		}
	}

	private static int method_13754(class_2168 arg, class_268 arg2, boolean bl) throws CommandSyntaxException {
		if (arg2.method_1205() == bl) {
			if (bl) {
				throw field_13753.create();
			} else {
				throw field_13754.create();
			}
		} else {
			arg2.method_1135(bl);
			arg.method_9226(new class_2588("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), arg2.method_1148()), true);
			return 0;
		}
	}

	private static int method_13711(class_2168 arg, class_268 arg2, class_2561 arg3) throws CommandSyntaxException {
		if (arg2.method_1140().equals(arg3)) {
			throw field_13755.create();
		} else {
			arg2.method_1137(arg3);
			arg.method_9226(new class_2588("commands.team.option.name.success", arg2.method_1148()), true);
			return 0;
		}
	}

	private static int method_13745(class_2168 arg, class_268 arg2, class_124 arg3) throws CommandSyntaxException {
		if (arg2.method_1202() == arg3) {
			throw field_13746.create();
		} else {
			arg2.method_1141(arg3);
			arg.method_9226(new class_2588("commands.team.option.color.success", arg2.method_1148(), arg3.method_537()), true);
			return 0;
		}
	}

	private static int method_13723(class_2168 arg, class_268 arg2) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		Collection<String> collection = Lists.<String>newArrayList(arg2.method_1204());
		if (collection.isEmpty()) {
			throw field_13751.create();
		} else {
			for (String string : collection) {
				lv.method_1157(string, arg2);
			}

			arg.method_9226(new class_2588("commands.team.empty.success", collection.size(), arg2.method_1148()), true);
			return collection.size();
		}
	}

	private static int method_13747(class_2168 arg, class_268 arg2) {
		class_269 lv = arg.method_9211().method_3845();
		lv.method_1191(arg2);
		arg.method_9226(new class_2588("commands.team.remove.success", arg2.method_1148()), true);
		return lv.method_1159().size();
	}

	private static int method_13757(class_2168 arg, String string) throws CommandSyntaxException {
		return method_13715(arg, string, new class_2585(string));
	}

	private static int method_13715(class_2168 arg, String string, class_2561 arg2) throws CommandSyntaxException {
		class_269 lv = arg.method_9211().method_3845();
		if (lv.method_1153(string) != null) {
			throw field_13749.create();
		} else if (string.length() > 16) {
			throw field_13748.create(16);
		} else {
			class_268 lv2 = lv.method_1171(string);
			lv2.method_1137(arg2);
			arg.method_9226(new class_2588("commands.team.add.success", lv2.method_1148()), true);
			return lv.method_1159().size();
		}
	}

	private static int method_13748(class_2168 arg, class_268 arg2) {
		Collection<String> collection = arg2.method_1204();
		if (collection.isEmpty()) {
			arg.method_9226(new class_2588("commands.team.list.members.empty", arg2.method_1148()), false);
		} else {
			arg.method_9226(new class_2588("commands.team.list.members.success", arg2.method_1148(), collection.size(), class_2564.method_10888(collection)), false);
		}

		return collection.size();
	}

	private static int method_13728(class_2168 arg) {
		Collection<class_268> collection = arg.method_9211().method_3845().method_1159();
		if (collection.isEmpty()) {
			arg.method_9226(new class_2588("commands.team.list.teams.empty"), false);
		} else {
			arg.method_9226(new class_2588("commands.team.list.teams.success", collection.size(), class_2564.method_10884(collection, class_268::method_1148)), false);
		}

		return collection.size();
	}

	private static int method_13743(class_2168 arg, class_268 arg2, class_2561 arg3) {
		arg2.method_1138(arg3);
		arg.method_9226(new class_2588("commands.team.option.prefix.success", arg3), false);
		return 1;
	}

	private static int method_13756(class_2168 arg, class_268 arg2, class_2561 arg3) {
		arg2.method_1139(arg3);
		arg.method_9226(new class_2588("commands.team.option.suffix.success", arg3), false);
		return 1;
	}
}
