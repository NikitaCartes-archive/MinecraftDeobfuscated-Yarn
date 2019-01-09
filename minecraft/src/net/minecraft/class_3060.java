package net.minecraft;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;

public class class_3060 {
	private static final Dynamic2CommandExceptionType field_13657 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.forceload.toobig", object, object2)
	);
	private static final Dynamic2CommandExceptionType field_13659 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.forceload.query.failure", object, object2)
	);
	private static final SimpleCommandExceptionType field_13658 = new SimpleCommandExceptionType(new class_2588("commands.forceload.added.failure"));
	private static final SimpleCommandExceptionType field_13660 = new SimpleCommandExceptionType(new class_2588("commands.forceload.removed.failure"));

	public static void method_13365(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("forceload")
				.requires(arg -> arg.method_9259(4))
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("from", class_2264.method_9701())
								.executes(
									commandContext -> method_13372(
											commandContext.getSource(), class_2264.method_9702(commandContext, "from"), class_2264.method_9702(commandContext, "from"), true
										)
								)
								.then(
									class_2170.method_9244("to", class_2264.method_9701())
										.executes(
											commandContext -> method_13372(
													commandContext.getSource(), class_2264.method_9702(commandContext, "from"), class_2264.method_9702(commandContext, "to"), true
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("remove")
						.then(
							class_2170.method_9244("from", class_2264.method_9701())
								.executes(
									commandContext -> method_13372(
											commandContext.getSource(), class_2264.method_9702(commandContext, "from"), class_2264.method_9702(commandContext, "from"), false
										)
								)
								.then(
									class_2170.method_9244("to", class_2264.method_9701())
										.executes(
											commandContext -> method_13372(
													commandContext.getSource(), class_2264.method_9702(commandContext, "from"), class_2264.method_9702(commandContext, "to"), false
												)
										)
								)
						)
						.then(class_2170.method_9247("all").executes(commandContext -> method_13366(commandContext.getSource())))
				)
				.then(
					class_2170.method_9247("query")
						.executes(commandContext -> method_13373(commandContext.getSource()))
						.then(
							class_2170.method_9244("pos", class_2264.method_9701())
								.executes(commandContext -> method_13374(commandContext.getSource(), class_2264.method_9702(commandContext, "pos")))
						)
				)
		);
	}

	private static int method_13374(class_2168 arg, class_2264.class_2265 arg2) throws CommandSyntaxException {
		class_1923 lv = new class_1923(arg2.field_10708 >> 4, arg2.field_10707 >> 4);
		class_2874 lv2 = arg.method_9225().method_8597().method_12460();
		boolean bl = arg.method_9211().method_3847(lv2).method_8559(lv.field_9181, lv.field_9180);
		if (bl) {
			arg.method_9226(new class_2588("commands.forceload.query.success", lv, lv2), false);
			return 1;
		} else {
			throw field_13659.create(lv, lv2);
		}
	}

	private static int method_13373(class_2168 arg) {
		class_2874 lv = arg.method_9225().method_8597().method_12460();
		LongSet longSet = arg.method_9211().method_3847(lv).method_8440();
		int i = longSet.size();
		if (i > 0) {
			String string = Joiner.on(", ").join(longSet.stream().sorted().map(class_1923::new).map(class_1923::toString).iterator());
			if (i == 1) {
				arg.method_9226(new class_2588("commands.forceload.list.single", lv, string), false);
			} else {
				arg.method_9226(new class_2588("commands.forceload.list.multiple", i, lv, string), false);
			}
		} else {
			arg.method_9213(new class_2588("commands.forceload.added.none", lv));
		}

		return i;
	}

	private static int method_13366(class_2168 arg) {
		class_2874 lv = arg.method_9225().method_8597().method_12460();
		class_3218 lv2 = arg.method_9211().method_3847(lv);
		LongSet longSet = lv2.method_8440();
		longSet.forEach(l -> lv2.method_8461(class_1923.method_8325(l), class_1923.method_8332(l), false));
		arg.method_9226(new class_2588("commands.forceload.removed.all", lv), true);
		return 0;
	}

	private static int method_13372(class_2168 arg, class_2264.class_2265 arg2, class_2264.class_2265 arg3, boolean bl) throws CommandSyntaxException {
		int i = Math.min(arg2.field_10708, arg3.field_10708);
		int j = Math.min(arg2.field_10707, arg3.field_10707);
		int k = Math.max(arg2.field_10708, arg3.field_10708);
		int l = Math.max(arg2.field_10707, arg3.field_10707);
		if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
			int m = i >> 4;
			int n = j >> 4;
			int o = k >> 4;
			int p = l >> 4;
			long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
			if (q > 256L) {
				throw field_13657.create(256, q);
			} else {
				class_2874 lv = arg.method_9225().method_8597().method_12460();
				class_3218 lv2 = arg.method_9211().method_3847(lv);
				class_1923 lv3 = null;
				int r = 0;

				for (int s = m; s <= o; s++) {
					for (int t = n; t <= p; t++) {
						boolean bl2 = lv2.method_8461(s, t, bl);
						if (bl2) {
							r++;
							if (lv3 == null) {
								lv3 = new class_1923(s, t);
							}
						}
					}
				}

				if (r == 0) {
					throw (bl ? field_13658 : field_13660).create();
				} else {
					if (r == 1) {
						arg.method_9226(new class_2588("commands.forceload." + (bl ? "added" : "removed") + ".single", lv3, lv), true);
					} else {
						class_1923 lv4 = new class_1923(m, n);
						class_1923 lv5 = new class_1923(o, p);
						arg.method_9226(new class_2588("commands.forceload." + (bl ? "added" : "removed") + ".multiple", r, lv, lv4, lv5), true);
					}

					return r;
				}
			}
		} else {
			throw class_2262.field_10704.create();
		}
	}
}
