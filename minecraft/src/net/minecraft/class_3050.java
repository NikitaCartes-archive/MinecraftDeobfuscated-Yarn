package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;

public class class_3050 {
	private static final Dynamic2CommandExceptionType field_13635 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.execute.blocks.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType field_13636 = new SimpleCommandExceptionType(new class_2588("commands.execute.conditional.fail"));
	private static final DynamicCommandExceptionType field_13637 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.execute.conditional.fail_count", object)
	);
	private static final BinaryOperator<ResultConsumer<class_2168>> field_13634 = (resultConsumer, resultConsumer2) -> (commandContext, bl, i) -> {
			resultConsumer.onCommandComplete(commandContext, bl, i);
			resultConsumer2.onCommandComplete(commandContext, bl, i);
		};

	public static void method_13271(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralCommandNode<class_2168> literalCommandNode = commandDispatcher.register(class_2170.method_9247("execute").requires(arg -> arg.method_9259(2)));
		commandDispatcher.register(
			class_2170.method_9247("execute")
				.requires(arg -> arg.method_9259(2))
				.then(class_2170.method_9247("run").redirect(commandDispatcher.getRoot()))
				.then(method_13298(literalCommandNode, class_2170.method_9247("if"), true))
				.then(method_13298(literalCommandNode, class_2170.method_9247("unless"), false))
				.then(class_2170.method_9247("as").then(class_2170.method_9244("targets", class_2186.method_9306()).fork(literalCommandNode, commandContext -> {
					List<class_2168> list = Lists.<class_2168>newArrayList();

					for (class_1297 lv : class_2186.method_9307(commandContext, "targets")) {
						list.add(commandContext.getSource().method_9232(lv));
					}

					return list;
				})))
				.then(class_2170.method_9247("at").then(class_2170.method_9244("targets", class_2186.method_9306()).fork(literalCommandNode, commandContext -> {
					List<class_2168> list = Lists.<class_2168>newArrayList();

					for (class_1297 lv : class_2186.method_9307(commandContext, "targets")) {
						list.add(commandContext.getSource().method_9227((class_3218)lv.field_6002).method_9208(lv.method_5812()).method_9216(lv.method_5802()));
					}

					return list;
				})))
				.then(
					class_2170.method_9247("store")
						.then(method_13289(literalCommandNode, class_2170.method_9247("result"), true))
						.then(method_13289(literalCommandNode, class_2170.method_9247("success"), false))
				)
				.then(
					class_2170.method_9247("positioned")
						.then(
							class_2170.method_9244("pos", class_2277.method_9737())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().method_9208(class_2277.method_9736(commandContext, "pos")))
						)
						.then(class_2170.method_9247("as").then(class_2170.method_9244("targets", class_2186.method_9306()).fork(literalCommandNode, commandContext -> {
							List<class_2168> list = Lists.<class_2168>newArrayList();

							for (class_1297 lv : class_2186.method_9307(commandContext, "targets")) {
								list.add(commandContext.getSource().method_9208(lv.method_5812()));
							}

							return list;
						})))
				)
				.then(
					class_2170.method_9247("rotated")
						.then(
							class_2170.method_9244("rot", class_2270.method_9717())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource().method_9216(class_2270.method_9716(commandContext, "rot").method_9709(commandContext.getSource()))
								)
						)
						.then(class_2170.method_9247("as").then(class_2170.method_9244("targets", class_2186.method_9306()).fork(literalCommandNode, commandContext -> {
							List<class_2168> list = Lists.<class_2168>newArrayList();

							for (class_1297 lv : class_2186.method_9307(commandContext, "targets")) {
								list.add(commandContext.getSource().method_9216(lv.method_5802()));
							}

							return list;
						})))
				)
				.then(
					class_2170.method_9247("facing")
						.then(
							class_2170.method_9247("entity")
								.then(
									class_2170.method_9244("targets", class_2186.method_9306())
										.then(class_2170.method_9244("anchor", class_2183.method_9295()).fork(literalCommandNode, commandContext -> {
											List<class_2168> list = Lists.<class_2168>newArrayList();
											class_2183.class_2184 lv = class_2183.method_9294(commandContext, "anchor");

											for (class_1297 lv2 : class_2186.method_9307(commandContext, "targets")) {
												list.add(commandContext.getSource().method_9220(lv2, lv));
											}

											return list;
										}))
								)
						)
						.then(
							class_2170.method_9244("pos", class_2277.method_9737())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().method_9221(class_2277.method_9736(commandContext, "pos")))
						)
				)
				.then(
					class_2170.method_9247("align")
						.then(
							class_2170.method_9244("axes", class_2273.method_9721())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.method_9208(commandContext.getSource().method_9222().method_1032(class_2273.method_9720(commandContext, "axes")))
								)
						)
				)
				.then(
					class_2170.method_9247("anchored")
						.then(
							class_2170.method_9244("anchor", class_2183.method_9295())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().method_9218(class_2183.method_9294(commandContext, "anchor")))
						)
				)
				.then(
					class_2170.method_9247("in")
						.then(
							class_2170.method_9244("dimension", class_2181.method_9288())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.method_9227(commandContext.getSource().method_9211().method_3847(class_2181.method_9289(commandContext, "dimension")))
								)
						)
				)
		);
	}

	private static ArgumentBuilder<class_2168, ?> method_13289(
		LiteralCommandNode<class_2168> literalCommandNode, LiteralArgumentBuilder<class_2168> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
			class_2170.method_9247("score")
				.then(
					class_2170.method_9244("targets", class_2233.method_9451())
						.suggests(class_2233.field_9951)
						.then(
							class_2170.method_9244("objective", class_2214.method_9391())
								.redirect(
									literalCommandNode,
									commandContext -> method_13290(
											commandContext.getSource(), class_2233.method_9449(commandContext, "targets"), class_2214.method_9395(commandContext, "objective"), bl
										)
								)
						)
				)
		);
		literalArgumentBuilder.then(
			class_2170.method_9247("bossbar")
				.then(
					class_2170.method_9244("id", class_2232.method_9441())
						.suggests(class_3019.field_13482)
						.then(
							class_2170.method_9247("value")
								.redirect(literalCommandNode, commandContext -> method_13297(commandContext.getSource(), class_3019.method_13054(commandContext), true, bl))
						)
						.then(
							class_2170.method_9247("max")
								.redirect(literalCommandNode, commandContext -> method_13297(commandContext.getSource(), class_3019.method_13054(commandContext), false, bl))
						)
				)
		);

		for (class_3164.class_3167 lv : class_3164.field_13798) {
			lv.method_13925(
				literalArgumentBuilder,
				argumentBuilder -> argumentBuilder.then(
						class_2170.method_9244("path", class_2203.method_9360())
							.then(
								class_2170.method_9247("int")
									.then(
										class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														class_2203.method_9358(commandContext, "path"),
														i -> new class_2497((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								class_2170.method_9247("float")
									.then(
										class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														class_2203.method_9358(commandContext, "path"),
														i -> new class_2494((float)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								class_2170.method_9247("short")
									.then(
										class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														class_2203.method_9358(commandContext, "path"),
														i -> new class_2516((short)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
														bl
													)
											)
									)
							)
							.then(
								class_2170.method_9247("long")
									.then(
										class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														class_2203.method_9358(commandContext, "path"),
														i -> new class_2503((long)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								class_2170.method_9247("double")
									.then(
										class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														class_2203.method_9358(commandContext, "path"),
														i -> new class_2489((double)i * DoubleArgumentType.getDouble(commandContext, "scale")),
														bl
													)
											)
									)
							)
							.then(
								class_2170.method_9247("byte")
									.then(
										class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														class_2203.method_9358(commandContext, "path"),
														i -> new class_2481((byte)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
														bl
													)
											)
									)
							)
					)
			);
		}

		return literalArgumentBuilder;
	}

	private static class_2168 method_13290(class_2168 arg, Collection<String> collection, class_266 arg2, boolean bl) {
		class_269 lv = arg.method_9211().method_3845();
		return arg.method_9209((commandContext, bl2, i) -> {
			for (String string : collection) {
				class_267 lvx = lv.method_1180(string, arg2);
				int j = bl ? i : (bl2 ? 1 : 0);
				lvx.method_1128(j);
			}
		}, field_13634);
	}

	private static class_2168 method_13297(class_2168 arg, class_3002 arg2, boolean bl, boolean bl2) {
		return arg.method_9209((commandContext, bl3, i) -> {
			int j = bl2 ? i : (bl3 ? 1 : 0);
			if (bl) {
				arg2.method_12954(j);
			} else {
				arg2.method_12956(j);
			}
		}, field_13634);
	}

	private static class_2168 method_13265(class_2168 arg, class_3162 arg2, class_2203.class_2209 arg3, IntFunction<class_2520> intFunction, boolean bl) {
		return arg.method_9209((commandContext, bl2, i) -> {
			try {
				class_2487 lv = arg2.method_13881();
				int j = bl ? i : (bl2 ? 1 : 0);
				arg3.method_9368(lv, () -> (class_2520)intFunction.apply(j));
				arg2.method_13880(lv);
			} catch (CommandSyntaxException var9) {
			}
		}, field_13634);
	}

	private static ArgumentBuilder<class_2168, ?> method_13298(
		CommandNode<class_2168> commandNode, LiteralArgumentBuilder<class_2168> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
				class_2170.method_9247("block")
					.then(
						class_2170.method_9244("pos", class_2262.method_9698())
							.then(
								method_13310(
									commandNode,
									class_2170.method_9244("block", class_2252.method_9645()),
									bl,
									commandContext -> class_2252.method_9644(commandContext, "block")
											.test(new class_2694(commandContext.getSource().method_9225(), class_2262.method_9696(commandContext, "pos"), true))
								)
							)
					)
			)
			.then(
				class_2170.method_9247("score")
					.then(
						class_2170.method_9244("target", class_2233.method_9447())
							.suggests(class_2233.field_9951)
							.then(
								class_2170.method_9244("targetObjective", class_2214.method_9391())
									.then(
										class_2170.method_9247("=")
											.then(
												class_2170.method_9244("source", class_2233.method_9447())
													.suggests(class_2233.field_9951)
													.then(
														method_13310(
															commandNode,
															class_2170.method_9244("sourceObjective", class_2214.method_9391()),
															bl,
															commandContext -> method_13263(commandContext, Integer::equals)
														)
													)
											)
									)
									.then(
										class_2170.method_9247("<")
											.then(
												class_2170.method_9244("source", class_2233.method_9447())
													.suggests(class_2233.field_9951)
													.then(
														method_13310(
															commandNode,
															class_2170.method_9244("sourceObjective", class_2214.method_9391()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer < integer2)
														)
													)
											)
									)
									.then(
										class_2170.method_9247("<=")
											.then(
												class_2170.method_9244("source", class_2233.method_9447())
													.suggests(class_2233.field_9951)
													.then(
														method_13310(
															commandNode,
															class_2170.method_9244("sourceObjective", class_2214.method_9391()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer <= integer2)
														)
													)
											)
									)
									.then(
										class_2170.method_9247(">")
											.then(
												class_2170.method_9244("source", class_2233.method_9447())
													.suggests(class_2233.field_9951)
													.then(
														method_13310(
															commandNode,
															class_2170.method_9244("sourceObjective", class_2214.method_9391()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer > integer2)
														)
													)
											)
									)
									.then(
										class_2170.method_9247(">=")
											.then(
												class_2170.method_9244("source", class_2233.method_9447())
													.suggests(class_2233.field_9951)
													.then(
														method_13310(
															commandNode,
															class_2170.method_9244("sourceObjective", class_2214.method_9391()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer >= integer2)
														)
													)
											)
									)
									.then(
										class_2170.method_9247("matches")
											.then(
												method_13310(
													commandNode,
													class_2170.method_9244("range", class_2224.method_9422()),
													bl,
													commandContext -> method_13313(commandContext, class_2224.class_2227.method_9425(commandContext, "range"))
												)
											)
									)
							)
					)
			)
			.then(
				class_2170.method_9247("blocks")
					.then(
						class_2170.method_9244("start", class_2262.method_9698())
							.then(
								class_2170.method_9244("end", class_2262.method_9698())
									.then(
										class_2170.method_9244("destination", class_2262.method_9698())
											.then(method_13320(commandNode, class_2170.method_9247("all"), bl, false))
											.then(method_13320(commandNode, class_2170.method_9247("masked"), bl, true))
									)
							)
					)
			)
			.then(
				class_2170.method_9247("entity")
					.then(
						class_2170.method_9244("entities", class_2186.method_9306())
							.fork(commandNode, commandContext -> method_13319(commandContext, bl, !class_2186.method_9307(commandContext, "entities").isEmpty()))
							.executes(method_13323(bl, commandContext -> class_2186.method_9307(commandContext, "entities").size()))
					)
			);

		for (class_3164.class_3167 lv : class_3164.field_13792) {
			literalArgumentBuilder.then(
				lv.method_13925(
					class_2170.method_9247("data"),
					argumentBuilder -> argumentBuilder.then(
							class_2170.method_9244("path", class_2203.method_9360())
								.fork(
									commandNode,
									commandContext -> method_13319(commandContext, bl, method_13303(lv.method_13924(commandContext), class_2203.method_9358(commandContext, "path")) > 0)
								)
								.executes(method_13323(bl, commandContext -> method_13303(lv.method_13924(commandContext), class_2203.method_9358(commandContext, "path"))))
						)
				)
			);
		}

		return literalArgumentBuilder;
	}

	private static Command<class_2168> method_13323(boolean bl, class_3050.class_3051 arg) {
		return bl ? commandContext -> {
			int i = arg.test(commandContext);
			if (i > 0) {
				commandContext.getSource().method_9226(new class_2588("commands.execute.conditional.pass_count", i), false);
				return i;
			} else {
				throw field_13636.create();
			}
		} : commandContext -> {
			int i = arg.test(commandContext);
			if (i == 0) {
				commandContext.getSource().method_9226(new class_2588("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw field_13637.create(i);
			}
		};
	}

	private static int method_13303(class_3162 arg, class_2203.class_2209 arg2) throws CommandSyntaxException {
		return arg2.method_9374(arg.method_13881());
	}

	private static boolean method_13263(CommandContext<class_2168> commandContext, BiPredicate<Integer, Integer> biPredicate) throws CommandSyntaxException {
		String string = class_2233.method_9452(commandContext, "target");
		class_266 lv = class_2214.method_9395(commandContext, "targetObjective");
		String string2 = class_2233.method_9452(commandContext, "source");
		class_266 lv2 = class_2214.method_9395(commandContext, "sourceObjective");
		class_269 lv3 = commandContext.getSource().method_9211().method_3845();
		if (lv3.method_1183(string, lv) && lv3.method_1183(string2, lv2)) {
			class_267 lv4 = lv3.method_1180(string, lv);
			class_267 lv5 = lv3.method_1180(string2, lv2);
			return biPredicate.test(lv4.method_1126(), lv5.method_1126());
		} else {
			return false;
		}
	}

	private static boolean method_13313(CommandContext<class_2168> commandContext, class_2096.class_2100 arg) throws CommandSyntaxException {
		String string = class_2233.method_9452(commandContext, "target");
		class_266 lv = class_2214.method_9395(commandContext, "targetObjective");
		class_269 lv2 = commandContext.getSource().method_9211().method_3845();
		return !lv2.method_1183(string, lv) ? false : arg.method_9054(lv2.method_1180(string, lv).method_1126());
	}

	private static Collection<class_2168> method_13319(CommandContext<class_2168> commandContext, boolean bl, boolean bl2) {
		return (Collection<class_2168>)(bl2 == bl ? Collections.singleton(commandContext.getSource()) : Collections.emptyList());
	}

	private static ArgumentBuilder<class_2168, ?> method_13310(
		CommandNode<class_2168> commandNode, ArgumentBuilder<class_2168, ?> argumentBuilder, boolean bl, class_3050.class_3052 arg
	) {
		return argumentBuilder.fork(commandNode, commandContext -> method_13319(commandContext, bl, arg.test(commandContext))).executes(commandContext -> {
			if (bl == arg.test(commandContext)) {
				commandContext.getSource().method_9226(new class_2588("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw field_13636.create();
			}
		});
	}

	private static ArgumentBuilder<class_2168, ?> method_13320(
		CommandNode<class_2168> commandNode, ArgumentBuilder<class_2168, ?> argumentBuilder, boolean bl, boolean bl2
	) {
		return argumentBuilder.fork(commandNode, commandContext -> method_13319(commandContext, bl, method_13272(commandContext, bl2).isPresent()))
			.executes(bl ? commandContext -> method_13306(commandContext, bl2) : commandContext -> method_13304(commandContext, bl2));
	}

	private static int method_13306(CommandContext<class_2168> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = method_13272(commandContext, bl);
		if (optionalInt.isPresent()) {
			commandContext.getSource().method_9226(new class_2588("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
			return optionalInt.getAsInt();
		} else {
			throw field_13636.create();
		}
	}

	private static int method_13304(CommandContext<class_2168> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = method_13272(commandContext, bl);
		if (optionalInt.isPresent()) {
			throw field_13637.create(optionalInt.getAsInt());
		} else {
			commandContext.getSource().method_9226(new class_2588("commands.execute.conditional.pass"), false);
			return 1;
		}
	}

	private static OptionalInt method_13272(CommandContext<class_2168> commandContext, boolean bl) throws CommandSyntaxException {
		return method_13261(
			commandContext.getSource().method_9225(),
			class_2262.method_9696(commandContext, "start"),
			class_2262.method_9696(commandContext, "end"),
			class_2262.method_9696(commandContext, "destination"),
			bl
		);
	}

	private static OptionalInt method_13261(class_3218 arg, class_2338 arg2, class_2338 arg3, class_2338 arg4, boolean bl) throws CommandSyntaxException {
		class_3341 lv = new class_3341(arg2, arg3);
		class_3341 lv2 = new class_3341(arg4, arg4.method_10081(lv.method_14659()));
		class_2338 lv3 = new class_2338(lv2.field_14381 - lv.field_14381, lv2.field_14380 - lv.field_14380, lv2.field_14379 - lv.field_14379);
		int i = lv.method_14660() * lv.method_14663() * lv.method_14664();
		if (i > 32768) {
			throw field_13635.create(32768, i);
		} else {
			int j = 0;

			for (int k = lv.field_14379; k <= lv.field_14376; k++) {
				for (int l = lv.field_14380; l <= lv.field_14377; l++) {
					for (int m = lv.field_14381; m <= lv.field_14378; m++) {
						class_2338 lv4 = new class_2338(m, l, k);
						class_2338 lv5 = lv4.method_10081(lv3);
						class_2680 lv6 = arg.method_8320(lv4);
						if (!bl || lv6.method_11614() != class_2246.field_10124) {
							if (lv6 != arg.method_8320(lv5)) {
								return OptionalInt.empty();
							}

							class_2586 lv7 = arg.method_8321(lv4);
							class_2586 lv8 = arg.method_8321(lv5);
							if (lv7 != null) {
								if (lv8 == null) {
									return OptionalInt.empty();
								}

								class_2487 lv9 = lv7.method_11007(new class_2487());
								lv9.method_10551("x");
								lv9.method_10551("y");
								lv9.method_10551("z");
								class_2487 lv10 = lv8.method_11007(new class_2487());
								lv10.method_10551("x");
								lv10.method_10551("y");
								lv10.method_10551("z");
								if (!lv9.equals(lv10)) {
									return OptionalInt.empty();
								}
							}

							j++;
						}
					}
				}
			}

			return OptionalInt.of(j);
		}
	}

	@FunctionalInterface
	interface class_3051 {
		int test(CommandContext<class_2168> commandContext) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_3052 {
		boolean test(CommandContext<class_2168> commandContext) throws CommandSyntaxException;
	}
}
