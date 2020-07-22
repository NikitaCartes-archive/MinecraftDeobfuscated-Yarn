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
import net.minecraft.class_5481;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

@Environment(value=EnvType.CLIENT)
public class ChatMessages {
    private static final class_5481 field_25263 = class_5481.method_30741(32, Style.EMPTY);

    private static String getRenderedChatMessage(String message) {
        return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
    }

    public static List<class_5481> breakRenderedChatMessageLines(StringRenderable stringRenderable2, int width, TextRenderer textRenderer) {
        TextCollector textCollector = new TextCollector();
        stringRenderable2.visit((style, string) -> {
            textCollector.add(StringRenderable.styled(ChatMessages.getRenderedChatMessage(string), style));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList<class_5481> list = Lists.newArrayList();
        textRenderer.getTextHandler().method_29971(textCollector.getCombined(), width, Style.EMPTY, (stringRenderable, boolean_) -> {
            class_5481 lv = Language.getInstance().method_30934((StringRenderable)stringRenderable);
            list.add(boolean_ != false ? class_5481.method_30742(field_25263, lv) : lv);
        });
        if (list.isEmpty()) {
            return Lists.newArrayList(class_5481.field_26385);
        }
        return list;
    }
}

