/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class ChatMessages {
    private static String getRenderedChatMessage(String message) {
        return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
    }

    public static List<Text> breakRenderedChatMessageLines(Text text, int width, TextRenderer textRenderer) {
        TextCollector textCollector = new TextCollector();
        text.visit((style, string) -> {
            textCollector.add(new LiteralText(ChatMessages.getRenderedChatMessage(string)).setStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        List<Text> list = textRenderer.getTextHandler().wrapLines(textCollector.getCombined(), width, Style.EMPTY);
        if (list.isEmpty()) {
            return Lists.newArrayList(LiteralText.EMPTY);
        }
        ArrayList<Text> list2 = Lists.newArrayList();
        list2.add(list.get(0));
        for (int i = 1; i < list.size(); ++i) {
            list2.add(new LiteralText(" ").append(list.get(i)));
        }
        return list2;
    }
}

