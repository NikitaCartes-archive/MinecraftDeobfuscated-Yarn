package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class class_3054 {
	private static final SimpleCommandExceptionType field_13638 = new SimpleCommandExceptionType(new class_2588("commands.experience.set.points.invalid"));

	public static void method_13330(CommandDispatcher<class_2168> commandDispatcher) {
		LiteralCommandNode<class_2168> literalCommandNode = commandDispatcher.register(
			class_2170.method_9247("experience")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("targets", class_2186.method_9308())
								.then(
									class_2170.method_9244("amount", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_13326(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "amount"),
													class_3054.class_3055.field_13644
												)
										)
										.then(
											class_2170.method_9247("points")
												.executes(
													commandContext -> method_13326(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															class_3054.class_3055.field_13644
														)
												)
										)
										.then(
											class_2170.method_9247("levels")
												.executes(
													commandContext -> method_13326(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															class_3054.class_3055.field_13641
														)
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("set")
						.then(
							class_2170.method_9244("targets", class_2186.method_9308())
								.then(
									class_2170.method_9244("amount", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13333(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "amount"),
													class_3054.class_3055.field_13644
												)
										)
										.then(
											class_2170.method_9247("points")
												.executes(
													commandContext -> method_13333(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															class_3054.class_3055.field_13644
														)
												)
										)
										.then(
											class_2170.method_9247("levels")
												.executes(
													commandContext -> method_13333(
															commandContext.getSource(),
															class_2186.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															class_3054.class_3055.field_13641
														)
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("query")
						.then(
							class_2170.method_9244("targets", class_2186.method_9305())
								.then(
									class_2170.method_9247("points")
										.executes(
											commandContext -> method_13328(commandContext.getSource(), class_2186.method_9315(commandContext, "targets"), class_3054.class_3055.field_13644)
										)
								)
								.then(
									class_2170.method_9247("levels")
										.executes(
											commandContext -> method_13328(commandContext.getSource(), class_2186.method_9315(commandContext, "targets"), class_3054.class_3055.field_13641)
										)
								)
						)
				)
		);
		commandDispatcher.register(class_2170.method_9247("xp").requires(arg -> arg.method_9259(2)).redirect(literalCommandNode));
	}

	private static int method_13328(class_2168 arg, class_3222 arg2, class_3054.class_3055 arg3) {
		int i = arg3.field_13645.applyAsInt(arg2);
		arg.method_9226(new class_2588("commands.experience.query." + arg3.field_13643, arg2.method_5476(), i), false);
		return i;
	}

	private static int method_13326(class_2168 arg, Collection<? extends class_3222> collection, int i, class_3054.class_3055 arg2) {
		for (class_3222 lv : collection) {
			arg2.field_13639.accept(lv, i);
		}

		if (collection.size() == 1) {
			arg.method_9226(
				new class_2588("commands.experience.add." + arg2.field_13643 + ".success.single", i, ((class_3222)collection.iterator().next()).method_5476()), true
			);
		} else {
			arg.method_9226(new class_2588("commands.experience.add." + arg2.field_13643 + ".success.multiple", i, collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13333(class_2168 arg, Collection<? extends class_3222> collection, int i, class_3054.class_3055 arg2) throws CommandSyntaxException {
		int j = 0;

		for (class_3222 lv : collection) {
			if (arg2.field_13642.test(lv, i)) {
				j++;
			}
		}

		if (j == 0) {
			throw field_13638.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(
					new class_2588("commands.experience.set." + arg2.field_13643 + ".success.single", i, ((class_3222)collection.iterator().next()).method_5476()), true
				);
			} else {
				arg.method_9226(new class_2588("commands.experience.set." + arg2.field_13643 + ".success.multiple", i, collection.size()), true);
			}

			return collection.size();
		}
	}

	static enum class_3055 {
		field_13644("points", class_1657::method_7255, (arg, integer) -> {
			if (integer >= arg.method_7349()) {
				return false;
			} else {
				arg.method_14228(integer);
				return true;
			}
		}, arg -> class_3532.method_15375(arg.field_7510 * (float)arg.method_7349())),
		field_13641("levels", class_3222::method_7316, (arg, integer) -> {
			arg.method_14252(integer);
			return true;
		}, arg -> arg.field_7520);

		public final BiConsumer<class_3222, Integer> field_13639;
		public final BiPredicate<class_3222, Integer> field_13642;
		public final String field_13643;
		private final ToIntFunction<class_3222> field_13645;

		private class_3055(
			String string2, BiConsumer<class_3222, Integer> biConsumer, BiPredicate<class_3222, Integer> biPredicate, ToIntFunction<class_3222> toIntFunction
		) {
			this.field_13639 = biConsumer;
			this.field_13643 = string2;
			this.field_13642 = biPredicate;
			this.field_13645 = toIntFunction;
		}
	}
}
