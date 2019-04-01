package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class class_3079 {
	private static final SimpleCommandExceptionType field_13666 = new SimpleCommandExceptionType(new class_2588("commands.locate.failed"));

	public static void method_13443(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("locate")
				.requires(arg -> arg.method_9259(2))
				.then(class_2170.method_9247("Pillager_Outpost").executes(commandContext -> method_13457(commandContext.getSource(), "Pillager_Outpost")))
				.then(class_2170.method_9247("Mineshaft").executes(commandContext -> method_13457(commandContext.getSource(), "Mineshaft")))
				.then(class_2170.method_9247("Mansion").executes(commandContext -> method_13457(commandContext.getSource(), "Mansion")))
				.then(class_2170.method_9247("Igloo").executes(commandContext -> method_13457(commandContext.getSource(), "Igloo")))
				.then(class_2170.method_9247("Desert_Pyramid").executes(commandContext -> method_13457(commandContext.getSource(), "Desert_Pyramid")))
				.then(class_2170.method_9247("Jungle_Pyramid").executes(commandContext -> method_13457(commandContext.getSource(), "Jungle_Pyramid")))
				.then(class_2170.method_9247("Swamp_Hut").executes(commandContext -> method_13457(commandContext.getSource(), "Swamp_Hut")))
				.then(class_2170.method_9247("Stronghold").executes(commandContext -> method_13457(commandContext.getSource(), "Stronghold")))
				.then(class_2170.method_9247("Monument").executes(commandContext -> method_13457(commandContext.getSource(), "Monument")))
				.then(class_2170.method_9247("Fortress").executes(commandContext -> method_13457(commandContext.getSource(), "Fortress")))
				.then(class_2170.method_9247("EndCity").executes(commandContext -> method_13457(commandContext.getSource(), "EndCity")))
				.then(class_2170.method_9247("Ocean_Ruin").executes(commandContext -> method_13457(commandContext.getSource(), "Ocean_Ruin")))
				.then(class_2170.method_9247("Buried_Treasure").executes(commandContext -> method_13457(commandContext.getSource(), "Buried_Treasure")))
				.then(class_2170.method_9247("Shipwreck").executes(commandContext -> method_13457(commandContext.getSource(), "Shipwreck")))
				.then(class_2170.method_9247("Village").executes(commandContext -> method_13457(commandContext.getSource(), "Village")))
		);
	}

	private static int method_13457(class_2168 arg, String string) throws CommandSyntaxException {
		class_2338 lv = new class_2338(arg.method_9222());
		class_2338 lv2 = arg.method_9225().method_8487(string, lv, 100, false);
		if (lv2 == null) {
			throw field_13666.create();
		} else {
			int i = class_3532.method_15375(method_13439(lv.method_10263(), lv.method_10260(), lv2.method_10263(), lv2.method_10260()));
			class_2561 lv3 = class_2564.method_10885(new class_2588("chat.coordinates", lv2.method_10263(), "~", lv2.method_10260()))
				.method_10859(
					arg2 -> arg2.method_10977(class_124.field_1060)
							.method_10958(new class_2558(class_2558.class_2559.field_11745, "/tp @s " + lv2.method_10263() + " ~ " + lv2.method_10260()))
							.method_10949(new class_2568(class_2568.class_2569.field_11762, new class_2588("chat.coordinates.tooltip")))
				);
			arg.method_9226(new class_2588("commands.locate.success", string, lv3, i), false);
			return i;
		}
	}

	private static float method_13439(int i, int j, int k, int l) {
		int m = k - i;
		int n = l - j;
		return class_3532.method_15355((float)(m * m + n * n));
	}
}
