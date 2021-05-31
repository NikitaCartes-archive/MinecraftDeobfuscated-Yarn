package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.util.profiler.ProfilerDumper;
import net.minecraft.entity.ai.Durations;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ZipCompressor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_6413 {
	private static final Logger field_33985 = LogManager.getLogger();
	private static final SimpleCommandExceptionType field_33986 = new SimpleCommandExceptionType(new TranslatableText("commands.perf.notRunning"));
	private static final SimpleCommandExceptionType field_33987 = new SimpleCommandExceptionType(new TranslatableText("commands.perf.alreadyRunning"));

	public static void method_37331(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("perf")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
				.then(CommandManager.literal("start").executes(commandContext -> method_37333(commandContext.getSource())))
				.then(CommandManager.literal("stop").executes(commandContext -> method_37338(commandContext.getSource())))
		);
	}

	private static int method_37333(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		if (minecraftServer.method_37321()) {
			throw field_33987.create();
		} else {
			Consumer<ProfileResult> consumer = profileResult -> method_37334(serverCommandSource, profileResult);
			Consumer<Path> consumer2 = path -> method_37335(serverCommandSource, path, minecraftServer);
			minecraftServer.method_37320(consumer, consumer2);
			serverCommandSource.sendFeedback(new TranslatableText("commands.perf.started"), false);
			return 0;
		}
	}

	private static int method_37338(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		if (!minecraftServer.method_37321()) {
			throw field_33986.create();
		} else {
			minecraftServer.method_37323();
			return 0;
		}
	}

	private static void method_37335(ServerCommandSource serverCommandSource, Path path, MinecraftServer minecraftServer) {
		String string = String.format(
			"%s-%s-%s",
			new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()),
			minecraftServer.getSaveProperties().getLevelName(),
			SharedConstants.getGameVersion().getId()
		);

		String string2;
		try {
			string2 = FileNameUtil.getNextUniqueName(ProfilerDumper.DEBUG_PROFILING_DIRECTORY, string, ".zip");
		} catch (IOException var11) {
			serverCommandSource.sendError(new TranslatableText("commands.perf.reportFailed"));
			field_33985.error(var11);
			return;
		}

		ZipCompressor zipCompressor = new ZipCompressor(ProfilerDumper.DEBUG_PROFILING_DIRECTORY.resolve(string2));

		try {
			zipCompressor.write(Paths.get("system.txt"), minecraftServer.method_37324(new SystemDetails()).collect());
			zipCompressor.copyAll(path);
		} catch (Throwable var10) {
			try {
				zipCompressor.close();
			} catch (Throwable var8) {
				var10.addSuppressed(var8);
			}

			throw var10;
		}

		zipCompressor.close();

		try {
			FileUtils.forceDelete(path.toFile());
		} catch (IOException var9) {
			field_33985.warn("Failed to delete temporary profiling file {}", path, var9);
		}

		serverCommandSource.sendFeedback(new TranslatableText("commands.perf.reportSaved", string2), false);
	}

	private static void method_37334(ServerCommandSource serverCommandSource, ProfileResult profileResult) {
		int i = profileResult.getTickSpan();
		double d = (double)profileResult.getTimeSpan() / (double)Durations.field_33868;
		serverCommandSource.sendFeedback(
			new TranslatableText("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d)), false
		);
	}
}
