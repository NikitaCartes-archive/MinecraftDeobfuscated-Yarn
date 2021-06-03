/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
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

public class PerfCommand {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.perf.notRunning"));
    private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.perf.alreadyRunning"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("perf").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).then(CommandManager.literal("start").executes(commandContext -> PerfCommand.executeStart((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("stop").executes(commandContext -> PerfCommand.executeStop((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (minecraftServer.isRunningMonitor()) {
            throw ALREADY_RUNNING_EXCEPTION.create();
        }
        Consumer<ProfileResult> consumer = profileResult -> PerfCommand.sendProfilingStoppedMessage(source, profileResult);
        Consumer<Path> consumer2 = path -> PerfCommand.saveReport(source, path, minecraftServer);
        minecraftServer.method_37320(consumer, consumer2);
        source.sendFeedback(new TranslatableText("commands.perf.started"), false);
        return 0;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (!minecraftServer.isRunningMonitor()) {
            throw NOT_RUNNING_EXCEPTION.create();
        }
        minecraftServer.method_37323();
        return 0;
    }

    private static void saveReport(ServerCommandSource source, Path tempProfilingFile, MinecraftServer server) {
        String string2;
        String string = String.format("%s-%s-%s", new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()), server.getSaveProperties().getLevelName(), SharedConstants.getGameVersion().getId());
        try {
            string2 = FileNameUtil.getNextUniqueName(ProfilerDumper.DEBUG_PROFILING_DIRECTORY, string, ".zip");
        } catch (IOException iOException) {
            source.sendError(new TranslatableText("commands.perf.reportFailed"));
            LOGGER.error(iOException);
            return;
        }
        try (ZipCompressor zipCompressor = new ZipCompressor(ProfilerDumper.DEBUG_PROFILING_DIRECTORY.resolve(string2));){
            zipCompressor.write(Paths.get("system.txt", new String[0]), server.method_37324(new SystemDetails()).collect());
            zipCompressor.copyAll(tempProfilingFile);
        }
        try {
            FileUtils.forceDelete(tempProfilingFile.toFile());
        } catch (IOException iOException) {
            LOGGER.warn("Failed to delete temporary profiling file {}", (Object)tempProfilingFile, (Object)iOException);
        }
        source.sendFeedback(new TranslatableText("commands.perf.reportSaved", string2), false);
    }

    private static void sendProfilingStoppedMessage(ServerCommandSource source, ProfileResult result) {
        int i = result.getTickSpan();
        double d = (double)result.getTimeSpan() / (double)Durations.field_33868;
        source.sendFeedback(new TranslatableText("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d)), false);
    }
}

