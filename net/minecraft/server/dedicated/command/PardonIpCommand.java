/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.text.TranslatableText;

public class PardonIpCommand {
    private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.pardonip.invalid", new Object[0]));
    private static final SimpleCommandExceptionType ALREADY_UNBANNED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.pardonip.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("pardon-ip").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled() && serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("target", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getIpBanList().getNames(), suggestionsBuilder)).executes(commandContext -> PardonIpCommand.pardonIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "target")))));
    }

    private static int pardonIp(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
        Matcher matcher = BanIpCommand.field_13466.matcher(string);
        if (!matcher.matches()) {
            throw INVALID_IP_EXCEPTION.create();
        }
        BannedIpList bannedIpList = serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList();
        if (!bannedIpList.isBanned(string)) {
            throw ALREADY_UNBANNED_EXCEPTION.create();
        }
        bannedIpList.remove(string);
        serverCommandSource.sendFeedback(new TranslatableText("commands.pardonip.success", string), true);
        return 1;
    }
}

