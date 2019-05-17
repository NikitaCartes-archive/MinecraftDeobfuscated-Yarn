/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BossBarCommand {
    private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.bossbar.create.failed", object));
    private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.bossbar.unknown", object));
    private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.players.unchanged", new Object[0]));
    private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.name.unchanged", new Object[0]));
    private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.color.unchanged", new Object[0]));
    private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.style.unchanged", new Object[0]));
    private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.value.unchanged", new Object[0]));
    private static final SimpleCommandExceptionType SETMAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.max.unchanged", new Object[0]));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.hidden", new Object[0]));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.visible", new Object[0]));
    public static final SuggestionProvider<ServerCommandSource> suggestionProvider = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getBossBarManager().getIds(), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("bossbar").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("id", IdentifierArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", ComponentArgumentType.create()).executes(commandContext -> BossBarCommand.addBossBar((ServerCommandSource)commandContext.getSource(), IdentifierArgumentType.getIdentifier(commandContext, "id"), ComponentArgumentType.getComponent(commandContext, "name"))))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("id", IdentifierArgumentType.create()).suggests(suggestionProvider).executes(commandContext -> BossBarCommand.removeBossBar((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext)))))).then(CommandManager.literal("list").executes(commandContext -> BossBarCommand.listBossBars((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.create()).suggests(suggestionProvider).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("name").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", ComponentArgumentType.create()).executes(commandContext -> BossBarCommand.setName((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), ComponentArgumentType.getComponent(commandContext, "name")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("color").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("pink").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.PINK)))).then(CommandManager.literal("blue").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.BLUE)))).then(CommandManager.literal("red").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.RED)))).then(CommandManager.literal("green").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.GREEN)))).then(CommandManager.literal("yellow").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.YELLOW)))).then(CommandManager.literal("purple").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.PURPLE)))).then(CommandManager.literal("white").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Color.WHITE))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("style").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("progress").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Style.PROGRESS)))).then(CommandManager.literal("notched_6").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Style.NOTCHED_6)))).then(CommandManager.literal("notched_10").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Style.NOTCHED_10)))).then(CommandManager.literal("notched_12").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Style.NOTCHED_12)))).then(CommandManager.literal("notched_20").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BossBar.Style.NOTCHED_20))))).then(CommandManager.literal("value").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(commandContext -> BossBarCommand.setValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "value")))))).then(CommandManager.literal("max").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("max", IntegerArgumentType.integer(1)).executes(commandContext -> BossBarCommand.setMaxValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "max")))))).then(CommandManager.literal("visible").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("visible", BoolArgumentType.bool()).executes(commandContext -> BossBarCommand.setVisible((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), BoolArgumentType.getBool(commandContext, "visible")))))).then(((LiteralArgumentBuilder)CommandManager.literal("players").executes(commandContext -> BossBarCommand.setPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), Collections.emptyList()))).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> BossBarCommand.setPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext), EntityArgumentType.getOptionalPlayers(commandContext, "targets")))))))).then(CommandManager.literal("get").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.create()).suggests(suggestionProvider).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("value").executes(commandContext -> BossBarCommand.getValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext))))).then(CommandManager.literal("max").executes(commandContext -> BossBarCommand.getMaxValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext))))).then(CommandManager.literal("visible").executes(commandContext -> BossBarCommand.isVisible((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext))))).then(CommandManager.literal("players").executes(commandContext -> BossBarCommand.getPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar(commandContext)))))));
    }

    private static int getValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.get.value", commandBossBar.getTextComponent(), commandBossBar.getValue()), true);
        return commandBossBar.getValue();
    }

    private static int getMaxValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.get.max", commandBossBar.getTextComponent(), commandBossBar.getMaxValue()), true);
        return commandBossBar.getMaxValue();
    }

    private static int isVisible(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
        if (commandBossBar.isVisible()) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.get.visible.visible", commandBossBar.getTextComponent()), true);
            return 1;
        }
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.get.visible.hidden", commandBossBar.getTextComponent()), true);
        return 0;
    }

    private static int getPlayers(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
        if (commandBossBar.getPlayers().isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.get.players.none", commandBossBar.getTextComponent()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.get.players.some", commandBossBar.getTextComponent(), commandBossBar.getPlayers().size(), Components.join(commandBossBar.getPlayers(), PlayerEntity::getDisplayName)), true);
        }
        return commandBossBar.getPlayers().size();
    }

    private static int setVisible(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, boolean bl) throws CommandSyntaxException {
        if (commandBossBar.isVisible() == bl) {
            if (bl) {
                throw SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION.create();
            }
            throw SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION.create();
        }
        commandBossBar.setVisible(bl);
        if (bl) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.visible.success.visible", commandBossBar.getTextComponent()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.visible.success.hidden", commandBossBar.getTextComponent()), true);
        }
        return 0;
    }

    private static int setValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, int i) throws CommandSyntaxException {
        if (commandBossBar.getValue() == i) {
            throw SET_VALUE_UNCHANGED_EXCEPTION.create();
        }
        commandBossBar.setValue(i);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.value.success", commandBossBar.getTextComponent(), i), true);
        return i;
    }

    private static int setMaxValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, int i) throws CommandSyntaxException {
        if (commandBossBar.getMaxValue() == i) {
            throw SETMAX_UNCHANGED_EXCEPTION.create();
        }
        commandBossBar.setMaxValue(i);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.max.success", commandBossBar.getTextComponent(), i), true);
        return i;
    }

    private static int setColor(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, BossBar.Color color) throws CommandSyntaxException {
        if (commandBossBar.getColor().equals((Object)color)) {
            throw SET_COLOR_UNCHANGED_EXCEPTION.create();
        }
        commandBossBar.setColor(color);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.color.success", commandBossBar.getTextComponent()), true);
        return 0;
    }

    private static int setStyle(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, BossBar.Style style) throws CommandSyntaxException {
        if (commandBossBar.getOverlay().equals((Object)style)) {
            throw SET_STYLE_UNCHANGED_EXCEPTION.create();
        }
        commandBossBar.setOverlay(style);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.style.success", commandBossBar.getTextComponent()), true);
        return 0;
    }

    private static int setName(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, Component component) throws CommandSyntaxException {
        Component component2 = Components.resolveAndStyle(serverCommandSource, component, null, 0);
        if (commandBossBar.getName().equals(component2)) {
            throw SET_NAME_UNCHANGED_EXCEPTION.create();
        }
        commandBossBar.setName(component2);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.name.success", commandBossBar.getTextComponent()), true);
        return 0;
    }

    private static int setPlayers(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, Collection<ServerPlayerEntity> collection) throws CommandSyntaxException {
        boolean bl = commandBossBar.addPlayers(collection);
        if (!bl) {
            throw SET_PLAYERS_UNCHANGED_EXCEPTION.create();
        }
        if (commandBossBar.getPlayers().isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.players.success.none", commandBossBar.getTextComponent()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.set.players.success.some", commandBossBar.getTextComponent(), collection.size(), Components.join(collection, PlayerEntity::getDisplayName)), true);
        }
        return commandBossBar.getPlayers().size();
    }

    private static int listBossBars(ServerCommandSource serverCommandSource) {
        Collection<CommandBossBar> collection = serverCommandSource.getMinecraftServer().getBossBarManager().getAll();
        if (collection.isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.list.bars.none", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.list.bars.some", collection.size(), Components.join(collection, CommandBossBar::getTextComponent)), false);
        }
        return collection.size();
    }

    private static int addBossBar(ServerCommandSource serverCommandSource, Identifier identifier, Component component) throws CommandSyntaxException {
        BossBarManager bossBarManager = serverCommandSource.getMinecraftServer().getBossBarManager();
        if (bossBarManager.get(identifier) != null) {
            throw CREATE_FAILED_EXCEPTION.create(identifier.toString());
        }
        CommandBossBar commandBossBar = bossBarManager.add(identifier, Components.resolveAndStyle(serverCommandSource, component, null, 0));
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.create.success", commandBossBar.getTextComponent()), true);
        return bossBarManager.getAll().size();
    }

    private static int removeBossBar(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
        BossBarManager bossBarManager = serverCommandSource.getMinecraftServer().getBossBarManager();
        commandBossBar.clearPlayers();
        bossBarManager.remove(commandBossBar);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.bossbar.remove.success", commandBossBar.getTextComponent()), true);
        return bossBarManager.getAll().size();
    }

    public static CommandBossBar createBossBar(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgumentType.getIdentifier(commandContext, "id");
        CommandBossBar commandBossBar = commandContext.getSource().getMinecraftServer().getBossBarManager().get(identifier);
        if (commandBossBar == null) {
            throw UNKNOWN_EXCEPTION.create(identifier.toString());
        }
        return commandBossBar;
    }
}

