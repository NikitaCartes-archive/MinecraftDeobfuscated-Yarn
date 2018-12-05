package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.class_3689;
import net.minecraft.class_3696;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;

public class DebugCommand {
	private static final SimpleCommandExceptionType NORUNNING_EXCPETION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.debug.notRunning")
	);
	private static final SimpleCommandExceptionType ALREADYRUNNING_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.debug.alreadyRunning")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("debug")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(ServerCommandManager.literal("start").executes(commandContext -> method_13159(commandContext.getSource())))
				.then(ServerCommandManager.literal("stop").executes(commandContext -> method_13158(commandContext.getSource())))
		);
	}

	private static int method_13159(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		class_3689 lv = minecraftServer.getProfiler();
		if (lv.method_16055().method_16057()) {
			throw ALREADYRUNNING_EXCEPTION.create();
		} else {
			minecraftServer.method_3832();
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
			return 0;
		}
	}

	private static int method_13158(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		class_3689 lv = minecraftServer.getProfiler();
		if (!lv.method_16055().method_16057()) {
			throw NORUNNING_EXCPETION.create();
		} else {
			class_3696 lv2 = lv.method_16055().method_16058();
			File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
			lv2.method_16069(file);
			float f = (float)lv2.method_16071() / 1.0E9F;
			float g = (float)lv2.method_16074() / f;
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), lv2.method_16074(), String.format("%.2f", g)), true
			);
			return MathHelper.floor(g);
		}
	}
}
