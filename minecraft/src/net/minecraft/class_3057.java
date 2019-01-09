package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_3057 {
	private static final Dynamic2CommandExceptionType field_13649 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("commands.fill.toobig", object, object2)
	);
	private static final class_2247 field_13648 = new class_2247(class_2246.field_10124.method_9564(), Collections.emptySet(), null);
	private static final SimpleCommandExceptionType field_13650 = new SimpleCommandExceptionType(new class_2588("commands.fill.failed"));

	public static void method_13347(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("fill")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("from", class_2262.method_9698())
						.then(
							class_2170.method_9244("to", class_2262.method_9698())
								.then(
									class_2170.method_9244("block", class_2257.method_9653())
										.executes(
											commandContext -> method_13354(
													commandContext.getSource(),
													new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
													class_2257.method_9655(commandContext, "block"),
													class_3057.class_3058.field_13655,
													null
												)
										)
										.then(
											class_2170.method_9247("replace")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
															class_2257.method_9655(commandContext, "block"),
															class_3057.class_3058.field_13655,
															null
														)
												)
												.then(
													class_2170.method_9244("filter", class_2252.method_9645())
														.executes(
															commandContext -> method_13354(
																	commandContext.getSource(),
																	new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
																	class_2257.method_9655(commandContext, "block"),
																	class_3057.class_3058.field_13655,
																	class_2252.method_9644(commandContext, "filter")
																)
														)
												)
										)
										.then(
											class_2170.method_9247("keep")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
															class_2257.method_9655(commandContext, "block"),
															class_3057.class_3058.field_13655,
															arg -> arg.method_11679().method_8623(arg.method_11683())
														)
												)
										)
										.then(
											class_2170.method_9247("outline")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
															class_2257.method_9655(commandContext, "block"),
															class_3057.class_3058.field_13652,
															null
														)
												)
										)
										.then(
											class_2170.method_9247("hollow")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
															class_2257.method_9655(commandContext, "block"),
															class_3057.class_3058.field_13656,
															null
														)
												)
										)
										.then(
											class_2170.method_9247("destroy")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new class_3341(class_2262.method_9696(commandContext, "from"), class_2262.method_9696(commandContext, "to")),
															class_2257.method_9655(commandContext, "block"),
															class_3057.class_3058.field_13651,
															null
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13354(class_2168 arg, class_3341 arg2, class_2247 arg3, class_3057.class_3058 arg4, @Nullable Predicate<class_2694> predicate) throws CommandSyntaxException {
		int i = arg2.method_14660() * arg2.method_14663() * arg2.method_14664();
		if (i > 32768) {
			throw field_13649.create(32768, i);
		} else {
			List<class_2338> list = Lists.<class_2338>newArrayList();
			class_3218 lv = arg.method_9225();
			int j = 0;

			for (class_2338 lv2 : class_2338.class_2339.method_10094(
				arg2.field_14381, arg2.field_14380, arg2.field_14379, arg2.field_14378, arg2.field_14377, arg2.field_14376
			)) {
				if (predicate == null || predicate.test(new class_2694(lv, lv2, true))) {
					class_2247 lv3 = arg4.field_13654.filter(arg2, lv2, arg3, lv);
					if (lv3 != null) {
						class_2586 lv4 = lv.method_8321(lv2);
						class_3829.method_16825(lv4);
						if (lv3.method_9495(lv, lv2, 2)) {
							list.add(lv2.method_10062());
							j++;
						}
					}
				}
			}

			for (class_2338 lv2x : list) {
				class_2248 lv5 = lv.method_8320(lv2x).method_11614();
				lv.method_8408(lv2x, lv5);
			}

			if (j == 0) {
				throw field_13650.create();
			} else {
				arg.method_9226(new class_2588("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum class_3058 {
		field_13655((arg, arg2, arg3, arg4) -> arg3),
		field_13652(
			(arg, arg2, arg3, arg4) -> arg2.method_10263() != arg.field_14381
						&& arg2.method_10263() != arg.field_14378
						&& arg2.method_10264() != arg.field_14380
						&& arg2.method_10264() != arg.field_14377
						&& arg2.method_10260() != arg.field_14379
						&& arg2.method_10260() != arg.field_14376
					? null
					: arg3
		),
		field_13656(
			(arg, arg2, arg3, arg4) -> arg2.method_10263() != arg.field_14381
						&& arg2.method_10263() != arg.field_14378
						&& arg2.method_10264() != arg.field_14380
						&& arg2.method_10264() != arg.field_14377
						&& arg2.method_10260() != arg.field_14379
						&& arg2.method_10260() != arg.field_14376
					? class_3057.field_13648
					: arg3
		),
		field_13651((arg, arg2, arg3, arg4) -> {
			arg4.method_8651(arg2, true);
			return arg3;
		});

		public final class_3119.class_3120 field_13654;

		private class_3058(class_3119.class_3120 arg) {
			this.field_13654 = arg;
		}
	}
}
