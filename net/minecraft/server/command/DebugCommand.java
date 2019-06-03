/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.ProfileResult;

public class DebugCommand {
    private static final SimpleCommandExceptionType NORUNNING_EXCPETION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.notRunning", new Object[0]));
    private static final SimpleCommandExceptionType ALREADYRUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.alreadyRunning", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.literal("start").executes(commandContext -> DebugCommand.executeStart((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("stop").executes(commandContext -> DebugCommand.executeStop((ServerCommandSource)commandContext.getSource()))));
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
        profileResult.saveToFile(file);
        float f = (float)profileResult.getTimeSpan() / 1.0E9f;
        float g = (float)profileResult.getTickSpan() / f;
        serverCommandSource.sendFeedback(new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", Float.valueOf(f)), profileResult.getTickSpan(), String.format("%.2f", Float.valueOf(g))), true);
        return MathHelper.floor(g);
    }
}

