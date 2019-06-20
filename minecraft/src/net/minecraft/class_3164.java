package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class class_3164 {
	private static final SimpleCommandExceptionType field_13796 = new SimpleCommandExceptionType(new class_2588("commands.data.merge.failed"));
	private static final DynamicCommandExceptionType field_13791 = new DynamicCommandExceptionType(object -> new class_2588("commands.data.get.invalid", object));
	private static final DynamicCommandExceptionType field_13793 = new DynamicCommandExceptionType(object -> new class_2588("commands.data.get.unknown", object));
	private static final SimpleCommandExceptionType field_13794 = new SimpleCommandExceptionType(new class_2588("commands.data.get.multiple"));
	private static final DynamicCommandExceptionType field_13795 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.data.modify.expected_list", object)
	);
	private static final DynamicCommandExceptionType field_13797 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.data.modify.expected_object", object)
	);
	private static final DynamicCommandExceptionType field_17441 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.data.modify.invalid_index", object)
	);
	public static final List<Function<String, class_3164.class_3167>> field_13790 = ImmutableList.of(class_3169.field_13800, class_3161.field_13786);
	public static final List<class_3164.class_3167> field_13798 = (List<class_3164.class_3167>)field_13790.stream()
		.map(function -> (class_3164.class_3167)function.apply("target"))
		.collect(ImmutableList.toImmutableList());
	public static final List<class_3164.class_3167> field_13792 = (List<class_3164.class_3167>)field_13790.stream()
		.map(function -> (class_3164.class_3167)function.apply("source"))
		.collect(ImmutableList.toImmutableList());

	public static void method_13905(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("data").requires(arg -> arg.method_9259(2));

		for (class_3164.class_3167 lv : field_13798) {
			literalArgumentBuilder.then(
					lv.method_13925(
						class_2170.method_9247("merge"),
						argumentBuilder -> argumentBuilder.then(
								class_2170.method_9244("nbt", class_2179.method_9284())
									.executes(commandContext -> method_13901(commandContext.getSource(), lv.method_13924(commandContext), class_2179.method_9285(commandContext, "nbt")))
							)
					)
				)
				.then(
					lv.method_13925(
						class_2170.method_9247("get"),
						argumentBuilder -> argumentBuilder.executes(commandContext -> method_13908((class_2168)commandContext.getSource(), lv.method_13924(commandContext)))
								.then(
									class_2170.method_9244("path", class_2203.method_9360())
										.executes(commandContext -> method_13916(commandContext.getSource(), lv.method_13924(commandContext), class_2203.method_9358(commandContext, "path")))
										.then(
											class_2170.method_9244("scale", DoubleArgumentType.doubleArg())
												.executes(
													commandContext -> method_13903(
															commandContext.getSource(),
															lv.method_13924(commandContext),
															class_2203.method_9358(commandContext, "path"),
															DoubleArgumentType.getDouble(commandContext, "scale")
														)
												)
										)
								)
					)
				)
				.then(
					lv.method_13925(
						class_2170.method_9247("remove"),
						argumentBuilder -> argumentBuilder.then(
								class_2170.method_9244("path", class_2203.method_9360())
									.executes(commandContext -> method_13885(commandContext.getSource(), lv.method_13924(commandContext), class_2203.method_9358(commandContext, "path")))
							)
					)
				)
				.then(
					method_13898(
						(argumentBuilder, arg) -> argumentBuilder.then(
									class_2170.method_9247("insert")
										.then(class_2170.method_9244("index", IntegerArgumentType.integer()).then(arg.create((commandContext, argx, arg2, list) -> {
											int i = IntegerArgumentType.getInteger(commandContext, "index");
											return method_13910(i, argx, arg2, list);
										})))
								)
								.then(class_2170.method_9247("prepend").then(arg.create((commandContext, argx, arg2, list) -> method_13910(0, argx, arg2, list))))
								.then(class_2170.method_9247("append").then(arg.create((commandContext, argx, arg2, list) -> method_13910(-1, argx, arg2, list))))
								.then(
									class_2170.method_9247("set").then(arg.create((commandContext, argx, arg2, list) -> arg2.method_9368(argx, Iterables.getLast(list)::method_10707)))
								)
								.then(class_2170.method_9247("merge").then(arg.create((commandContext, argx, arg2, list) -> {
									Collection<class_2520> collection = arg2.method_9367(argx, class_2487::new);
									int i = 0;

									for (class_2520 lvx : collection) {
										if (!(lvx instanceof class_2487)) {
											throw field_13797.create(lvx);
										}

										class_2487 lv2 = (class_2487)lvx;
										class_2487 lv3 = lv2.method_10553();

										for (class_2520 lv4 : list) {
											if (!(lv4 instanceof class_2487)) {
												throw field_13797.create(lv4);
											}

											lv2.method_10543((class_2487)lv4);
										}

										i += lv3.equals(lv2) ? 0 : 1;
									}

									return i;
								})))
					)
				);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_13910(int i, class_2487 arg, class_2203.class_2209 arg2, List<class_2520> list) throws CommandSyntaxException {
		Collection<class_2520> collection = arg2.method_9367(arg, class_2499::new);
		int j = 0;

		for (class_2520 lv : collection) {
			if (!(lv instanceof class_2483)) {
				throw field_13795.create(lv);
			}

			boolean bl = false;
			class_2483<?> lv2 = (class_2483<?>)lv;
			int k = i < 0 ? lv2.size() + i + 1 : i;

			for (class_2520 lv3 : list) {
				try {
					if (lv2.method_10533(k, lv3.method_10707())) {
						k++;
						bl = true;
					}
				} catch (IndexOutOfBoundsException var14) {
					throw field_17441.create(k);
				}
			}

			j += bl ? 1 : 0;
		}

		return j;
	}

	private static ArgumentBuilder<class_2168, ?> method_13898(BiConsumer<ArgumentBuilder<class_2168, ?>, class_3164.class_3166> biConsumer) {
		LiteralArgumentBuilder<class_2168> literalArgumentBuilder = class_2170.method_9247("modify");

		for (class_3164.class_3167 lv : field_13798) {
			lv.method_13925(
				literalArgumentBuilder,
				argumentBuilder -> {
					ArgumentBuilder<class_2168, ?> argumentBuilder2 = class_2170.method_9244("targetPath", class_2203.method_9360());

					for (class_3164.class_3167 lvx : field_13792) {
						biConsumer.accept(
							argumentBuilder2,
							(class_3164.class_3166)arg3 -> lvx.method_13925(class_2170.method_9247("from"), argumentBuilderx -> argumentBuilderx.executes(commandContext -> {
										List<class_2520> list = Collections.singletonList(lvx.method_13924(commandContext).method_13881());
										return method_13920(commandContext, lv, arg3, list);
									}).then(class_2170.method_9244("sourcePath", class_2203.method_9360()).executes(commandContext -> {
										class_3162 lvxx = lvx.method_13924(commandContext);
										class_2203.class_2209 lv2 = class_2203.method_9358(commandContext, "sourcePath");
										List<class_2520> list = lv2.method_9366(lvxx.method_13881());
										return method_13920(commandContext, lv, arg3, list);
									})))
						);
					}

					biConsumer.accept(
						argumentBuilder2,
						(class_3164.class_3166)arg2 -> (LiteralArgumentBuilder)class_2170.method_9247("value")
								.then(class_2170.method_9244("value", class_2212.method_9389()).executes(commandContext -> {
									List<class_2520> list = Collections.singletonList(class_2212.method_9390(commandContext, "value"));
									return method_13920(commandContext, lv, arg2, list);
								}))
					);
					return argumentBuilder.then(argumentBuilder2);
				}
			);
		}

		return literalArgumentBuilder;
	}

	private static int method_13920(CommandContext<class_2168> commandContext, class_3164.class_3167 arg, class_3164.class_3165 arg2, List<class_2520> list) throws CommandSyntaxException {
		class_3162 lv = arg.method_13924(commandContext);
		class_2203.class_2209 lv2 = class_2203.method_9358(commandContext, "targetPath");
		class_2487 lv3 = lv.method_13881();
		int i = arg2.modify(commandContext, lv3, lv2, list);
		if (i == 0) {
			throw field_13796.create();
		} else {
			lv.method_13880(lv3);
			commandContext.getSource().method_9226(lv.method_13883(), true);
			return i;
		}
	}

	private static int method_13885(class_2168 arg, class_3162 arg2, class_2203.class_2209 arg3) throws CommandSyntaxException {
		class_2487 lv = arg2.method_13881();
		int i = arg3.method_9372(lv);
		if (i == 0) {
			throw field_13796.create();
		} else {
			arg2.method_13880(lv);
			arg.method_9226(arg2.method_13883(), true);
			return i;
		}
	}

	private static class_2520 method_13921(class_2203.class_2209 arg, class_3162 arg2) throws CommandSyntaxException {
		Collection<class_2520> collection = arg.method_9366(arg2.method_13881());
		Iterator<class_2520> iterator = collection.iterator();
		class_2520 lv = (class_2520)iterator.next();
		if (iterator.hasNext()) {
			throw field_13794.create();
		} else {
			return lv;
		}
	}

	private static int method_13916(class_2168 arg, class_3162 arg2, class_2203.class_2209 arg3) throws CommandSyntaxException {
		class_2520 lv = method_13921(arg3, arg2);
		int i;
		if (lv instanceof class_2514) {
			i = class_3532.method_15357(((class_2514)lv).method_10697());
		} else if (lv instanceof class_2483) {
			i = ((class_2483)lv).size();
		} else if (lv instanceof class_2487) {
			i = ((class_2487)lv).method_10546();
		} else {
			if (!(lv instanceof class_2519)) {
				throw field_13793.create(arg3.toString());
			}

			i = lv.method_10714().length();
		}

		arg.method_9226(arg2.method_13882(lv), false);
		return i;
	}

	private static int method_13903(class_2168 arg, class_3162 arg2, class_2203.class_2209 arg3, double d) throws CommandSyntaxException {
		class_2520 lv = method_13921(arg3, arg2);
		if (!(lv instanceof class_2514)) {
			throw field_13791.create(arg3.toString());
		} else {
			int i = class_3532.method_15357(((class_2514)lv).method_10697() * d);
			arg.method_9226(arg2.method_13879(arg3, d, i), false);
			return i;
		}
	}

	private static int method_13908(class_2168 arg, class_3162 arg2) throws CommandSyntaxException {
		arg.method_9226(arg2.method_13882(arg2.method_13881()), false);
		return 1;
	}

	private static int method_13901(class_2168 arg, class_3162 arg2, class_2487 arg3) throws CommandSyntaxException {
		class_2487 lv = arg2.method_13881();
		class_2487 lv2 = lv.method_10553().method_10543(arg3);
		if (lv.equals(lv2)) {
			throw field_13796.create();
		} else {
			arg2.method_13880(lv2);
			arg.method_9226(arg2.method_13883(), true);
			return 1;
		}
	}

	interface class_3165 {
		int modify(CommandContext<class_2168> commandContext, class_2487 arg, class_2203.class_2209 arg2, List<class_2520> list) throws CommandSyntaxException;
	}

	interface class_3166 {
		ArgumentBuilder<class_2168, ?> create(class_3164.class_3165 arg);
	}

	public interface class_3167 {
		class_3162 method_13924(CommandContext<class_2168> commandContext) throws CommandSyntaxException;

		ArgumentBuilder<class_2168, ?> method_13925(
			ArgumentBuilder<class_2168, ?> argumentBuilder, Function<ArgumentBuilder<class_2168, ?>, ArgumentBuilder<class_2168, ?>> function
		);
	}
}
