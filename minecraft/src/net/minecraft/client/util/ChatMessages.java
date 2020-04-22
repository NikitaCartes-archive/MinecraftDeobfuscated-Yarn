package net.minecraft.client.util;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ChatMessages {
	private static String getRenderedChatMessage(String message) {
		return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
	}

	public static List<Text> breakRenderedChatMessageLines(Text text, int width, TextRenderer textRenderer) {
		TextCollector textCollector = new TextCollector();
		text.visit((style, string) -> {
			textCollector.add(new LiteralText(getRenderedChatMessage(string)).setStyle(style));
			return Optional.empty();
		}, Style.EMPTY);
		return textRenderer.getTextHandler().wrapLines(textCollector.getCombined(), width, Style.EMPTY, false);
	}
}
