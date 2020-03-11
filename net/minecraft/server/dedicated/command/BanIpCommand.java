/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class BanIpCommand {
    public static final Pattern field_13466 = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.banip.invalid", new Object[0]));
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.banip.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban-ip").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("target", StringArgumentType.word()).executes(commandContext -> BanIpCommand.checkIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), null))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(commandContext -> BanIpCommand.checkIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), MessageArgumentType.getMessage(commandContext, "reason"))))));
    }

    private static int checkIp(ServerCommandSource serverCommandSource, String string, @Nullable Text text) throws CommandSyntaxException {
        Matcher matcher = field_13466.matcher(string);
        if (matcher.matches()) {
            return BanIpCommand.banIp(serverCommandSource, string, text);
        }
        ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(string);
        if (serverPlayerEntity != null) {
            return BanIpCommand.banIp(serverCommandSource, serverPlayerEntity.getIp(), text);
        }
        throw INVALID_IP_EXCEPTION.create();
    }

    private static int banIp(ServerCommandSource serverCommandSource, String string, @Nullable Text text) throws CommandSyntaxException {
        BannedIpList bannedIpList = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
        if (bannedIpList.isBanned(string)) {
            throw ALREADY_BANNED_EXCEPTION.create();
        }
        List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayersByIp(string);
        BannedIpEntry bannedIpEntry = new BannedIpEntry(string, null, serverCommandSource.getName(), null, text == null ? null : text.getString());
        bannedIpList.add(bannedIpEntry);
        serverCommandSource.sendFeedback(new TranslatableText("commands.banip.success", string, bannedIpEntry.getReason()), true);
        if (!list.isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.banip.info", list.size(), EntitySelector.getNames(list)), true);
        }
        for (ServerPlayerEntity serverPlayerEntity : list) {
            serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.ip_banned", new Object[0]));
        }
        return list.size();
    }
}

