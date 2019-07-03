package net.minecraft.server.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
	private static final Logger field_20283 = LogManager.getLogger();
	private static final SimpleCommandExceptionType NORUNNING_EXCPETION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.notRunning"));
	private static final SimpleCommandExceptionType ALREADYRUNNING_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.debug.alreadyRunning")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("debug")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(CommandManager.literal("start").executes(commandContext -> executeStart(commandContext.getSource())))
				.then(CommandManager.literal("stop").executes(commandContext -> executeStop(commandContext.getSource())))
				.then(CommandManager.literal("report").executes(commandContext -> method_21618(commandContext.getSource())))
		);
	}

	private static int executeStart(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		DisableableProfiler disableableProfiler = minecraftServer.getProfiler();
		if (disableableProfiler.getController().isEnabled()) {
			throw ALREADYRUNNING_EXCEPTION.create();
		} else {
			minecraftServer.enableProfiler();
			serverCommandSource.sendFeedback(new TranslatableText("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
			return 0;
		}
	}

	private static int executeStop(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		DisableableProfiler disableableProfiler = minecraftServer.getProfiler();
		if (!disableableProfiler.getController().isEnabled()) {
			throw NORUNNING_EXCPETION.create();
		} else {
			ProfileResult profileResult = disableableProfiler.getController().disable();
			File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
			profileResult.saveToFile(file);
			float f = (float)profileResult.getTimeSpan() / 1.0E9F;
			float g = (float)profileResult.getTickSpan() / f;
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), profileResult.getTickSpan(), String.format("%.2f", g)), true
			);
			return MathHelper.floor(g);
		}
	}

	private static int method_21618(ServerCommandSource serverCommandSource) {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		String string = "debug-report-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());

		try {
			Path path = minecraftServer.getFile("debug").toPath();
			Files.createDirectories(path);
			if (SharedConstants.isDevelopment) {
				Path path2 = path.resolve(string);
				minecraftServer.method_21613(path2);
			} else {
				Path path2 = path.resolve(string + ".zip");
				URI uRI = new URI("jar", path2.toUri().toString(), null);
				FileSystem fileSystem = FileSystems.newFileSystem(uRI, ImmutableMap.of("create", "true"));
				Throwable var7 = null;

				try {
					minecraftServer.method_21613(fileSystem.getPath("/"));
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (fileSystem != null) {
						if (var7 != null) {
							try {
								fileSystem.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							fileSystem.close();
						}
					}
				}
			}

			serverCommandSource.sendFeedback(new TranslatableText("commands.debug.reportSaved", string), false);
			return 1;
		} catch (URISyntaxException | IOException var19) {
			field_20283.error("Failed to save debug dump", (Throwable)var19);
			serverCommandSource.sendError(new TranslatableText("commands.debug.reportFailed"));
			return 0;
		}
	}
}
