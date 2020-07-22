package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5481;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public class ChatMessages {
	private static final class_5481 field_25263 = class_5481.method_30741(32, Style.EMPTY);

	private static String getRenderedChatMessage(String message) {
		return MinecraftClient.getInstance().options.chatColors ? message : Formatting.strip(message);
	}

	public static List<class_5481> breakRenderedChatMessageLines(StringRenderable stringRenderable, int width, TextRenderer textRenderer) {
		TextCollector textCollector = new TextCollector();
		stringRenderable.visit((style, string) -> {
			textCollector.add(StringRenderable.styled(getRenderedChatMessage(string), style));
			return Optional.empty();
		}, Style.EMPTY);
		List<class_5481> list = Lists.<class_5481>newArrayList();
		textRenderer.getTextHandler().method_29971(textCollector.getCombined(), width, Style.EMPTY, (stringRenderablex, boolean_) -> {
			class_5481 lv = Language.getInstance().method_30934(stringRenderablex);
			list.add(boolean_ ? class_5481.method_30742(field_25263, lv) : lv);
		});
		return (List<class_5481>)(list.isEmpty() ? Lists.<class_5481>newArrayList(class_5481.field_26385) : list);
	}
}
