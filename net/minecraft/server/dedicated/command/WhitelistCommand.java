/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.alreadyOn", new Object[0]));
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.alreadyOff", new Object[0]));
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.add.failed", new Object[0]));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.remove.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("whitelist").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.literal("on").executes(commandContext -> WhitelistCommand.executeOn((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("off").executes(commandContext -> WhitelistCommand.executeOff((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("list").executes(commandContext -> WhitelistCommand.executeList((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> {
            PlayerManager playerManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(serverPlayerEntity -> !playerManager.getWhitelist().isAllowed(serverPlayerEntity.getGameProfile())).map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> WhitelistCommand.executeAdd((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets")))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getWhitelistedNames(), suggestionsBuilder)).executes(commandContext -> WhitelistCommand.executeRemove((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets")))))).then(CommandManager.literal("reload").executes(commandContext -> WhitelistCommand.executeReload((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeReload(ServerCommandSource serverCommandSource) {
        serverCommandSource.getMinecraftServer().getPlayerManager().reloadWhitelist();
        serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.reloaded", new Object[0]), true);
        serverCommandSource.getMinecraftServer().kickNonWhitelistedPlayers(serverCommandSource);
        return 1;
    }

    private static int executeAdd(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
        Whitelist whitelist = serverCommandSource.getMinecraftServer().getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (whitelist.isAllowed(gameProfile)) continue;
            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.add(whitelistEntry);
            serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.add.success", Texts.toText(gameProfile)), true);
            ++i;
        }
        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        return i;
    }

    private static int executeRemove(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
        Whitelist whitelist = serverCommandSource.getMinecraftServer().getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (!whitelist.isAllowed(gameProfile)) continue;
            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.removeEntry(whitelistEntry);
            serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.remove.success", Texts.toText(gameProfile)), true);
            ++i;
        }
        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        serverCommandSource.getMinecraftServer().kickNonWhitelistedPlayers(serverCommandSource);
        return i;
    }

    private static int executeOn(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
        if (playerManager.isWhitelistEnabled()) {
            throw ALREADY_ON_EXCEPTION.create();
        }
        playerManager.setWhitelistEnabled(true);
        serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.enabled", new Object[0]), true);
        serverCommandSource.getMinecraftServer().kickNonWhitelistedPlayers(serverCommandSource);
        return 1;
    }

    private static int executeOff(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
        if (!playerManager.isWhitelistEnabled()) {
            throw ALREADY_OFF_EXCEPTION.create();
        }
        playerManager.setWhitelistEnabled(false);
        serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.disabled", new Object[0]), true);
        return 1;
    }

    private static int executeList(ServerCommandSource serverCommandSource) {
        CharSequence[] strings = serverCommandSource.getMinecraftServer().getPlayerManager().getWhitelistedNames();
        if (strings.length == 0) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.none", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.whitelist.list", strings.length, String.join((CharSequence)", ", strings)), false);
        }
        return strings.length;
    }
}

