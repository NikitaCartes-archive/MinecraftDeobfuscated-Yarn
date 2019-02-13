package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

@Environment(EnvType.CLIENT)
public class TextComponentUtil {
	public static String method_1849(String string, boolean bl) {
		return !bl && !MinecraftClient.getInstance().options.chatColors ? TextFormat.stripFormatting(string) : string;
	}

	public static List<TextComponent> wrapLines(TextComponent textComponent, int i, TextRenderer textRenderer, boolean bl, boolean bl2) {
		int j = 0;
		TextComponent textComponent2 = new StringTextComponent("");
		List<TextComponent> list = Lists.<TextComponent>newArrayList();
		List<TextComponent> list2 = Lists.<TextComponent>newArrayList(textComponent);

		for (int k = 0; k < list2.size(); k++) {
			TextComponent textComponent3 = (TextComponent)list2.get(k);
			String string = textComponent3.getText();
			boolean bl3 = false;
			if (string.contains("\n")) {
				int l = string.indexOf(10);
				String string2 = string.substring(l + 1);
				string = string.substring(0, l + 1);
				TextComponent textComponent4 = new StringTextComponent(string2).setStyle(textComponent3.getStyle().clone());
				list2.add(k + 1, textComponent4);
				bl3 = true;
			}

			String string3 = method_1849(textComponent3.getStyle().getFormatString() + string, bl2);
			String string2 = string3.endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
			int m = textRenderer.getStringWidth(string2);
			TextComponent textComponent5 = new StringTextComponent(string2).setStyle(textComponent3.getStyle().clone());
			if (j + m > i) {
				String string4 = textRenderer.trimToWidth(string3, i - j, false);
				String string5 = string4.length() < string3.length() ? string3.substring(string4.length()) : null;
				if (string5 != null && !string5.isEmpty()) {
					int n = string5.charAt(0) != ' ' ? string4.lastIndexOf(32) : string4.length();
					if (n >= 0 && textRenderer.getStringWidth(string3.substring(0, n)) > 0) {
						string4 = string3.substring(0, n);
						if (bl) {
							n++;
						}

						string5 = string3.substring(n);
					} else if (j > 0 && !string3.contains(" ")) {
						string4 = "";
						string5 = string3;
					}

					TextComponent textComponent6 = new StringTextComponent(string5).setStyle(textComponent3.getStyle().clone());
					list2.add(k + 1, textComponent6);
				}

				m = textRenderer.getStringWidth(string4);
				textComponent5 = new StringTextComponent(string4);
				textComponent5.setStyle(textComponent3.getStyle().clone());
				bl3 = true;
			}

			if (j + m <= i) {
				j += m;
				textComponent2.append(textComponent5);
			} else {
				bl3 = true;
			}

			if (bl3) {
				list.add(textComponent2);
				j = 0;
				textComponent2 = new StringTextComponent("");
			}
		}

		list.add(textComponent2);
		return list;
	}
}
