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
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class BanIpCommand {
    public static final Pattern PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.banip.invalid"));
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.banip.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban-ip").requires(source -> source.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("target", StringArgumentType.word()).executes(context -> BanIpCommand.checkIp((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "target"), null))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(context -> BanIpCommand.checkIp((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "target"), MessageArgumentType.getMessage(context, "reason"))))));
    }

    private static int checkIp(ServerCommandSource source, String target, @Nullable Text reason) throws CommandSyntaxException {
        Matcher matcher = PATTERN.matcher(target);
        if (matcher.matches()) {
            return BanIpCommand.banIp(source, target, reason);
        }
        ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(target);
        if (serverPlayerEntity != null) {
            return BanIpCommand.banIp(source, serverPlayerEntity.getIp(), reason);
        }
        throw INVALID_IP_EXCEPTION.create();
    }

    private static int banIp(ServerCommandSource source, String targetIp, @Nullable Text reason) throws CommandSyntaxException {
        BannedIpList bannedIpList = source.getServer().getPlayerManager().getIpBanList();
        if (bannedIpList.isBanned(targetIp)) {
            throw ALREADY_BANNED_EXCEPTION.create();
        }
        List<ServerPlayerEntity> list = source.getServer().getPlayerManager().getPlayersByIp(targetIp);
        BannedIpEntry bannedIpEntry = new BannedIpEntry(targetIp, null, source.getName(), null, reason == null ? null : reason.getString());
        bannedIpList.add(bannedIpEntry);
        source.sendFeedback(Text.translatable("commands.banip.success", targetIp, bannedIpEntry.getReason()), true);
        if (!list.isEmpty()) {
            source.sendFeedback(Text.translatable("commands.banip.info", list.size(), EntitySelector.getNames(list)), true);
        }
        for (ServerPlayerEntity serverPlayerEntity : list) {
            serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.ip_banned"));
        }
        return list.size();
    }
}

