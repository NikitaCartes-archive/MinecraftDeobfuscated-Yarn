/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class TagCommand {
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("commands.tag.add.failed"));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("commands.tag.remove.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tag").requires(source -> source.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.word()).executes(context -> TagCommand.executeAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StringArgumentType.getString(context, "name")))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.word()).suggests((context, builder) -> CommandSource.suggestMatching(TagCommand.getTags(EntityArgumentType.getEntities(context, "targets")), builder)).executes(context -> TagCommand.executeRemove((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StringArgumentType.getString(context, "name")))))).then(CommandManager.literal("list").executes(context -> TagCommand.executeList((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"))))));
    }

    private static Collection<String> getTags(Collection<? extends Entity> entities) {
        HashSet<String> set = Sets.newHashSet();
        for (Entity entity : entities) {
            set.addAll(entity.getScoreboardTags());
        }
        return set;
    }

    private static int executeAdd(ServerCommandSource source, Collection<? extends Entity> targets, String tag) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets) {
            if (!entity.addScoreboardTag(tag)) continue;
            ++i;
        }
        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.method_43469("commands.tag.add.success.single", tag, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.method_43469("commands.tag.add.success.multiple", tag, targets.size()), true);
        }
        return i;
    }

    private static int executeRemove(ServerCommandSource source, Collection<? extends Entity> targets, String tag) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets) {
            if (!entity.removeScoreboardTag(tag)) continue;
            ++i;
        }
        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.method_43469("commands.tag.remove.success.single", tag, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.method_43469("commands.tag.remove.success.multiple", tag, targets.size()), true);
        }
        return i;
    }

    private static int executeList(ServerCommandSource source, Collection<? extends Entity> targets) {
        HashSet<String> set = Sets.newHashSet();
        for (Entity entity : targets) {
            set.addAll(entity.getScoreboardTags());
        }
        if (targets.size() == 1) {
            Entity entity2 = targets.iterator().next();
            if (set.isEmpty()) {
                source.sendFeedback(Text.method_43469("commands.tag.list.single.empty", entity2.getDisplayName()), false);
            } else {
                source.sendFeedback(Text.method_43469("commands.tag.list.single.success", entity2.getDisplayName(), set.size(), Texts.joinOrdered(set)), false);
            }
        } else if (set.isEmpty()) {
            source.sendFeedback(Text.method_43469("commands.tag.list.multiple.empty", targets.size()), false);
        } else {
            source.sendFeedback(Text.method_43469("commands.tag.list.multiple.success", targets.size(), set.size(), Texts.joinOrdered(set)), false);
        }
        return set.size();
    }
}

