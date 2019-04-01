package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_3023 {
	private static final SimpleCommandExceptionType field_13493 = new SimpleCommandExceptionType(new class_2588("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType field_13491 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.clone.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType field_13492 = new SimpleCommandExceptionType(new class_2588("commands.clone.failed"));
	public static final Predicate<class_2694> field_13490 = arg -> !arg.method_11681().method_11588();

	public static void method_13089(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("clone")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("begin", class_2262.method_9698())
						.then(
							class_2170.method_9244("end", class_2262.method_9698())
								.then(
									class_2170.method_9244("destination", class_2262.method_9698())
										.executes(
											commandContext -> method_13090(
													commandContext.getSource(),
													class_2262.method_9696(commandContext, "begin"),
													class_2262.method_9696(commandContext, "end"),
													class_2262.method_9696(commandContext, "destination"),
													arg -> true,
													class_3023.class_3025.field_13499
												)
										)
										.then(
											class_2170.method_9247("replace")
												.executes(
													commandContext -> method_13090(
															commandContext.getSource(),
															class_2262.method_9696(commandContext, "begin"),
															class_2262.method_9696(commandContext, "end"),
															class_2262.method_9696(commandContext, "destination"),
															arg -> true,
															class_3023.class_3025.field_13499
														)
												)
												.then(
													class_2170.method_9247("force")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	arg -> true,
																	class_3023.class_3025.field_13497
																)
														)
												)
												.then(
													class_2170.method_9247("move")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	arg -> true,
																	class_3023.class_3025.field_13500
																)
														)
												)
												.then(
													class_2170.method_9247("normal")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	arg -> true,
																	class_3023.class_3025.field_13499
																)
														)
												)
										)
										.then(
											class_2170.method_9247("masked")
												.executes(
													commandContext -> method_13090(
															commandContext.getSource(),
															class_2262.method_9696(commandContext, "begin"),
															class_2262.method_9696(commandContext, "end"),
															class_2262.method_9696(commandContext, "destination"),
															field_13490,
															class_3023.class_3025.field_13499
														)
												)
												.then(
													class_2170.method_9247("force")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	field_13490,
																	class_3023.class_3025.field_13497
																)
														)
												)
												.then(
													class_2170.method_9247("move")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	field_13490,
																	class_3023.class_3025.field_13500
																)
														)
												)
												.then(
													class_2170.method_9247("normal")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	field_13490,
																	class_3023.class_3025.field_13499
																)
														)
												)
										)
										.then(
											class_2170.method_9247("filtered")
												.then(
													class_2170.method_9244("filter", class_2252.method_9645())
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	class_2262.method_9696(commandContext, "begin"),
																	class_2262.method_9696(commandContext, "end"),
																	class_2262.method_9696(commandContext, "destination"),
																	class_2252.method_9644(commandContext, "filter"),
																	class_3023.class_3025.field_13499
																)
														)
														.then(
															class_2170.method_9247("force")
																.executes(
																	commandContext -> method_13090(
																			commandContext.getSource(),
																			class_2262.method_9696(commandContext, "begin"),
																			class_2262.method_9696(commandContext, "end"),
																			class_2262.method_9696(commandContext, "destination"),
																			class_2252.method_9644(commandContext, "filter"),
																			class_3023.class_3025.field_13497
																		)
																)
														)
														.then(
															class_2170.method_9247("move")
																.executes(
																	commandContext -> method_13090(
																			commandContext.getSource(),
																			class_2262.method_9696(commandContext, "begin"),
																			class_2262.method_9696(commandContext, "end"),
																			class_2262.method_9696(commandContext, "destination"),
																			class_2252.method_9644(commandContext, "filter"),
																			class_3023.class_3025.field_13500
																		)
																)
														)
														.then(
															class_2170.method_9247("normal")
																.executes(
																	commandContext -> method_13090(
																			commandContext.getSource(),
																			class_2262.method_9696(commandContext, "begin"),
																			class_2262.method_9696(commandContext, "end"),
																			class_2262.method_9696(commandContext, "destination"),
																			class_2252.method_9644(commandContext, "filter"),
																			class_3023.class_3025.field_13499
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

	private static int method_13090(class_2168 arg, class_2338 arg2, class_2338 arg3, class_2338 arg4, Predicate<class_2694> predicate, class_3023.class_3025 arg5) throws CommandSyntaxException {
		class_3341 lv = new class_3341(arg2, arg3);
		class_2338 lv2 = arg4.method_10081(lv.method_14659());
		class_3341 lv3 = new class_3341(arg4, lv2);
		if (!arg5.method_13109() && lv3.method_14657(lv)) {
			throw field_13493.create();
		} else {
			int i = lv.method_14660() * lv.method_14663() * lv.method_14664();
			if (i > 32768) {
				throw field_13491.create(32768, i);
			} else {
				class_3218 lv4 = arg.method_9225();
				if (lv4.method_8617(arg2, arg3) && lv4.method_8617(arg4, lv2)) {
					List<class_3023.class_3024> list = Lists.<class_3023.class_3024>newArrayList();
					List<class_3023.class_3024> list2 = Lists.<class_3023.class_3024>newArrayList();
					List<class_3023.class_3024> list3 = Lists.<class_3023.class_3024>newArrayList();
					Deque<class_2338> deque = Lists.<class_2338>newLinkedList();
					class_2338 lv5 = new class_2338(lv3.field_14381 - lv.field_14381, lv3.field_14380 - lv.field_14380, lv3.field_14379 - lv.field_14379);

					for (int j = lv.field_14379; j <= lv.field_14376; j++) {
						for (int k = lv.field_14380; k <= lv.field_14377; k++) {
							for (int l = lv.field_14381; l <= lv.field_14378; l++) {
								class_2338 lv6 = new class_2338(l, k, j);
								class_2338 lv7 = lv6.method_10081(lv5);
								class_2694 lv8 = new class_2694(lv4, lv6, false);
								class_2680 lv9 = lv8.method_11681();
								if (predicate.test(lv8)) {
									class_2586 lv10 = lv4.method_8321(lv6);
									if (lv10 != null) {
										class_2487 lv11 = lv10.method_11007(new class_2487());
										list2.add(new class_3023.class_3024(lv7, lv9, lv11));
										deque.addLast(lv6);
									} else if (!lv9.method_11598(lv4, lv6) && !class_2248.method_9614(lv9.method_11628(lv4, lv6))) {
										list3.add(new class_3023.class_3024(lv7, lv9, null));
										deque.addFirst(lv6);
									} else {
										list.add(new class_3023.class_3024(lv7, lv9, null));
										deque.addLast(lv6);
									}
								}
							}
						}
					}

					if (arg5 == class_3023.class_3025.field_13500) {
						for (class_2338 lv12 : deque) {
							class_2586 lv13 = lv4.method_8321(lv12);
							class_3829.method_16825(lv13);
							lv4.method_8652(lv12, class_2246.field_10499.method_9564(), 2);
						}

						for (class_2338 lv12 : deque) {
							lv4.method_8652(lv12, class_2246.field_10124.method_9564(), 3);
						}
					}

					List<class_3023.class_3024> list4 = Lists.<class_3023.class_3024>newArrayList();
					list4.addAll(list);
					list4.addAll(list2);
					list4.addAll(list3);
					List<class_3023.class_3024> list5 = Lists.reverse(list4);

					for (class_3023.class_3024 lv14 : list5) {
						class_2586 lv15 = lv4.method_8321(lv14.field_13496);
						class_3829.method_16825(lv15);
						lv4.method_8652(lv14.field_13496, class_2246.field_10499.method_9564(), 2);
					}

					int lx = 0;

					for (class_3023.class_3024 lv16 : list4) {
						if (lv4.method_8652(lv16.field_13496, lv16.field_13495, 2)) {
							lx++;
						}
					}

					for (class_3023.class_3024 lv16x : list2) {
						class_2586 lv17 = lv4.method_8321(lv16x.field_13496);
						if (lv16x.field_13494 != null && lv17 != null) {
							lv16x.field_13494.method_10569("x", lv16x.field_13496.method_10263());
							lv16x.field_13494.method_10569("y", lv16x.field_13496.method_10264());
							lv16x.field_13494.method_10569("z", lv16x.field_13496.method_10260());
							lv17.method_11014(lv16x.field_13494);
							lv17.method_5431();
						}

						lv4.method_8652(lv16x.field_13496, lv16x.field_13495, 2);
					}

					for (class_3023.class_3024 lv16x : list5) {
						lv4.method_8408(lv16x.field_13496, lv16x.field_13495.method_11614());
					}

					lv4.method_14196().method_8666(lv, lv5);
					if (lx == 0) {
						throw field_13492.create();
					} else {
						arg.method_9226(new class_2588("commands.clone.success", lx), true);
						return lx;
					}
				} else {
					throw class_2262.field_10703.create();
				}
			}
		}
	}

	static class class_3024 {
		public final class_2338 field_13496;
		public final class_2680 field_13495;
		@Nullable
		public final class_2487 field_13494;

		public class_3024(class_2338 arg, class_2680 arg2, @Nullable class_2487 arg3) {
			this.field_13496 = arg;
			this.field_13495 = arg2;
			this.field_13494 = arg3;
		}
	}

	static enum class_3025 {
		field_13497(true),
		field_13500(true),
		field_13499(false);

		private final boolean field_13498;

		private class_3025(boolean bl) {
			this.field_13498 = bl;
		}

		public boolean method_13109() {
			return this.field_13498;
		}
	}
}
