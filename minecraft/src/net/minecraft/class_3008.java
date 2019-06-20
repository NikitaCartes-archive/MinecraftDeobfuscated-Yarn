package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;

public class class_3008 {
	private static final SuggestionProvider<class_2168> field_13453 = (commandContext, suggestionsBuilder) -> {
		Collection<class_161> collection = commandContext.getSource().method_9211().method_3851().method_12893();
		return class_2172.method_9257(collection.stream().map(class_161::method_688), suggestionsBuilder);
	};

	public static void method_12980(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("advancement")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("grant")
						.then(
							class_2170.method_9244("targets", class_2186.method_9308())
								.then(
									class_2170.method_9247("only")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13457,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13464)
														)
												)
												.then(
													class_2170.method_9244("criterion", StringArgumentType.greedyString())
														.suggests(
															(commandContext, suggestionsBuilder) -> class_2172.method_9265(
																	class_2232.method_9439(commandContext, "advancement").method_682().keySet(), suggestionsBuilder
																)
														)
														.executes(
															commandContext -> method_12981(
																	commandContext.getSource(),
																	class_2186.method_9312(commandContext, "targets"),
																	class_3008.class_3009.field_13457,
																	class_2232.method_9439(commandContext, "advancement"),
																	StringArgumentType.getString(commandContext, "criterion")
																)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("from")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13457,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13458)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("until")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13457,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13465)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("through")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13457,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13462)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("everything")
										.executes(
											commandContext -> method_12988(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													class_3008.class_3009.field_13457,
													commandContext.getSource().method_9211().method_3851().method_12893()
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("revoke")
						.then(
							class_2170.method_9244("targets", class_2186.method_9308())
								.then(
									class_2170.method_9247("only")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13456,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13464)
														)
												)
												.then(
													class_2170.method_9244("criterion", StringArgumentType.greedyString())
														.suggests(
															(commandContext, suggestionsBuilder) -> class_2172.method_9265(
																	class_2232.method_9439(commandContext, "advancement").method_682().keySet(), suggestionsBuilder
																)
														)
														.executes(
															commandContext -> method_12981(
																	commandContext.getSource(),
																	class_2186.method_9312(commandContext, "targets"),
																	class_3008.class_3009.field_13456,
																	class_2232.method_9439(commandContext, "advancement"),
																	StringArgumentType.getString(commandContext, "criterion")
																)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("from")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13456,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13458)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("until")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13456,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13465)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("through")
										.then(
											class_2170.method_9244("advancement", class_2232.method_9441())
												.suggests(field_13453)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															class_3008.class_3009.field_13456,
															method_12996(class_2232.method_9439(commandContext, "advancement"), class_3008.class_3010.field_13462)
														)
												)
										)
								)
								.then(
									class_2170.method_9247("everything")
										.executes(
											commandContext -> method_12988(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													class_3008.class_3009.field_13456,
													commandContext.getSource().method_9211().method_3851().method_12893()
												)
										)
								)
						)
				)
		);
	}

	private static int method_12988(class_2168 arg, Collection<class_3222> collection, class_3008.class_3009 arg2, Collection<class_161> collection2) {
		int i = 0;

		for (class_3222 lv : collection) {
			i += arg2.method_12999(lv, collection2);
		}

		if (i == 0) {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					throw new class_2164(
						new class_2588(
							arg2.method_13001() + ".one.to.one.failure",
							((class_161)collection2.iterator().next()).method_684(),
							((class_3222)collection.iterator().next()).method_5476()
						)
					);
				} else {
					throw new class_2164(
						new class_2588(arg2.method_13001() + ".one.to.many.failure", ((class_161)collection2.iterator().next()).method_684(), collection.size())
					);
				}
			} else if (collection.size() == 1) {
				throw new class_2164(
					new class_2588(arg2.method_13001() + ".many.to.one.failure", collection2.size(), ((class_3222)collection.iterator().next()).method_5476())
				);
			} else {
				throw new class_2164(new class_2588(arg2.method_13001() + ".many.to.many.failure", collection2.size(), collection.size()));
			}
		} else {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					arg.method_9226(
						new class_2588(
							arg2.method_13001() + ".one.to.one.success",
							((class_161)collection2.iterator().next()).method_684(),
							((class_3222)collection.iterator().next()).method_5476()
						),
						true
					);
				} else {
					arg.method_9226(
						new class_2588(arg2.method_13001() + ".one.to.many.success", ((class_161)collection2.iterator().next()).method_684(), collection.size()), true
					);
				}
			} else if (collection.size() == 1) {
				arg.method_9226(
					new class_2588(arg2.method_13001() + ".many.to.one.success", collection2.size(), ((class_3222)collection.iterator().next()).method_5476()), true
				);
			} else {
				arg.method_9226(new class_2588(arg2.method_13001() + ".many.to.many.success", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int method_12981(class_2168 arg, Collection<class_3222> collection, class_3008.class_3009 arg2, class_161 arg3, String string) {
		int i = 0;
		if (!arg3.method_682().containsKey(string)) {
			throw new class_2164(new class_2588("commands.advancement.criterionNotFound", arg3.method_684(), string));
		} else {
			for (class_3222 lv : collection) {
				if (arg2.method_13000(lv, arg3, string)) {
					i++;
				}
			}

			if (i == 0) {
				if (collection.size() == 1) {
					throw new class_2164(
						new class_2588(arg2.method_13001() + ".criterion.to.one.failure", string, arg3.method_684(), ((class_3222)collection.iterator().next()).method_5476())
					);
				} else {
					throw new class_2164(new class_2588(arg2.method_13001() + ".criterion.to.many.failure", string, arg3.method_684(), collection.size()));
				}
			} else {
				if (collection.size() == 1) {
					arg.method_9226(
						new class_2588(arg2.method_13001() + ".criterion.to.one.success", string, arg3.method_684(), ((class_3222)collection.iterator().next()).method_5476()),
						true
					);
				} else {
					arg.method_9226(new class_2588(arg2.method_13001() + ".criterion.to.many.success", string, arg3.method_684(), collection.size()), true);
				}

				return i;
			}
		}
	}

	private static List<class_161> method_12996(class_161 arg, class_3008.class_3010 arg2) {
		List<class_161> list = Lists.<class_161>newArrayList();
		if (arg2.field_13460) {
			for (class_161 lv = arg.method_687(); lv != null; lv = lv.method_687()) {
				list.add(lv);
			}
		}

		list.add(arg);
		if (arg2.field_13459) {
			method_12990(arg, list);
		}

		return list;
	}

	private static void method_12990(class_161 arg, List<class_161> list) {
		for (class_161 lv : arg.method_681()) {
			list.add(lv);
			method_12990(lv, list);
		}
	}

	static enum class_3009 {
		field_13457("grant") {
			@Override
			protected boolean method_13002(class_3222 arg, class_161 arg2) {
				class_167 lv = arg.method_14236().method_12882(arg2);
				if (lv.method_740()) {
					return false;
				} else {
					for (String string : lv.method_731()) {
						arg.method_14236().method_12878(arg2, string);
					}

					return true;
				}
			}

			@Override
			protected boolean method_13000(class_3222 arg, class_161 arg2, String string) {
				return arg.method_14236().method_12878(arg2, string);
			}
		},
		field_13456("revoke") {
			@Override
			protected boolean method_13002(class_3222 arg, class_161 arg2) {
				class_167 lv = arg.method_14236().method_12882(arg2);
				if (!lv.method_742()) {
					return false;
				} else {
					for (String string : lv.method_734()) {
						arg.method_14236().method_12883(arg2, string);
					}

					return true;
				}
			}

			@Override
			protected boolean method_13000(class_3222 arg, class_161 arg2, String string) {
				return arg.method_14236().method_12883(arg2, string);
			}
		};

		private final String field_13454;

		private class_3009(String string2) {
			this.field_13454 = "commands.advancement." + string2;
		}

		public int method_12999(class_3222 arg, Iterable<class_161> iterable) {
			int i = 0;

			for (class_161 lv : iterable) {
				if (this.method_13002(arg, lv)) {
					i++;
				}
			}

			return i;
		}

		protected abstract boolean method_13002(class_3222 arg, class_161 arg2);

		protected abstract boolean method_13000(class_3222 arg, class_161 arg2, String string);

		protected String method_13001() {
			return this.field_13454;
		}
	}

	static enum class_3010 {
		field_13464(false, false),
		field_13462(true, true),
		field_13458(false, true),
		field_13465(true, false),
		field_13461(true, true);

		private final boolean field_13460;
		private final boolean field_13459;

		private class_3010(boolean bl, boolean bl2) {
			this.field_13460 = bl;
			this.field_13459 = bl2;
		}
	}
}
