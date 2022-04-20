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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;

public class BossBarCommand {
    private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("commands.bossbar.create.failed", name));
    private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("commands.bossbar.unknown", name));
    private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.players.unchanged"));
    private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.name.unchanged"));
    private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.color.unchanged"));
    private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.style.unchanged"));
    private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.value.unchanged"));
    private static final SimpleCommandExceptionType SET_MAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.max.unchanged"));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.visibility.unchanged.hidden"));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.bossbar.set.visibility.unchanged.visible"));
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(((ServerCommandSource)context.getSource()).getServer().getBossBarManager().getIds(), builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("bossbar").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("id", IdentifierArgumentType.identifier()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", TextArgumentType.text()).executes(context -> BossBarCommand.addBossBar((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "id"), TextArgumentType.getTextArgument(context, "name"))))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> BossBarCommand.removeBossBar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context)))))).then(CommandManager.literal("list").executes(context -> BossBarCommand.listBossBars((ServerCommandSource)context.getSource())))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("name").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", TextArgumentType.text()).executes(context -> BossBarCommand.setName((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), TextArgumentType.getTextArgument(context, "name")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("color").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("pink").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.PINK)))).then(CommandManager.literal("blue").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.BLUE)))).then(CommandManager.literal("red").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.RED)))).then(CommandManager.literal("green").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.GREEN)))).then(CommandManager.literal("yellow").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.YELLOW)))).then(CommandManager.literal("purple").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.PURPLE)))).then(CommandManager.literal("white").executes(context -> BossBarCommand.setColor((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Color.WHITE))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("style").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("progress").executes(context -> BossBarCommand.setStyle((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Style.PROGRESS)))).then(CommandManager.literal("notched_6").executes(context -> BossBarCommand.setStyle((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Style.NOTCHED_6)))).then(CommandManager.literal("notched_10").executes(context -> BossBarCommand.setStyle((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Style.NOTCHED_10)))).then(CommandManager.literal("notched_12").executes(context -> BossBarCommand.setStyle((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Style.NOTCHED_12)))).then(CommandManager.literal("notched_20").executes(context -> BossBarCommand.setStyle((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BossBar.Style.NOTCHED_20))))).then(CommandManager.literal("value").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> BossBarCommand.setValue((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), IntegerArgumentType.getInteger(context, "value")))))).then(CommandManager.literal("max").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("max", IntegerArgumentType.integer(1)).executes(context -> BossBarCommand.setMaxValue((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), IntegerArgumentType.getInteger(context, "max")))))).then(CommandManager.literal("visible").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("visible", BoolArgumentType.bool()).executes(context -> BossBarCommand.setVisible((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), BoolArgumentType.getBool(context, "visible")))))).then(((LiteralArgumentBuilder)CommandManager.literal("players").executes(context -> BossBarCommand.setPlayers((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), Collections.emptyList()))).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(context -> BossBarCommand.setPlayers((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), EntityArgumentType.getOptionalPlayers(context, "targets")))))))).then(CommandManager.literal("get").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("value").executes(context -> BossBarCommand.getValue((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context))))).then(CommandManager.literal("max").executes(context -> BossBarCommand.getMaxValue((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context))))).then(CommandManager.literal("visible").executes(context -> BossBarCommand.isVisible((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context))))).then(CommandManager.literal("players").executes(context -> BossBarCommand.getPlayers((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context)))))));
    }

    private static int getValue(ServerCommandSource source, CommandBossBar bossBar) {
        source.sendFeedback(Text.translatable("commands.bossbar.get.value", bossBar.toHoverableText(), bossBar.getValue()), true);
        return bossBar.getValue();
    }

    private static int getMaxValue(ServerCommandSource source, CommandBossBar bossBar) {
        source.sendFeedback(Text.translatable("commands.bossbar.get.max", bossBar.toHoverableText(), bossBar.getMaxValue()), true);
        return bossBar.getMaxValue();
    }

    private static int isVisible(ServerCommandSource source, CommandBossBar bossBar) {
        if (bossBar.isVisible()) {
            source.sendFeedback(Text.translatable("commands.bossbar.get.visible.visible", bossBar.toHoverableText()), true);
            return 1;
        }
        source.sendFeedback(Text.translatable("commands.bossbar.get.visible.hidden", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int getPlayers(ServerCommandSource source, CommandBossBar bossBar) {
        if (bossBar.getPlayers().isEmpty()) {
            source.sendFeedback(Text.translatable("commands.bossbar.get.players.none", bossBar.toHoverableText()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.bossbar.get.players.some", bossBar.toHoverableText(), bossBar.getPlayers().size(), Texts.join(bossBar.getPlayers(), PlayerEntity::getDisplayName)), true);
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
            source.sendFeedback(Text.translatable("commands.bossbar.set.visible.success.visible", bossBar.toHoverableText()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.bossbar.set.visible.success.hidden", bossBar.toHoverableText()), true);
        }
        return 0;
    }

    private static int setValue(ServerCommandSource source, CommandBossBar bossBar, int value) throws CommandSyntaxException {
        if (bossBar.getValue() == value) {
            throw SET_VALUE_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setValue(value);
        source.sendFeedback(Text.translatable("commands.bossbar.set.value.success", bossBar.toHoverableText(), value), true);
        return value;
    }

    private static int setMaxValue(ServerCommandSource source, CommandBossBar bossBar, int value) throws CommandSyntaxException {
        if (bossBar.getMaxValue() == value) {
            throw SET_MAX_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setMaxValue(value);
        source.sendFeedback(Text.translatable("commands.bossbar.set.max.success", bossBar.toHoverableText(), value), true);
        return value;
    }

    private static int setColor(ServerCommandSource source, CommandBossBar bossBar, BossBar.Color color) throws CommandSyntaxException {
        if (bossBar.getColor().equals((Object)color)) {
            throw SET_COLOR_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setColor(color);
        source.sendFeedback(Text.translatable("commands.bossbar.set.color.success", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int setStyle(ServerCommandSource source, CommandBossBar bossBar, BossBar.Style style) throws CommandSyntaxException {
        if (bossBar.getStyle().equals((Object)style)) {
            throw SET_STYLE_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setStyle(style);
        source.sendFeedback(Text.translatable("commands.bossbar.set.style.success", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int setName(ServerCommandSource source, CommandBossBar bossBar, Text name) throws CommandSyntaxException {
        MutableText text = Texts.parse(source, name, null, 0);
        if (bossBar.getName().equals(text)) {
            throw SET_NAME_UNCHANGED_EXCEPTION.create();
        }
        bossBar.setName(text);
        source.sendFeedback(Text.translatable("commands.bossbar.set.name.success", bossBar.toHoverableText()), true);
        return 0;
    }

    private static int setPlayers(ServerCommandSource source, CommandBossBar bossBar, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        boolean bl = bossBar.addPlayers(players);
        if (!bl) {
            throw SET_PLAYERS_UNCHANGED_EXCEPTION.create();
        }
        if (bossBar.getPlayers().isEmpty()) {
            source.sendFeedback(Text.translatable("commands.bossbar.set.players.success.none", bossBar.toHoverableText()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.bossbar.set.players.success.some", bossBar.toHoverableText(), players.size(), Texts.join(players, PlayerEntity::getDisplayName)), true);
        }
        return bossBar.getPlayers().size();
    }

    private static int listBossBars(ServerCommandSource source) {
        Collection<CommandBossBar> collection = source.getServer().getBossBarManager().getAll();
        if (collection.isEmpty()) {
            source.sendFeedback(Text.translatable("commands.bossbar.list.bars.none"), false);
        } else {
            source.sendFeedback(Text.translatable("commands.bossbar.list.bars.some", collection.size(), Texts.join(collection, CommandBossBar::toHoverableText)), false);
        }
        return collection.size();
    }

    private static int addBossBar(ServerCommandSource source, Identifier name, Text displayName) throws CommandSyntaxException {
        BossBarManager bossBarManager = source.getServer().getBossBarManager();
        if (bossBarManager.get(name) != null) {
            throw CREATE_FAILED_EXCEPTION.create(name.toString());
        }
        CommandBossBar commandBossBar = bossBarManager.add(name, Texts.parse(source, displayName, null, 0));
        source.sendFeedback(Text.translatable("commands.bossbar.create.success", commandBossBar.toHoverableText()), true);
        return bossBarManager.getAll().size();
    }

    private static int removeBossBar(ServerCommandSource source, CommandBossBar bossBar) {
        BossBarManager bossBarManager = source.getServer().getBossBarManager();
        bossBar.clearPlayers();
        bossBarManager.remove(bossBar);
        source.sendFeedback(Text.translatable("commands.bossbar.remove.success", bossBar.toHoverableText()), true);
        return bossBarManager.getAll().size();
    }

    public static CommandBossBar getBossBar(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgumentType.getIdentifier(context, "id");
        CommandBossBar commandBossBar = context.getSource().getServer().getBossBarManager().get(identifier);
        if (commandBossBar == null) {
            throw UNKNOWN_EXCEPTION.create(identifier.toString());
        }
        return commandBossBar;
    }
}

