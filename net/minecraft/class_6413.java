/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.class_6396;
import net.minecraft.class_6397;
import net.minecraft.client.util.profiler.ProfilerDumper;
import net.minecraft.entity.ai.Durations;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_6413 {
    private static final Logger field_33985 = LogManager.getLogger();
    private static final SimpleCommandExceptionType field_33986 = new SimpleCommandExceptionType(new TranslatableText("commands.perf.notRunning"));
    private static final SimpleCommandExceptionType field_33987 = new SimpleCommandExceptionType(new TranslatableText("commands.perf.alreadyRunning"));

    public static void method_37331(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("perf").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).then(CommandManager.literal("start").executes(commandContext -> class_6413.method_37333((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("stop").executes(commandContext -> class_6413.method_37338((ServerCommandSource)commandContext.getSource()))));
    }

    private static int method_37333(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        if (minecraftServer.method_37321()) {
            throw field_33987.create();
        }
        Consumer<ProfileResult> consumer = profileResult -> class_6413.method_37334(serverCommandSource, profileResult);
        Consumer<Path> consumer2 = path -> class_6413.method_37335(serverCommandSource, path, minecraftServer);
        minecraftServer.method_37320(consumer, consumer2);
        serverCommandSource.sendFeedback(new TranslatableText("commands.perf.started"), false);
        return 0;
    }

    private static int method_37338(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        if (!minecraftServer.method_37321()) {
            throw field_33986.create();
        }
        minecraftServer.method_37323();
        return 0;
    }

    private static void method_37335(ServerCommandSource serverCommandSource, Path path, MinecraftServer minecraftServer) {
        String string2;
        String string = String.format("%s-%s-%s", new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()), minecraftServer.getSaveProperties().getLevelName(), SharedConstants.getGameVersion().getId());
        try {
            string2 = FileNameUtil.getNextUniqueName(ProfilerDumper.DEBUG_PROFILING_DIRECTORY, string, ".zip");
        } catch (IOException iOException) {
            serverCommandSource.sendError(new TranslatableText("commands.perf.reportFailed"));
            field_33985.error(iOException);
            return;
        }
        try (class_6397 lv = new class_6397(ProfilerDumper.DEBUG_PROFILING_DIRECTORY.resolve(string2));){
            lv.method_37163(Paths.get("system.txt", new String[0]), minecraftServer.method_37324(new class_6396()).method_37120());
            lv.method_37161(path);
        }
        try {
            FileUtils.forceDelete(path.toFile());
        } catch (IOException iOException) {
            field_33985.warn("Failed to delete temporary profiling file {}", (Object)path, (Object)iOException);
        }
        serverCommandSource.sendFeedback(new TranslatableText("commands.perf.reportSaved", string2), false);
    }

    private static void method_37334(ServerCommandSource serverCommandSource, ProfileResult profileResult) {
        int i = profileResult.getTickSpan();
        double d = (double)profileResult.getTimeSpan() / (double)Durations.field_33868;
        serverCommandSource.sendFeedback(new TranslatableText("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d)), false);
    }
}

