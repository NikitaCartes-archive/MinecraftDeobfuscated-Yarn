/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
    private static final SimpleCommandExceptionType CENTER_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.center.failed", new Object[0]));
    private static final SimpleCommandExceptionType SET_FAILED_NOCHANGE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.nochange", new Object[0]));
    private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.small.", new Object[0]));
    private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.big.", new Object[0]));
    private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.warning.time.failed", new Object[0]));
    private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.warning.distance.failed", new Object[0]));
    private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.damage.buffer.failed", new Object[0]));
    private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.damage.amount.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("worldborder").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("distance", FloatArgumentType.floatArg(-6.0E7f, 6.0E7f)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L))).then(CommandManager.argument("time", IntegerArgumentType.integer(0)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getTargetRemainingTime() + (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L)))))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("distance", FloatArgumentType.floatArg(-6.0E7f, 6.0E7f)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"), 0L))).then(CommandManager.argument("time", IntegerArgumentType.integer(0)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"), (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L)))))).then(CommandManager.literal("center").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", Vec2ArgumentType.create()).executes(commandContext -> WorldBorderCommand.executeCenter((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2(commandContext, "pos")))))).then(((LiteralArgumentBuilder)CommandManager.literal("damage").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("amount").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("damagePerBlock", FloatArgumentType.floatArg(0.0f)).executes(commandContext -> WorldBorderCommand.executeDamage((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock")))))).then(CommandManager.literal("buffer").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("distance", FloatArgumentType.floatArg(0.0f)).executes(commandContext -> WorldBorderCommand.executeBuffer((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"))))))).then(CommandManager.literal("get").executes(commandContext -> WorldBorderCommand.executeGet((ServerCommandSource)commandContext.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("warning").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("distance").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("distance", IntegerArgumentType.integer(0)).executes(commandContext -> WorldBorderCommand.executeWarningDistance((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance")))))).then(CommandManager.literal("time").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("time", IntegerArgumentType.integer(0)).executes(commandContext -> WorldBorderCommand.executeWarningTime((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))))));
    }

    private static int executeBuffer(ServerCommandSource serverCommandSource, float f) throws CommandSyntaxException {
        WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
        if (worldBorder.getBuffer() == (double)f) {
            throw DAMAGE_BUFFER_FAILED_EXCEPTION.create();
        }
        worldBorder.setBuffer(f);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(f))), true);
        return (int)f;
    }

    private static int executeDamage(ServerCommandSource serverCommandSource, float f) throws CommandSyntaxException {
        WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
        if (worldBorder.getDamagePerBlock() == (double)f) {
            throw DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
        }
        worldBorder.setDamagePerBlock(f);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(f))), true);
        return (int)f;
    }

    private static int executeWarningTime(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
        WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
        if (worldBorder.getWarningTime() == i) {
            throw WARNING_TIME_FAILED_EXCEPTION.create();
        }
        worldBorder.setWarningTime(i);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.warning.time.success", i), true);
        return i;
    }

    private static int executeWarningDistance(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
        WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
        if (worldBorder.getWarningBlocks() == i) {
            throw WARNING_DISTANCE_FAILED_EXCEPTION.create();
        }
        worldBorder.setWarningBlocks(i);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.warning.distance.success", i), true);
        return i;
    }

    private static int executeGet(ServerCommandSource serverCommandSource) {
        double d = serverCommandSource.getWorld().getWorldBorder().getSize();
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
        return MathHelper.floor(d + 0.5);
    }

    private static int executeCenter(ServerCommandSource serverCommandSource, Vec2f vec2f) throws CommandSyntaxException {
        WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
        if (worldBorder.getCenterX() == (double)vec2f.x && worldBorder.getCenterZ() == (double)vec2f.y) {
            throw CENTER_FAILED_EXCEPTION.create();
        }
        worldBorder.setCenter(vec2f.x, vec2f.y);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(vec2f.x)), String.format("%.2f", Float.valueOf(vec2f.y))), true);
        return 0;
    }

    private static int executeSet(ServerCommandSource serverCommandSource, double d, long l) throws CommandSyntaxException {
        WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
        double e = worldBorder.getSize();
        if (e == d) {
            throw SET_FAILED_NOCHANGE_EXCEPTION.create();
        }
        if (d < 1.0) {
            throw SET_FAILED_SMALL_EXCEPTION.create();
        }
        if (d > 6.0E7) {
            throw SET_FAILED_BIG_EXCEPTION.create();
        }
        if (l > 0L) {
            worldBorder.interpolateSize(e, d, l);
            if (d > e) {
                serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
            } else {
                serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
            }
        } else {
            worldBorder.setSize(d);
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
        }
        return (int)(d - e);
    }
}

