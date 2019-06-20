package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;

public class class_3151 {
	public static void method_13804(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("title")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9308())
						.then(
							class_2170.method_9247("clear").executes(commandContext -> method_13805(commandContext.getSource(), class_2186.method_9312(commandContext, "targets")))
						)
						.then(
							class_2170.method_9247("reset").executes(commandContext -> method_13799(commandContext.getSource(), class_2186.method_9312(commandContext, "targets")))
						)
						.then(
							class_2170.method_9247("title")
								.then(
									class_2170.method_9244("title", class_2178.method_9281())
										.executes(
											commandContext -> method_13802(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													class_2178.method_9280(commandContext, "title"),
													class_2762.class_2763.field_12630
												)
										)
								)
						)
						.then(
							class_2170.method_9247("subtitle")
								.then(
									class_2170.method_9244("title", class_2178.method_9281())
										.executes(
											commandContext -> method_13802(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													class_2178.method_9280(commandContext, "title"),
													class_2762.class_2763.field_12632
												)
										)
								)
						)
						.then(
							class_2170.method_9247("actionbar")
								.then(
									class_2170.method_9244("title", class_2178.method_9281())
										.executes(
											commandContext -> method_13802(
													commandContext.getSource(),
													class_2186.method_9312(commandContext, "targets"),
													class_2178.method_9280(commandContext, "title"),
													class_2762.class_2763.field_12627
												)
										)
								)
						)
						.then(
							class_2170.method_9247("times")
								.then(
									class_2170.method_9244("fadeIn", IntegerArgumentType.integer(0))
										.then(
											class_2170.method_9244("stay", IntegerArgumentType.integer(0))
												.then(
													class_2170.method_9244("fadeOut", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13806(
																	commandContext.getSource(),
																	class_2186.method_9312(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "fadeIn"),
																	IntegerArgumentType.getInteger(commandContext, "stay"),
																	IntegerArgumentType.getInteger(commandContext, "fadeOut")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13805(class_2168 arg, Collection<class_3222> collection) {
		class_2762 lv = new class_2762(class_2762.class_2763.field_12633, null);

		for (class_3222 lv2 : collection) {
			lv2.field_13987.method_14364(lv);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.title.cleared.single", ((class_3222)collection.iterator().next()).method_5476()), true);
		} else {
			arg.method_9226(new class_2588("commands.title.cleared.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13799(class_2168 arg, Collection<class_3222> collection) {
		class_2762 lv = new class_2762(class_2762.class_2763.field_12628, null);

		for (class_3222 lv2 : collection) {
			lv2.field_13987.method_14364(lv);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.title.reset.single", ((class_3222)collection.iterator().next()).method_5476()), true);
		} else {
			arg.method_9226(new class_2588("commands.title.reset.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13802(class_2168 arg, Collection<class_3222> collection, class_2561 arg2, class_2762.class_2763 arg3) throws CommandSyntaxException {
		for (class_3222 lv : collection) {
			lv.field_13987.method_14364(new class_2762(arg3, class_2564.method_10881(arg, arg2, lv, 0)));
		}

		if (collection.size() == 1) {
			arg.method_9226(
				new class_2588("commands.title.show." + arg3.name().toLowerCase(Locale.ROOT) + ".single", ((class_3222)collection.iterator().next()).method_5476()), true
			);
		} else {
			arg.method_9226(new class_2588("commands.title.show." + arg3.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13806(class_2168 arg, Collection<class_3222> collection, int i, int j, int k) {
		class_2762 lv = new class_2762(i, j, k);

		for (class_3222 lv2 : collection) {
			lv2.field_13987.method_14364(lv);
		}

		if (collection.size() == 1) {
			arg.method_9226(new class_2588("commands.title.times.single", ((class_3222)collection.iterator().next()).method_5476()), true);
		} else {
			arg.method_9226(new class_2588("commands.title.times.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
