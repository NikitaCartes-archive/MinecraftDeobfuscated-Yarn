package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_3119 {
	private static final SimpleCommandExceptionType field_13719 = new SimpleCommandExceptionType(new class_2588("commands.setblock.failed"));

	public static void method_13623(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("setblock")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("pos", class_2262.method_9698())
						.then(
							class_2170.method_9244("block", class_2257.method_9653())
								.executes(
									commandContext -> method_13620(
											commandContext.getSource(),
											class_2262.method_9696(commandContext, "pos"),
											class_2257.method_9655(commandContext, "block"),
											class_3119.class_3121.field_13722,
											null
										)
								)
								.then(
									class_2170.method_9247("destroy")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													class_2262.method_9696(commandContext, "pos"),
													class_2257.method_9655(commandContext, "block"),
													class_3119.class_3121.field_13721,
													null
												)
										)
								)
								.then(
									class_2170.method_9247("keep")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													class_2262.method_9696(commandContext, "pos"),
													class_2257.method_9655(commandContext, "block"),
													class_3119.class_3121.field_13722,
													arg -> arg.method_11679().method_8623(arg.method_11683())
												)
										)
								)
								.then(
									class_2170.method_9247("replace")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													class_2262.method_9696(commandContext, "pos"),
													class_2257.method_9655(commandContext, "block"),
													class_3119.class_3121.field_13722,
													null
												)
										)
								)
						)
				)
		);
	}

	private static int method_13620(class_2168 arg, class_2338 arg2, class_2247 arg3, class_3119.class_3121 arg4, @Nullable Predicate<class_2694> predicate) throws CommandSyntaxException {
		class_3218 lv = arg.method_9225();
		if (predicate != null && !predicate.test(new class_2694(lv, arg2, true))) {
			throw field_13719.create();
		} else {
			boolean bl;
			if (arg4 == class_3119.class_3121.field_13721) {
				lv.method_8651(arg2, true);
				bl = !arg3.method_9494().method_11588();
			} else {
				class_2586 lv2 = lv.method_8321(arg2);
				class_3829.method_16825(lv2);
				bl = true;
			}

			if (bl && !arg3.method_9495(lv, arg2, 2)) {
				throw field_13719.create();
			} else {
				lv.method_8408(arg2, arg3.method_9494().method_11614());
				arg.method_9226(new class_2588("commands.setblock.success", arg2.method_10263(), arg2.method_10264(), arg2.method_10260()), true);
				return 1;
			}
		}
	}

	public interface class_3120 {
		@Nullable
		class_2247 filter(class_3341 arg, class_2338 arg2, class_2247 arg3, class_3218 arg4);
	}

	public static enum class_3121 {
		field_13722,
		field_13721;
	}
}
