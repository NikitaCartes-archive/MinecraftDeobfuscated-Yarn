package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class class_3039 {
	public static final SuggestionProvider<class_2168> field_13605 = (commandContext, suggestionsBuilder) -> {
		class_60 lv = commandContext.getSource().method_9211().method_3857();
		return class_2172.method_9270(lv.method_370(), suggestionsBuilder);
	};
	private static final DynamicCommandExceptionType field_13604 = new DynamicCommandExceptionType(object -> new class_2588("commands.drop.no_held_items", object));
	private static final DynamicCommandExceptionType field_13606 = new DynamicCommandExceptionType(object -> new class_2588("commands.drop.no_loot_table", object));

	public static void method_13193(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			method_13206(
				class_2170.method_9247("loot").requires(arg -> arg.method_9259(2)),
				(argumentBuilder, arg) -> argumentBuilder.then(
							class_2170.method_9247("fish")
								.then(
									class_2170.method_9244("loot_table", class_2232.method_9441())
										.suggests(field_13605)
										.then(
											class_2170.method_9244("pos", class_2262.method_9698())
												.executes(
													commandContext -> method_13199(
															commandContext, class_2232.method_9443(commandContext, "loot_table"), class_2262.method_9696(commandContext, "pos"), class_1799.field_8037, arg
														)
												)
												.then(
													class_2170.method_9244("tool", class_2287.method_9776())
														.executes(
															commandContext -> method_13199(
																	commandContext,
																	class_2232.method_9443(commandContext, "loot_table"),
																	class_2262.method_9696(commandContext, "pos"),
																	class_2287.method_9777(commandContext, "tool").method_9781(1, false),
																	arg
																)
														)
												)
												.then(
													class_2170.method_9247("mainhand")
														.executes(
															commandContext -> method_13199(
																	commandContext,
																	class_2232.method_9443(commandContext, "loot_table"),
																	class_2262.method_9696(commandContext, "pos"),
																	method_13178(commandContext.getSource(), class_1304.field_6173),
																	arg
																)
														)
												)
												.then(
													class_2170.method_9247("offhand")
														.executes(
															commandContext -> method_13199(
																	commandContext,
																	class_2232.method_9443(commandContext, "loot_table"),
																	class_2262.method_9696(commandContext, "pos"),
																	method_13178(commandContext.getSource(), class_1304.field_6171),
																	arg
																)
														)
												)
										)
								)
						)
						.then(
							class_2170.method_9247("loot")
								.then(
									class_2170.method_9244("loot_table", class_2232.method_9441())
										.suggests(field_13605)
										.executes(commandContext -> method_13197(commandContext, class_2232.method_9443(commandContext, "loot_table"), arg))
								)
						)
						.then(
							class_2170.method_9247("kill")
								.then(
									class_2170.method_9244("target", class_2186.method_9309())
										.executes(commandContext -> method_13189(commandContext, class_2186.method_9313(commandContext, "target"), arg))
								)
						)
						.then(
							class_2170.method_9247("mine")
								.then(
									class_2170.method_9244("pos", class_2262.method_9698())
										.executes(commandContext -> method_13219(commandContext, class_2262.method_9696(commandContext, "pos"), class_1799.field_8037, arg))
										.then(
											class_2170.method_9244("tool", class_2287.method_9776())
												.executes(
													commandContext -> method_13219(
															commandContext, class_2262.method_9696(commandContext, "pos"), class_2287.method_9777(commandContext, "tool").method_9781(1, false), arg
														)
												)
										)
										.then(
											class_2170.method_9247("mainhand")
												.executes(
													commandContext -> method_13219(
															commandContext, class_2262.method_9696(commandContext, "pos"), method_13178(commandContext.getSource(), class_1304.field_6173), arg
														)
												)
										)
										.then(
											class_2170.method_9247("offhand")
												.executes(
													commandContext -> method_13219(
															commandContext, class_2262.method_9696(commandContext, "pos"), method_13178(commandContext.getSource(), class_1304.field_6171), arg
														)
												)
										)
								)
						)
			)
		);
	}

	private static <T extends ArgumentBuilder<class_2168, T>> T method_13206(T argumentBuilder, class_3039.class_3042 arg) {
		return argumentBuilder.then(
				class_2170.method_9247("replace")
					.then(
						class_2170.method_9247("entity")
							.then(
								class_2170.method_9244("entities", class_2186.method_9306())
									.then(
										arg.construct(
												class_2170.method_9244("slot", class_2240.method_9473()),
												(commandContext, list, argx) -> method_13187(
														class_2186.method_9317(commandContext, "entities"), class_2240.method_9469(commandContext, "slot"), list.size(), list, argx
													)
											)
											.then(
												arg.construct(
													class_2170.method_9244("count", IntegerArgumentType.integer(0)),
													(commandContext, list, argx) -> method_13187(
															class_2186.method_9317(commandContext, "entities"),
															class_2240.method_9469(commandContext, "slot"),
															IntegerArgumentType.getInteger(commandContext, "count"),
															list,
															argx
														)
												)
											)
									)
							)
					)
					.then(
						class_2170.method_9247("block")
							.then(
								class_2170.method_9244("targetPos", class_2262.method_9698())
									.then(
										arg.construct(
												class_2170.method_9244("slot", class_2240.method_9473()),
												(commandContext, list, argx) -> method_13209(
														commandContext.getSource(),
														class_2262.method_9696(commandContext, "targetPos"),
														class_2240.method_9469(commandContext, "slot"),
														list.size(),
														list,
														argx
													)
											)
											.then(
												arg.construct(
													class_2170.method_9244("count", IntegerArgumentType.integer(0)),
													(commandContext, list, argx) -> method_13209(
															commandContext.getSource(),
															class_2262.method_9696(commandContext, "targetPos"),
															IntegerArgumentType.getInteger(commandContext, "slot"),
															IntegerArgumentType.getInteger(commandContext, "count"),
															list,
															argx
														)
												)
											)
									)
							)
					)
			)
			.then(
				class_2170.method_9247("insert")
					.then(
						arg.construct(
							class_2170.method_9244("targetPos", class_2262.method_9698()),
							(commandContext, list, argx) -> method_13196(commandContext.getSource(), class_2262.method_9696(commandContext, "targetPos"), list, argx)
						)
					)
			)
			.then(
				class_2170.method_9247("give")
					.then(
						arg.construct(
							class_2170.method_9244("players", class_2186.method_9308()),
							(commandContext, list, argx) -> method_13201(class_2186.method_9312(commandContext, "players"), list, argx)
						)
					)
			)
			.then(
				class_2170.method_9247("spawn")
					.then(
						arg.construct(
							class_2170.method_9244("targetPos", class_2277.method_9737()),
							(commandContext, list, argx) -> method_13183(commandContext.getSource(), class_2277.method_9736(commandContext, "targetPos"), list, argx)
						)
					)
			);
	}

	private static class_1263 method_13207(class_2168 arg, class_2338 arg2) throws CommandSyntaxException {
		class_2586 lv = arg.method_9225().method_8321(arg2);
		if (!(lv instanceof class_1263)) {
			throw class_3102.field_13696.create();
		} else {
			return (class_1263)lv;
		}
	}

	private static int method_13196(class_2168 arg, class_2338 arg2, List<class_1799> list, class_3039.class_3040 arg3) throws CommandSyntaxException {
		class_1263 lv = method_13207(arg, arg2);
		List<class_1799> list2 = Lists.<class_1799>newArrayListWithCapacity(list.size());

		for (class_1799 lv2 : list) {
			if (method_13223(lv, lv2.method_7972())) {
				lv.method_5431();
				list2.add(lv2);
			}
		}

		arg3.accept(list2);
		return list2.size();
	}

	private static boolean method_13223(class_1263 arg, class_1799 arg2) {
		boolean bl = false;

		for (int i = 0; i < arg.method_5439() && !arg2.method_7960(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (arg.method_5437(i, arg2)) {
				if (lv.method_7960()) {
					arg.method_5447(i, arg2);
					bl = true;
					break;
				}

				if (method_13218(lv, arg2)) {
					int j = arg2.method_7914() - lv.method_7947();
					int k = Math.min(arg2.method_7947(), j);
					arg2.method_7934(k);
					lv.method_7933(k);
					bl = true;
				}
			}
		}

		return bl;
	}

	private static int method_13209(class_2168 arg, class_2338 arg2, int i, int j, List<class_1799> list, class_3039.class_3040 arg3) throws CommandSyntaxException {
		class_1263 lv = method_13207(arg, arg2);
		int k = lv.method_5439();
		if (i >= 0 && i < k) {
			List<class_1799> list2 = Lists.<class_1799>newArrayListWithCapacity(list.size());

			for (int l = 0; l < j; l++) {
				int m = i + l;
				class_1799 lv2 = l < list.size() ? (class_1799)list.get(l) : class_1799.field_8037;
				if (lv.method_5437(m, lv2)) {
					lv.method_5447(m, lv2);
					list2.add(lv2);
				}
			}

			arg3.accept(list2);
			return list2.size();
		} else {
			throw class_3102.field_13695.create(i);
		}
	}

	private static boolean method_13218(class_1799 arg, class_1799 arg2) {
		return arg.method_7909() == arg2.method_7909()
			&& arg.method_7919() == arg2.method_7919()
			&& arg.method_7947() <= arg.method_7914()
			&& Objects.equals(arg.method_7969(), arg2.method_7969());
	}

	private static int method_13201(Collection<class_3222> collection, List<class_1799> list, class_3039.class_3040 arg) throws CommandSyntaxException {
		List<class_1799> list2 = Lists.<class_1799>newArrayListWithCapacity(list.size());

		for (class_1799 lv : list) {
			for (class_3222 lv2 : collection) {
				if (lv2.field_7514.method_7394(lv.method_7972())) {
					list2.add(lv);
				}
			}
		}

		arg.accept(list2);
		return list2.size();
	}

	private static void method_16139(class_1297 arg, List<class_1799> list, int i, int j, List<class_1799> list2) {
		for (int k = 0; k < j; k++) {
			class_1799 lv = k < list.size() ? (class_1799)list.get(k) : class_1799.field_8037;
			if (arg.method_5758(i + k, lv.method_7972())) {
				list2.add(lv);
			}
		}
	}

	private static int method_13187(Collection<? extends class_1297> collection, int i, int j, List<class_1799> list, class_3039.class_3040 arg) throws CommandSyntaxException {
		List<class_1799> list2 = Lists.<class_1799>newArrayListWithCapacity(list.size());

		for (class_1297 lv : collection) {
			if (lv instanceof class_3222) {
				class_3222 lv2 = (class_3222)lv;
				lv2.field_7498.method_7623();
				method_16139(lv, list, i, j, list2);
				lv2.field_7498.method_7623();
			} else {
				method_16139(lv, list, i, j, list2);
			}
		}

		arg.accept(list2);
		return list2.size();
	}

	private static int method_13183(class_2168 arg, class_243 arg2, List<class_1799> list, class_3039.class_3040 arg3) throws CommandSyntaxException {
		class_3218 lv = arg.method_9225();
		list.forEach(arg3x -> {
			class_1542 lvx = new class_1542(lv, arg2.field_1352, arg2.field_1351, arg2.field_1350, arg3x.method_7972());
			lvx.method_6988();
			lv.method_8649(lvx);
		});
		arg3.accept(list);
		return list.size();
	}

	private static void method_13213(class_2168 arg, List<class_1799> list) {
		if (list.size() == 1) {
			class_1799 lv = (class_1799)list.get(0);
			arg.method_9226(new class_2588("commands.drop.success.single", lv.method_7947(), lv.method_7954()), false);
		} else {
			arg.method_9226(new class_2588("commands.drop.success.multiple", list.size()), false);
		}
	}

	private static void method_13212(class_2168 arg, List<class_1799> list, class_2960 arg2) {
		if (list.size() == 1) {
			class_1799 lv = (class_1799)list.get(0);
			arg.method_9226(new class_2588("commands.drop.success.single_with_table", lv.method_7947(), lv.method_7954(), arg2), false);
		} else {
			arg.method_9226(new class_2588("commands.drop.success.multiple_with_table", list.size(), arg2), false);
		}
	}

	private static class_1799 method_13178(class_2168 arg, class_1304 arg2) throws CommandSyntaxException {
		class_1297 lv = arg.method_9229();
		if (lv instanceof class_1309) {
			return ((class_1309)lv).method_6118(arg2);
		} else {
			throw field_13604.create(lv.method_5476());
		}
	}

	private static int method_13219(CommandContext<class_2168> commandContext, class_2338 arg, class_1799 arg2, class_3039.class_3041 arg3) throws CommandSyntaxException {
		class_2168 lv = commandContext.getSource();
		class_3218 lv2 = lv.method_9225();
		class_2680 lv3 = lv2.method_8320(arg);
		class_2586 lv4 = lv2.method_8321(arg);
		class_47.class_48 lv5 = new class_47.class_48(lv2)
			.method_312(class_181.field_1232, arg)
			.method_312(class_181.field_1224, lv3)
			.method_306(class_181.field_1228, lv4)
			.method_306(class_181.field_1226, lv.method_9228())
			.method_312(class_181.field_1229, arg2);
		List<class_1799> list = lv3.method_11612(lv5);
		return arg3.accept(commandContext, list, listx -> method_13212(lv, listx, lv3.method_11614().method_9580()));
	}

	private static int method_13189(CommandContext<class_2168> commandContext, class_1297 arg, class_3039.class_3041 arg2) throws CommandSyntaxException {
		if (!(arg instanceof class_1309)) {
			throw field_13606.create(arg.method_5476());
		} else {
			class_2960 lv = ((class_1309)arg).method_5989();
			class_2168 lv2 = commandContext.getSource();
			class_47.class_48 lv3 = new class_47.class_48(lv2.method_9225());
			class_1297 lv4 = lv2.method_9228();
			if (lv4 instanceof class_1657) {
				lv3.method_312(class_181.field_1233, (class_1657)lv4);
			}

			lv3.method_312(class_181.field_1231, class_1282.field_5846);
			lv3.method_306(class_181.field_1227, lv4);
			lv3.method_306(class_181.field_1230, lv4);
			lv3.method_312(class_181.field_1226, arg);
			lv3.method_312(class_181.field_1232, new class_2338(lv2.method_9222()));
			class_52 lv5 = lv2.method_9211().method_3857().method_367(lv);
			List<class_1799> list = lv5.method_319(lv3.method_309(class_173.field_1173));
			return arg2.accept(commandContext, list, listx -> method_13212(lv2, listx, lv));
		}
	}

	private static int method_13197(CommandContext<class_2168> commandContext, class_2960 arg, class_3039.class_3041 arg2) throws CommandSyntaxException {
		class_2168 lv = commandContext.getSource();
		class_47.class_48 lv2 = new class_47.class_48(lv.method_9225())
			.method_306(class_181.field_1226, lv.method_9228())
			.method_312(class_181.field_1232, new class_2338(lv.method_9222()));
		return method_13180(commandContext, arg, lv2.method_309(class_173.field_1179), arg2);
	}

	private static int method_13199(CommandContext<class_2168> commandContext, class_2960 arg, class_2338 arg2, class_1799 arg3, class_3039.class_3041 arg4) throws CommandSyntaxException {
		class_2168 lv = commandContext.getSource();
		class_47 lv2 = new class_47.class_48(lv.method_9225())
			.method_312(class_181.field_1232, arg2)
			.method_312(class_181.field_1229, arg3)
			.method_309(class_173.field_1176);
		return method_13180(commandContext, arg, lv2, arg4);
	}

	private static int method_13180(CommandContext<class_2168> commandContext, class_2960 arg, class_47 arg2, class_3039.class_3041 arg3) throws CommandSyntaxException {
		class_2168 lv = commandContext.getSource();
		class_52 lv2 = lv.method_9211().method_3857().method_367(arg);
		List<class_1799> list = lv2.method_319(arg2);
		return arg3.accept(commandContext, list, listx -> method_13213(lv, listx));
	}

	@FunctionalInterface
	interface class_3040 {
		void accept(List<class_1799> list) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_3041 {
		int accept(CommandContext<class_2168> commandContext, List<class_1799> list, class_3039.class_3040 arg) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_3042 {
		ArgumentBuilder<class_2168, ?> construct(ArgumentBuilder<class_2168, ?> argumentBuilder, class_3039.class_3041 arg);
	}
}
