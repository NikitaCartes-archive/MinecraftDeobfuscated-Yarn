/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;

public class JfrCommand {
    private static final SimpleCommandExceptionType JFR_START_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.jfr.start.failed"));
    private static final DynamicCommandExceptionType JFR_DUMP_FAILED_EXCEPTION = new DynamicCommandExceptionType(message -> new TranslatableText("commands.jfr.dump.failed", message));

    private JfrCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("jfr").requires(source -> source.hasPermissionLevel(4))).then(CommandManager.literal("start").executes(context -> JfrCommand.executeStart((ServerCommandSource)context.getSource())))).then(CommandManager.literal("stop").executes(context -> JfrCommand.executeStop((ServerCommandSource)context.getSource()))));
    }

    private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
        InstanceType instanceType = InstanceType.get(source.getServer());
        if (!FlightProfiler.INSTANCE.start(instanceType)) {
            throw JFR_START_FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.jfr.started"), false);
        return 1;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        try {
            File file = FlightProfiler.INSTANCE.stop().toFile();
            MutableText text = new LiteralText(file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, file.getAbsolutePath())));
            source.sendFeedback(new TranslatableText("commands.jfr.stopped", text), false);
            return 1;
        } catch (Throwable throwable) {
            throw JFR_DUMP_FAILED_EXCEPTION.create(throwable.getMessage());
        }
    }
}

