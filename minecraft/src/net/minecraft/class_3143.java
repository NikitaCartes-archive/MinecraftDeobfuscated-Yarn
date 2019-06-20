package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;

public class class_3143 {
	public static void method_13760(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralCommandNode<class_2168> literalCommandNode = commandDispatcher.register(
			class_2170.method_9247("teleport")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9306())
						.then(
							class_2170.method_9244("location", class_2277.method_9737())
								.executes(
									commandContext -> method_13765(
											commandContext.getSource(),
											class_2186.method_9317(commandContext, "targets"),
											commandContext.getSource().method_9225(),
											class_2277.method_9734(commandContext, "location"),
											null,
											null
										)
								)
								.then(
									class_2170.method_9244("rotation", class_2270.method_9717())
										.executes(
											commandContext -> method_13765(
													commandContext.getSource(),
													class_2186.method_9317(commandContext, "targets"),
													commandContext.getSource().method_9225(),
													class_2277.method_9734(commandContext, "location"),
													class_2270.method_9716(commandContext, "rotation"),
													null
												)
										)
								)
								.then(
									class_2170.method_9247("facing")
										.then(
											class_2170.method_9247("entity")
												.then(
													class_2170.method_9244("facingEntity", class_2186.method_9309())
														.executes(
															commandContext -> method_13765(
																	commandContext.getSource(),
																	class_2186.method_9317(commandContext, "targets"),
																	commandContext.getSource().method_9225(),
																	class_2277.method_9734(commandContext, "location"),
																	null,
																	new class_3143.class_3144(class_2186.method_9313(commandContext, "facingEntity"), class_2183.class_2184.field_9853)
																)
														)
														.then(
															class_2170.method_9244("facingAnchor", class_2183.method_9295())
																.executes(
																	commandContext -> method_13765(
																			commandContext.getSource(),
																			class_2186.method_9317(commandContext, "targets"),
																			commandContext.getSource().method_9225(),
																			class_2277.method_9734(commandContext, "location"),
																			null,
																			new class_3143.class_3144(class_2186.method_9313(commandContext, "facingEntity"), class_2183.method_9294(commandContext, "facingAnchor"))
																		)
																)
														)
												)
										)
										.then(
											class_2170.method_9244("facingLocation", class_2277.method_9737())
												.executes(
													commandContext -> method_13765(
															commandContext.getSource(),
															class_2186.method_9317(commandContext, "targets"),
															commandContext.getSource().method_9225(),
															class_2277.method_9734(commandContext, "location"),
															null,
															new class_3143.class_3144(class_2277.method_9736(commandContext, "facingLocation"))
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9244("destination", class_2186.method_9309())
								.executes(
									commandContext -> method_13771(
											commandContext.getSource(), class_2186.method_9317(commandContext, "targets"), class_2186.method_9313(commandContext, "destination")
										)
								)
						)
				)
				.then(
					class_2170.method_9244("location", class_2277.method_9737())
						.executes(
							commandContext -> method_13765(
									commandContext.getSource(),
									Collections.singleton(commandContext.getSource().method_9229()),
									commandContext.getSource().method_9225(),
									class_2277.method_9734(commandContext, "location"),
									class_2280.method_9751(),
									null
								)
						)
				)
				.then(
					class_2170.method_9244("destination", class_2186.method_9309())
						.executes(
							commandContext -> method_13771(
									commandContext.getSource(), Collections.singleton(commandContext.getSource().method_9229()), class_2186.method_9313(commandContext, "destination")
								)
						)
				)
		);
		commandDispatcher.register(class_2170.method_9247("tp").requires(arg -> arg.method_9259(2)).redirect(literalCommandNode));
	}

	private static int method_13771(class_2168 arg, Collection<? extends class_1297> collection, class_1297 arg2) {
		for (class_1297 lv : collection) {
			method_13766(
				arg,
				lv,
				(class_3218)arg2.field_6002,
				arg2.field_5987,
				arg2.field_6010,
				arg2.field_6035,
				EnumSet.noneOf(class_2708.class_2709.class),
				arg2.field_6031,
				arg2.field_5965,
				null
			);
		}

		if (collection.size() == 1) {
			arg.method_9226(
				new class_2588("commands.teleport.success.entity.single", ((class_1297)collection.iterator().next()).method_5476(), arg2.method_5476()), true
			);
		} else {
			arg.method_9226(new class_2588("commands.teleport.success.entity.multiple", collection.size(), arg2.method_5476()), true);
		}

		return collection.size();
	}

	private static int method_13765(
		class_2168 arg,
		Collection<? extends class_1297> collection,
		class_3218 arg2,
		class_2267 arg3,
		@Nullable class_2267 arg4,
		@Nullable class_3143.class_3144 arg5
	) throws CommandSyntaxException {
		class_243 lv = arg3.method_9708(arg);
		class_241 lv2 = arg4 == null ? null : arg4.method_9709(arg);
		Set<class_2708.class_2709> set = EnumSet.noneOf(class_2708.class_2709.class);
		if (arg3.method_9705()) {
			set.add(class_2708.class_2709.field_12400);
		}

		if (arg3.method_9706()) {
			set.add(class_2708.class_2709.field_12398);
		}

		if (arg3.method_9707()) {
			set.add(class_2708.class_2709.field_12403);
		}

		if (arg4 == null) {
			set.add(class_2708.class_2709.field_12397);
			set.add(class_2708.class_2709.field_12401);
		} else {
			if (arg4.method_9705()) {
				set.add(class_2708.class_2709.field_12397);
			}

			if (arg4.method_9706()) {
				set.add(class_2708.class_2709.field_12401);
			}
		}

		for (class_1297 lv3 : collection) {
			if (arg4 == null) {
				method_13766(arg, lv3, arg2, lv.field_1352, lv.field_1351, lv.field_1350, set, lv3.field_6031, lv3.field_5965, arg5);
			} else {
				method_13766(arg, lv3, arg2, lv.field_1352, lv.field_1351, lv.field_1350, set, lv2.field_1342, lv2.field_1343, arg5);
			}
		}

		if (collection.size() == 1) {
			arg.method_9226(
				new class_2588(
					"commands.teleport.success.location.single", ((class_1297)collection.iterator().next()).method_5476(), lv.field_1352, lv.field_1351, lv.field_1350
				),
				true
			);
		} else {
			arg.method_9226(new class_2588("commands.teleport.success.location.multiple", collection.size(), lv.field_1352, lv.field_1351, lv.field_1350), true);
		}

		return collection.size();
	}

	private static void method_13766(
		class_2168 arg,
		class_1297 arg2,
		class_3218 arg3,
		double d,
		double e,
		double f,
		Set<class_2708.class_2709> set,
		float g,
		float h,
		@Nullable class_3143.class_3144 arg4
	) {
		if (arg2 instanceof class_3222) {
			class_1923 lv = new class_1923(new class_2338(d, e, f));
			arg3.method_14178().method_17297(class_3230.field_19347, lv, 1, arg2.method_5628());
			arg2.method_5848();
			if (((class_3222)arg2).method_6113()) {
				((class_3222)arg2).method_7358(true, true, false);
			}

			if (arg3 == arg2.field_6002) {
				((class_3222)arg2).field_13987.method_14360(d, e, f, g, h, set);
			} else {
				((class_3222)arg2).method_14251(arg3, d, e, f, g, h);
			}

			arg2.method_5847(g);
		} else {
			float i = class_3532.method_15393(g);
			float j = class_3532.method_15393(h);
			j = class_3532.method_15363(j, -90.0F, 90.0F);
			if (arg3 == arg2.field_6002) {
				arg2.method_5808(d, e, f, i, j);
				arg2.method_5847(i);
			} else {
				arg2.method_18375();
				arg2.field_6026 = arg3.field_9247.method_12460();
				class_1297 lv2 = arg2;
				arg2 = arg2.method_5864().method_5883(arg3);
				if (arg2 == null) {
					return;
				}

				arg2.method_5878(lv2);
				arg2.method_5808(d, e, f, i, j);
				arg2.method_5847(i);
				arg3.method_18769(arg2);
				lv2.field_5988 = true;
			}
		}

		if (arg4 != null) {
			arg4.method_13772(arg, arg2);
		}

		if (!(arg2 instanceof class_1309) || !((class_1309)arg2).method_6128()) {
			arg2.method_18799(arg2.method_18798().method_18805(1.0, 0.0, 1.0));
			arg2.field_5952 = true;
		}
	}

	static class class_3144 {
		private final class_243 field_13760;
		private final class_1297 field_13758;
		private final class_2183.class_2184 field_13759;

		public class_3144(class_1297 arg, class_2183.class_2184 arg2) {
			this.field_13758 = arg;
			this.field_13759 = arg2;
			this.field_13760 = arg2.method_9302(arg);
		}

		public class_3144(class_243 arg) {
			this.field_13758 = null;
			this.field_13760 = arg;
			this.field_13759 = null;
		}

		public void method_13772(class_2168 arg, class_1297 arg2) {
			if (this.field_13758 != null) {
				if (arg2 instanceof class_3222) {
					((class_3222)arg2).method_14222(arg.method_9219(), this.field_13758, this.field_13759);
				} else {
					arg2.method_5702(arg.method_9219(), this.field_13760);
				}
			} else {
				arg2.method_5702(arg.method_9219(), this.field_13760);
			}
		}
	}
}
