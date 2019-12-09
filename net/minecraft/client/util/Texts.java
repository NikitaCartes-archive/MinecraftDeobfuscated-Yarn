/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class Texts {
    public static String getRenderChatMessage(String string, boolean forceColor) {
        if (forceColor || MinecraftClient.getInstance().options.chatColors) {
            return string;
        }
        return Formatting.strip(string);
    }

    public static List<Text> wrapLines(Text text, int width, TextRenderer textRenderer, boolean bl, boolean forceColor) {
        int i = 0;
        LiteralText text2 = new LiteralText("");
        ArrayList<Text> list = Lists.newArrayList();
        ArrayList<Text> list2 = Lists.newArrayList(text);
        for (int j = 0; j < list2.size(); ++j) {
            String string3;
            String string2;
            Text text3 = (Text)list2.get(j);
            String string = text3.asString();
            boolean bl2 = false;
            if (string.contains("\n")) {
                int k = string.indexOf(10);
                string2 = string.substring(k + 1);
                string = string.substring(0, k + 1);
                Text text4 = new LiteralText(string2).setStyle(text3.getStyle().deepCopy());
                list2.add(j + 1, text4);
                bl2 = true;
            }
            string2 = (string3 = Texts.getRenderChatMessage(text3.getStyle().asString() + string, forceColor)).endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
            int l = textRenderer.getStringWidth(string2);
            Text text5 = new LiteralText(string2).setStyle(text3.getStyle().deepCopy());
            if (i + l > width) {
                String string5;
                String string4 = textRenderer.trimToWidth(string3, width - i, false);
                String string6 = string5 = string4.length() < string3.length() ? string3.substring(string4.length()) : null;
                if (string5 != null && !string5.isEmpty()) {
                    int m;
                    int n = m = string5.charAt(0) != ' ' ? string4.lastIndexOf(32) : string4.length();
                    if (m >= 0 && textRenderer.getStringWidth(string3.substring(0, m)) > 0) {
                        string4 = string3.substring(0, m);
                        if (bl) {
                            ++m;
                        }
                        string5 = string3.substring(m);
                    } else if (i > 0 && !string3.contains(" ")) {
                        string4 = "";
                        string5 = string3;
                    }
                    Text text6 = new LiteralText(string5).setStyle(text3.getStyle().deepCopy());
                    list2.add(j + 1, text6);
                }
                string3 = string4;
                l = textRenderer.getStringWidth(string3);
                text5 = new LiteralText(string3);
                text5.setStyle(text3.getStyle().deepCopy());
                bl2 = true;
            }
            if (i + l <= width) {
                i += l;
                text2.append(text5);
            } else {
                bl2 = true;
            }
            if (!bl2) continue;
            list.add(text2);
            i = 0;
            text2 = new LiteralText("");
        }
        list.add(text2);
        return list;
    }
}

