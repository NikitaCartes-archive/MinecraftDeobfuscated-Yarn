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
import net.minecraft.class_5348;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class ChatMessages {
    private static final class_5348 field_25263 = class_5348.method_29430(" ");

    private static String getRenderedChatMessage(String message) {
        return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
    }

    public static List<class_5348> breakRenderedChatMessageLines(class_5348 arg, int width, TextRenderer textRenderer) {
        TextCollector textCollector = new TextCollector();
        arg.visit((style, string) -> {
            textCollector.add(class_5348.method_29431(ChatMessages.getRenderedChatMessage(string), style));
            return Optional.empty();
        }, Style.EMPTY);
        List<class_5348> list = textRenderer.getTextHandler().wrapLines(textCollector.getCombined(), width, Style.EMPTY);
        if (list.isEmpty()) {
            return Lists.newArrayList(class_5348.field_25310);
        }
        ArrayList<class_5348> list2 = Lists.newArrayList();
        list2.add(list.get(0));
        for (int i = 1; i < list.size(); ++i) {
            list2.add(class_5348.method_29433(field_25263, list.get(i)));
        }
        return list2;
    }
}

