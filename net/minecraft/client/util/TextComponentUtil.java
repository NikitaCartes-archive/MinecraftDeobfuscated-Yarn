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
public class TextComponentUtil {
    public static String method_1849(String string, boolean bl) {
        if (bl || MinecraftClient.getInstance().options.chatColors) {
            return string;
        }
        return Formatting.strip(string);
    }

    public static List<Text> wrapLines(Text text, int i, TextRenderer textRenderer, boolean bl, boolean bl2) {
        int j = 0;
        LiteralText text2 = new LiteralText("");
        ArrayList<Text> list = Lists.newArrayList();
        ArrayList<Text> list2 = Lists.newArrayList(text);
        for (int k = 0; k < list2.size(); ++k) {
            String string3;
            String string2;
            Text text3 = (Text)list2.get(k);
            String string = text3.asString();
            boolean bl3 = false;
            if (string.contains("\n")) {
                int l = string.indexOf(10);
                string2 = string.substring(l + 1);
                string = string.substring(0, l + 1);
                Text text4 = new LiteralText(string2).setStyle(text3.getStyle().deepCopy());
                list2.add(k + 1, text4);
                bl3 = true;
            }
            string2 = (string3 = TextComponentUtil.method_1849(text3.getStyle().asString() + string, bl2)).endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
            int m = textRenderer.getStringWidth(string2);
            Text text5 = new LiteralText(string2).setStyle(text3.getStyle().deepCopy());
            if (j + m > i) {
                String string5;
                String string4 = textRenderer.trimToWidth(string3, i - j, false);
                String string6 = string5 = string4.length() < string3.length() ? string3.substring(string4.length()) : null;
                if (string5 != null && !string5.isEmpty()) {
                    int n;
                    int n2 = n = string5.charAt(0) != ' ' ? string4.lastIndexOf(32) : string4.length();
                    if (n >= 0 && textRenderer.getStringWidth(string3.substring(0, n)) > 0) {
                        string4 = string3.substring(0, n);
                        if (bl) {
                            ++n;
                        }
                        string5 = string3.substring(n);
                    } else if (j > 0 && !string3.contains(" ")) {
                        string4 = "";
                        string5 = string3;
                    }
                    Text text6 = new LiteralText(string5).setStyle(text3.getStyle().deepCopy());
                    list2.add(k + 1, text6);
                }
                string3 = string4;
                m = textRenderer.getStringWidth(string3);
                text5 = new LiteralText(string3);
                text5.setStyle(text3.getStyle().deepCopy());
                bl3 = true;
            }
            if (j + m <= i) {
                j += m;
                text2.append(text5);
            } else {
                bl3 = true;
            }
            if (!bl3) continue;
            list.add(text2);
            j = 0;
            text2 = new LiteralText("");
        }
        list.add(text2);
        return list;
    }
}

