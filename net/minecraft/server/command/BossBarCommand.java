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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BossBarCommand {
    private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.bossbar.create.failed", object));
    private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.bossbar.unknown", object));
    private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.players.unchanged"));
    private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.name.unchanged"));
    private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.color.unchanged"));
    private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.style.unchanged"));
    private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.value.unchanged"));
    private static final SimpleCommandExceptionType SET_MAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.max.unchanged"));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.visibility.unchanged.hidden"));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.visibility.unchanged.visible"));
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getBossBarManager().getIds(), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("bossbar").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("id", IdentifierArgumentType.identifier()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", TextArgumentType.text()).executes(commandContext -> BossBarCommand.addBossBar((ServerCommandSource)commandContext.getSource(), IdentifierArgumentType.getIdentifier(commandContext, "id"), TextArgumentType.getTextArgument(commandContext, "name"))))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> BossBarCommand.removeBossBar((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext)))))).then(CommandManager.literal("list").executes(commandContext -> BossBarCommand.listBossBars((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("name").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", TextArgumentType.text()).executes(commandContext -> BossBarCommand.setName((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), TextArgumentType.getTextArgument(commandContext, "name")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("color").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("pink").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.PINK)))).then(CommandManager.literal("blue").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.BLUE)))).then(CommandManager.literal("red").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.RED)))).then(CommandManager.literal("green").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.GREEN)))).then(CommandManager.literal("yellow").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.YELLOW)))).then(CommandManager.literal("purple").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.PURPLE)))).then(CommandManager.literal("white").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Color.WHITE))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("style").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("progress").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Style.PROGRESS)))).then(CommandManager.literal("notched_6").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Style.NOTCHED_6)))).then(CommandManager.literal("notched_10").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Style.NOTCHED_10)))).then(CommandManager.literal("notched_12").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Style.NOTCHED_12)))).then(CommandManager.literal("notched_20").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BossBar.Style.NOTCHED_20))))).then(CommandManager.literal("value").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(commandContext -> BossBarCommand.setValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "value")))))).then(CommandManager.literal("max").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("max", IntegerArgumentType.integer(1)).executes(commandContext -> BossBarCommand.setMaxValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "max")))))).then(CommandManager.literal("visible").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("visible", BoolArgumentType.bool()).executes(commandContext -> BossBarCommand.setVisible((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), BoolArgumentType.getBool(commandContext, "visible")))))).then(((LiteralArgumentBuilder)CommandManager.literal("players").executes(commandContext -> BossBarCommand.setPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), Collections.emptyList()))).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> BossBarCommand.setPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), EntityArgumentType.getOptionalPlayers(commandContext, "targets")))))))).then(CommandManager.literal("get").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("value").executes(commandContext -> BossBarCommand.getValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext))))).then(CommandManager.literal("max").executes(commandContext -> BossBarCommand.getMaxValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext))))).then(CommandManager.literal("visible").executes(commandContext -> BossBarCommand.isVisible((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext))))).then(CommandManager.literal("players").executes(commandContext -> BossBarCommand.getPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext)))))));
    }

    private static int getValue(ServerCommandSource source, CommandBossBar bossBar) {
        source.sendFeedback(new TranslatableText("commands.bossbar.get.value", bossBar.toHoverableText(), bossBar.getValue()), true);
        return bossBar.getValue();
    }

    private static int getMaxValue(ServerCommandSource source, CommandBossBar bossBar) {
        source.sendFeedback(new TranslatableText("commands.bossbar.get.max", bossBar.toHoverableText(), bossBar.getMaxValue()), true);
        return bossBar.getMaxValue();
    }

    private static int isVisible(ServerCommandSource source, CommandBossBar bossBar) {
        if (bossBar.isVisible()) {
            source.sendFeedback(new TranslatableText("commands.bossbar.get.visible.visible", bossBar.toHoverableText()), true);
            return 1;
        }
        source.sendFeedback(new TranslatableText("commands.bossbar.get.visible.hidden", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int getPlayers(ServerCommandSource source, CommandBossBar bossBar) {
        if (bossBar.getPlayers().isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.bossbar.get.players.none", bossBar.toHoverableText()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.bossbar.get.players.some", bossBar.toHoverableText(), bossBar.getPlayers().size(), Texts.join(bossBar.getPlayers(), PlayerEntity::getDisplayName)), true);
        }
        return bossBar.getPlayers().size();
    }

    private static int setVisible(ServerCommandSource source, CommandBossBar bossBar, boolean visible) throws CommandSyntaxException {
        if (bossBar.isVisible() == visible) {
            if (visible) {
                throw SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION.create();
            }
            throw SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION.create();
        }
        bossBar.setVisible(visible);
        if (visible) {
            source.sendFeedback(new TranslatableText("commands.bossbar.set.visible.success.visible", bossBar.toHoverableText()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.bossbar.set.visible.success.hidden", bossBar.toHoverableText()), true);
        }
        return 0;
    }

    private static int setValue(ServerCommandSource source, CommandBossBar bossBar, int value) throws CommandSyntaxException {
        if (bossBar.getValue() == value) {
            throw SET_VALUE_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setValue(value);
        source.sendFeedback(new TranslatableText("commands.bossbar.set.value.success", bossBar.toHoverableText(), value), true);
        return value;
    }

    private static int setMaxValue(ServerCommandSource source, CommandBossBar bossBar, int value) throws CommandSyntaxException {
        if (bossBar.getMaxValue() == value) {
            throw SET_MAX_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setMaxValue(value);
        source.sendFeedback(new TranslatableText("commands.bossbar.set.max.success", bossBar.toHoverableText(), value), true);
        return value;
    }

    private static int setColor(ServerCommandSource source, CommandBossBar bossBar, BossBar.Color color) throws CommandSyntaxException {
        if (bossBar.getColor().equals((Object)color)) {
            throw SET_COLOR_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setColor(color);
        source.sendFeedback(new TranslatableText("commands.bossbar.set.color.success", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int setStyle(ServerCommandSource source, CommandBossBar bossBar, BossBar.Style style) throws CommandSyntaxException {
        if (bossBar.getOverlay().equals((Object)style)) {
            throw SET_STYLE_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setOverlay(style);
        source.sendFeedback(new TranslatableText("commands.bossbar.set.style.success", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int setName(ServerCommandSource source, CommandBossBar bossBar, Text name) throws CommandSyntaxException {
        MutableText text = Texts.parse(source, name, null, 0);
        if (bossBar.getName().equals(text)) {
            throw SET_NAME_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setName(text);
        source.sendFeedback(new TranslatableText("commands.bossbar.set.name.success", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int setPlayers(ServerCommandSource source, CommandBossBar bossBar, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        boolean bl = bossBar.addPlayers(players);
        if (!bl) {
            throw SET_PLAYERS_UNCHANGED_EXCEPTION.create();
        }
        if (bossBar.getPlayers().isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.bossbar.set.players.success.none", bossBar.toHoverableText()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.bossbar.set.players.success.some", bossBar.toHoverableText(), players.size(), Texts.join(players, PlayerEntity::getDisplayName)), true);
        }
        return bossBar.getPlayers().size();
    }

    private static int listBossBars(ServerCommandSource source) {
        Collection<CommandBossBar> collection = source.getMinecraftServer().getBossBarManager().getAll();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.bossbar.list.bars.none"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.bossbar.list.bars.some", collection.size(), Texts.join(collection, CommandBossBar::toHoverableText)), false);
        }
        return collection.size();
    }

    private static int addBossBar(ServerCommandSource source, Identifier name, Text displayName) throws CommandSyntaxException {
        BossBarManager bossBarManager = source.getMinecraftServer().getBossBarManager();
        if (bossBarManager.get(name) != null) {
            throw CREATE_FAILED_EXCEPTION.create(name.toString());
        }
        CommandBossBar commandBossBar = bossBarManager.add(name, Texts.parse(source, displayName, null, 0));
        source.sendFeedback(new TranslatableText("commands.bossbar.create.success", commandBossBar.toHoverableText()), true);
        return bossBarManager.getAll().size();
    }

    private static int removeBossBar(ServerCommandSource source, CommandBossBar bossBar) {
        BossBarManager bossBarManager = source.getMinecraftServer().getBossBarManager();
        bossBar.clearPlayers();
        bossBarManager.remove(bossBar);
        source.sendFeedback(new TranslatableText("commands.bossbar.remove.success", bossBar.toHoverableText()), true);
        return bossBarManager.getAll().size();
    }

    public static CommandBossBar getBossBar(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgumentType.getIdentifier(context, "id");
        CommandBossBar commandBossBar = context.getSource().getMinecraftServer().getBossBarManager().get(identifier);
        if (commandBossBar == null) {
            throw UNKNOWN_EXCEPTION.create(identifier.toString());
        }
        return commandBossBar;
    }
}

