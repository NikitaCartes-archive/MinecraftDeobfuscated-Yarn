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
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class TagCommand {
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.tag.add.failed", new Object[0]));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.tag.remove.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tag").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.word()).executes(commandContext -> TagCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(TagCommand.getTags(EntityArgumentType.getEntities(commandContext, "targets")), suggestionsBuilder)).executes(commandContext -> TagCommand.executeRemove((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")))))).then(CommandManager.literal("list").executes(commandContext -> TagCommand.executeList((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"))))));
    }

    private static Collection<String> getTags(Collection<? extends Entity> collection) {
        HashSet<String> set = Sets.newHashSet();
        for (Entity entity : collection) {
            set.addAll(entity.getScoreboardTags());
        }
        return set;
    }

    private static int executeAdd(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!entity.addScoreboardTag(string)) continue;
            ++i;
        }
        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.add.success.single", string, collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.add.success.multiple", string, collection.size()), true);
        }
        return i;
    }

    private static int executeRemove(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!entity.removeScoreboardTag(string)) continue;
            ++i;
        }
        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.remove.success.single", string, collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.remove.success.multiple", string, collection.size()), true);
        }
        return i;
    }

    private static int executeList(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) {
        HashSet<String> set = Sets.newHashSet();
        for (Entity entity : collection) {
            set.addAll(entity.getScoreboardTags());
        }
        if (collection.size() == 1) {
            Entity entity2 = collection.iterator().next();
            if (set.isEmpty()) {
                serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.list.single.empty", entity2.getDisplayName()), false);
            } else {
                serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.list.single.success", entity2.getDisplayName(), set.size(), Components.sortedJoin(set)), false);
            }
        } else if (set.isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.list.multiple.empty", collection.size()), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.tag.list.multiple.success", collection.size(), set.size(), Components.sortedJoin(set)), false);
        }
        return set.size();
    }
}

