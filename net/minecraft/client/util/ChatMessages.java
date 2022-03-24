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
    private static final OrderedText SPACES = OrderedText.styled(32, Style.EMPTY);

    private static String getRenderedChatMessage(String message) {
        return MinecraftClient.getInstance().options.getChatColors().getValue() != false ? message : Formatting.strip(message);
    }

    public static List<OrderedText> breakRenderedChatMessageLines(StringVisitable message2, int width, TextRenderer textRenderer) {
        TextCollector textCollector = new TextCollector();
        message2.visit((style, message) -> {
            textCollector.add(StringVisitable.styled(ChatMessages.getRenderedChatMessage(message), style));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList<OrderedText> list = Lists.newArrayList();
        textRenderer.getTextHandler().wrapLines(textCollector.getCombined(), width, Style.EMPTY, (text, lastLineWrapped) -> {
            OrderedText orderedText = Language.getInstance().reorder((StringVisitable)text);
            list.add(lastLineWrapped != false ? OrderedText.concat(SPACES, orderedText) : orderedText);
        });
        if (list.isEmpty()) {
            return Lists.newArrayList(OrderedText.EMPTY);
        }
        return list;
    }
}

