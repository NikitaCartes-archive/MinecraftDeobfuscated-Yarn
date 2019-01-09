package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Set;

public class class_3140 {
	private static final SimpleCommandExceptionType field_13742 = new SimpleCommandExceptionType(new class_2588("commands.tag.add.failed"));
	private static final SimpleCommandExceptionType field_13743 = new SimpleCommandExceptionType(new class_2588("commands.tag.remove.failed"));

	public static void method_13698(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("tag")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("targets", class_2186.method_9306())
						.then(
							class_2170.method_9247("add")
								.then(
									class_2170.method_9244("name", StringArgumentType.word())
										.executes(
											commandContext -> method_13702(
													commandContext.getSource(), class_2186.method_9317(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
												)
										)
								)
						)
						.then(
							class_2170.method_9247("remove")
								.then(
									class_2170.method_9244("name", StringArgumentType.word())
										.suggests(
											(commandContext, suggestionsBuilder) -> class_2172.method_9265(method_13706(class_2186.method_9317(commandContext, "targets")), suggestionsBuilder)
										)
										.executes(
											commandContext -> method_13699(
													commandContext.getSource(), class_2186.method_9317(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
												)
										)
								)
						)
						.then(
							class_2170.method_9247("list").executes(commandContext -> method_13700(commandContext.getSource(), class_2186.method_9317(commandContext, "targets")))
						)
				)
		);
	}

	private static Collection<String> method_13706(Collection<? extends class_1297> collection) {
		Set<String> set = Sets.<String>newHashSet();

		for (class_1297 lv : collection) {
			set.addAll(lv.method_5752());
		}

		return set;
	}

	private static int method_13702(class_2168 arg, Collection<? extends class_1297> collection, String string) throws CommandSyntaxException {
		int i = 0;

		for (class_1297 lv : collection) {
			if (lv.method_5780(string)) {
				i++;
			}
		}

		if (i == 0) {
			throw field_13742.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.tag.add.success.single", string, ((class_1297)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.tag.add.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13699(class_2168 arg, Collection<? extends class_1297> collection, String string) throws CommandSyntaxException {
		int i = 0;

		for (class_1297 lv : collection) {
			if (lv.method_5738(string)) {
				i++;
			}
		}

		if (i == 0) {
			throw field_13743.create();
		} else {
			if (collection.size() == 1) {
				arg.method_9226(new class_2588("commands.tag.remove.success.single", string, ((class_1297)collection.iterator().next()).method_5476()), true);
			} else {
				arg.method_9226(new class_2588("commands.tag.remove.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13700(class_2168 arg, Collection<? extends class_1297> collection) {
		Set<String> set = Sets.<String>newHashSet();

		for (class_1297 lv : collection) {
			set.addAll(lv.method_5752());
		}

		if (collection.size() == 1) {
			class_1297 lv2 = (class_1297)collection.iterator().next();
			if (set.isEmpty()) {
				arg.method_9226(new class_2588("commands.tag.list.single.empty", lv2.method_5476()), false);
			} else {
				arg.method_9226(new class_2588("commands.tag.list.single.success", lv2.method_5476(), set.size(), class_2564.method_10888(set)), false);
			}
		} else if (set.isEmpty()) {
			arg.method_9226(new class_2588("commands.tag.list.multiple.empty", collection.size()), false);
		} else {
			arg.method_9226(new class_2588("commands.tag.list.multiple.success", collection.size(), set.size(), class_2564.method_10888(set)), false);
		}

		return set.size();
	}
}
