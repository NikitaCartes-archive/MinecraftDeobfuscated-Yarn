package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;

public class class_3032 {
	private static final SimpleCommandExceptionType field_13597 = new SimpleCommandExceptionType(new class_2588("commands.debug.notRunning"));
	private static final SimpleCommandExceptionType field_13596 = new SimpleCommandExceptionType(new class_2588("commands.debug.alreadyRunning"));

	public static void method_13156(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("debug")
				.requires(arg -> arg.method_9259(3))
				.then(class_2170.method_9247("start").executes(commandContext -> method_13159(commandContext.getSource())))
				.then(class_2170.method_9247("stop").executes(commandContext -> method_13158(commandContext.getSource())))
		);
	}

	private static int method_13159(class_2168 arg) throws CommandSyntaxException {
		MinecraftServer minecraftServer = arg.method_9211();
		class_3689 lv = minecraftServer.method_16044();
		if (lv.method_16055().method_16057()) {
			throw field_13596.create();
		} else {
			minecraftServer.method_3832();
			arg.method_9226(new class_2588("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
			return 0;
		}
	}

	private static int method_13158(class_2168 arg) throws CommandSyntaxException {
		MinecraftServer minecraftServer = arg.method_9211();
		class_3689 lv = minecraftServer.method_16044();
		if (!lv.method_16055().method_16057()) {
			throw field_13597.create();
		} else {
			class_3696 lv2 = lv.method_16055().method_16058();
			File file = new File(minecraftServer.method_3758("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
			lv2.method_16069(file);
			float f = (float)lv2.method_16071() / 1.0E9F;
			float g = (float)lv2.method_16074() / f;
			arg.method_9226(new class_2588("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), lv2.method_16074(), String.format("%.2f", g)), true);
			return class_3532.method_15375(g);
		}
	}
}
