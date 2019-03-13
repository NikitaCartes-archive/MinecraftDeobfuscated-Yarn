package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.ProfileResult;

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
		DisableableProfiler disableableProfiler = minecraftServer.getProfiler();
		if (disableableProfiler.getController().isEnabled()) {
			throw ALREADYRUNNING_EXCEPTION.create();
		} else {
			minecraftServer.method_3832();
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
			return 0;
		}
	}

	private static int method_13158(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		DisableableProfiler disableableProfiler = minecraftServer.getProfiler();
		if (!disableableProfiler.getController().isEnabled()) {
			throw NORUNNING_EXCPETION.create();
		} else {
			ProfileResult profileResult = disableableProfiler.getController().method_16058();
			File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
			profileResult.saveToFile(file);
			float f = (float)profileResult.getTimeSpan() / 1.0E9F;
			float g = (float)profileResult.getTickSpan() / f;
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), profileResult.getTickSpan(), String.format("%.2f", g)), true
			);
			return MathHelper.floor(g);
		}
	}
}
