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
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

@Environment(value=EnvType.CLIENT)
public class ChatMessages {
    private static final OrderedText field_25263 = OrderedText.styled(32, Style.EMPTY);

    private static String getRenderedChatMessage(String message) {
        return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
    }

    public static List<OrderedText> breakRenderedChatMessageLines(StringVisitable stringVisitable2, int width, TextRenderer textRenderer) {
        TextCollector textCollector = new TextCollector();
        stringVisitable2.visit((style, string) -> {
            textCollector.add(StringVisitable.styled(ChatMessages.getRenderedChatMessage(string), style));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList<OrderedText> list = Lists.newArrayList();
        textRenderer.getTextHandler().method_29971(textCollector.getCombined(), width, Style.EMPTY, (stringVisitable, boolean_) -> {
            OrderedText orderedText = Language.getInstance().reorder((StringVisitable)stringVisitable);
            list.add(boolean_ != false ? OrderedText.concat(field_25263, orderedText) : orderedText);
        });
        if (list.isEmpty()) {
            return Lists.newArrayList(OrderedText.EMPTY);
        }
        return list;
    }
}

