/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class AdvancementCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        Collection<Advancement> collection = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements();
        return CommandSource.suggestIdentifiers(collection.stream().map(Advancement::getId), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("advancement").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("grant").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("only").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommand.executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.GRANT, IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"))))))).then(CommandManager.literal("from").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.GRANT, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements())))))).then(CommandManager.literal("revoke").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("only").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommand.executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.REVOKE, IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"))))))).then(CommandManager.literal("from").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), Operation.REVOKE, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements()))))));
    }

    private static int executeAdvancement(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Operation operation, Collection<Advancement> collection2) {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            i += operation.processAll(serverPlayerEntity, collection2);
        }
        if (i == 0) {
            if (collection2.size() == 1) {
                if (collection.size() == 1) {
                    throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".one.to.one.failure", collection2.iterator().next().toHoverableText(), collection.iterator().next().getDisplayName()));
                }
                throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".one.to.many.failure", collection2.iterator().next().toHoverableText(), collection.size()));
            }
            if (collection.size() == 1) {
                throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".many.to.one.failure", collection2.size(), collection.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.failure", collection2.size(), collection.size()));
        }
        if (collection2.size() == 1) {
            if (collection.size() == 1) {
                serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".one.to.one.success", collection2.iterator().next().toHoverableText(), collection.iterator().next().getDisplayName()), true);
            } else {
                serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".one.to.many.success", collection2.iterator().next().toHoverableText(), collection.size()), true);
            }
        } else if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".many.to.one.success", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.success", collection2.size(), collection.size()), true);
        }
        return i;
    }

    private static int executeCriterion(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Operation operation, Advancement advancement, String string) {
        int i = 0;
        if (!advancement.getCriteria().containsKey(string)) {
            throw new CommandException(new TranslatableText("commands.advancement.criterionNotFound", advancement.toHoverableText(), string));
        }
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            if (!operation.processEachCriterion(serverPlayerEntity, advancement, string)) continue;
            ++i;
        }
        if (i == 0) {
            if (collection.size() == 1) {
                throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.one.failure", string, advancement.toHoverableText(), collection.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.failure", string, advancement.toHoverableText(), collection.size()));
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.one.success", string, advancement.toHoverableText(), collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.success", string, advancement.toHoverableText(), collection.size()), true);
        }
        return i;
    }

    private static List<Advancement> select(Advancement advancement, Selection selection) {
        ArrayList<Advancement> list = Lists.newArrayList();
        if (selection.before) {
            for (Advancement advancement2 = advancement.getParent(); advancement2 != null; advancement2 = advancement2.getParent()) {
                list.add(advancement2);
            }
        }
        list.add(advancement);
        if (selection.after) {
            AdvancementCommand.addChildrenRecursivelyToList(advancement, list);
        }
        return list;
    }

    private static void addChildrenRecursivelyToList(Advancement advancement, List<Advancement> list) {
        for (Advancement advancement2 : advancement.getChildren()) {
            list.add(advancement2);
            AdvancementCommand.addChildrenRecursivelyToList(advancement2, list);
        }
    }

    static enum Selection {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean before;
        private final boolean after;

        private Selection(boolean bl, boolean bl2) {
            this.before = bl;
            this.after = bl2;
        }
    }

    static enum Operation {
        GRANT("grant"){

            @Override
            protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
                AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
                if (advancementProgress.isDone()) {
                    return false;
                }
                for (String string : advancementProgress.getUnobtainedCriteria()) {
                    serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String string) {
                return serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, string);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
                AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
                if (!advancementProgress.isAnyObtained()) {
                    return false;
                }
                for (String string : advancementProgress.getObtainedCriteria()) {
                    serverPlayerEntity.getAdvancementTracker().revokeCriterion(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String string) {
                return serverPlayerEntity.getAdvancementTracker().revokeCriterion(advancement, string);
            }
        };

        private final String commandPrefix;

        private Operation(String string2) {
            this.commandPrefix = "commands.advancement." + string2;
        }

        public int processAll(ServerPlayerEntity serverPlayerEntity, Iterable<Advancement> iterable) {
            int i = 0;
            for (Advancement advancement : iterable) {
                if (!this.processEach(serverPlayerEntity, advancement)) continue;
                ++i;
            }
            return i;
        }

        protected abstract boolean processEach(ServerPlayerEntity var1, Advancement var2);

        protected abstract boolean processEachCriterion(ServerPlayerEntity var1, Advancement var2, String var3);

        protected String getCommandPrefix() {
            return this.commandPrefix;
        }
    }
}

