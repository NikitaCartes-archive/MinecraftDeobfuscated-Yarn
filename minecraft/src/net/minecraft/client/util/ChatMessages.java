package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ChatMessages {
	private static final StringRenderable field_25263 = StringRenderable.plain(" ");

	private static String getRenderedChatMessage(String message) {
		return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
	}

	public static List<StringRenderable> breakRenderedChatMessageLines(StringRenderable stringRenderable, int width, TextRenderer textRenderer) {
		TextCollector textCollector = new TextCollector();
		stringRenderable.visit((style, string) -> {
			textCollector.add(StringRenderable.styled(getRenderedChatMessage(string), style));
			return Optional.empty();
		}, Style.EMPTY);
		List<StringRenderable> list = textRenderer.getTextHandler().method_29971(textCollector.getCombined(), width, Style.EMPTY, field_25263);
		return (List<StringRenderable>)(list.isEmpty() ? Lists.<StringRenderable>newArrayList(StringRenderable.EMPTY) : list);
	}
}
