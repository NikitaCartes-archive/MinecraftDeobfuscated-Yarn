/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DebugCommand {
    private static final Logger logger = LogManager.getLogger();
    private static final SimpleCommandExceptionType NORUNNING_EXCPETION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.notRunning", new Object[0]));
    private static final SimpleCommandExceptionType ALREADYRUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.alreadyRunning", new Object[0]));
    @Nullable
    private static final FileSystemProvider field_20310 = FileSystemProvider.installedProviders().stream().filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar")).findFirst().orElse(null);

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.literal("start").executes(commandContext -> DebugCommand.executeStart((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("stop").executes(commandContext -> DebugCommand.executeStop((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("report").executes(commandContext -> DebugCommand.method_21618((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeStart(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        DisableableProfiler disableableProfiler = minecraftServer.getProfiler();
        if (disableableProfiler.getController().isEnabled()) {
            throw ALREADYRUNNING_EXCEPTION.create();
        }
        minecraftServer.enableProfiler();
        serverCommandSource.sendFeedback(new TranslatableText("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
        return 0;
    }

    private static int executeStop(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        DisableableProfiler disableableProfiler = minecraftServer.getProfiler();
        if (!disableableProfiler.getController().isEnabled()) {
            throw NORUNNING_EXCPETION.create();
        }
        ProfileResult profileResult = disableableProfiler.getController().disable();
        File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
        profileResult.save(file);
        float f = (float)profileResult.getTimeSpan() / 1.0E9f;
        float g = (float)profileResult.getTickSpan() / f;
        serverCommandSource.sendFeedback(new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", Float.valueOf(f)), profileResult.getTickSpan(), String.format("%.2f", Float.valueOf(g))), true);
        return MathHelper.floor(g);
    }

    private static int method_21618(ServerCommandSource serverCommandSource) {
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        String string = "debug-report-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        try {
            Path path = minecraftServer.getFile("debug").toPath();
            Files.createDirectories(path, new FileAttribute[0]);
            if (SharedConstants.isDevelopment || field_20310 == null) {
                Path path2 = path.resolve(string);
                minecraftServer.dump(path2);
            } else {
                Path path2 = path.resolve(string + ".zip");
                try (FileSystem fileSystem = field_20310.newFileSystem(path2, ImmutableMap.of("create", "true"));){
                    minecraftServer.dump(fileSystem.getPath("/", new String[0]));
                }
            }
            serverCommandSource.sendFeedback(new TranslatableText("commands.debug.reportSaved", string), false);
            return 1;
        } catch (IOException iOException) {
            logger.error("Failed to save debug dump", (Throwable)iOException);
            serverCommandSource.sendError(new TranslatableText("commands.debug.reportFailed", new Object[0]));
            return 0;
        }
    }
}

