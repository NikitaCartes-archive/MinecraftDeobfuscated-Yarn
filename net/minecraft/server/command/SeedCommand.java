/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("seed").requires(serverCommandSource -> !dedicated || serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> {
            long l = ((ServerCommandSource)commandContext.getSource()).getWorld().getSeed();
            MutableText text = Texts.bracketed(new LiteralText(String.valueOf(l)).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(l))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click"))).withInsertion(String.valueOf(l))));
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.seed.success", text), false);
            return (int)l;
        }));
    }
}

