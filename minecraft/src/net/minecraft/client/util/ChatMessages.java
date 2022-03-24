package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public class ChatMessages {
	private static final OrderedText SPACES = OrderedText.styled(32, Style.EMPTY);

	private static String getRenderedChatMessage(String message) {
		return MinecraftClient.getInstance().options.getChatColors().getValue() ? message : Formatting.strip(message);
	}

	public static List<OrderedText> breakRenderedChatMessageLines(StringVisitable message, int width, TextRenderer textRenderer) {
		TextCollector textCollector = new TextCollector();
		message.visit((style, messagex) -> {
			textCollector.add(StringVisitable.styled(getRenderedChatMessage(messagex), style));
			return Optional.empty();
		}, Style.EMPTY);
		List<OrderedText> list = Lists.<OrderedText>newArrayList();
		textRenderer.getTextHandler().wrapLines(textCollector.getCombined(), width, Style.EMPTY, (text, lastLineWrapped) -> {
			OrderedText orderedText = Language.getInstance().reorder(text);
			list.add(lastLineWrapped ? OrderedText.concat(SPACES, orderedText) : orderedText);
		});
		return (List<OrderedText>)(list.isEmpty() ? Lists.<OrderedText>newArrayList(OrderedText.EMPTY) : list);
	}
}
