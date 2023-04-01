package net.minecraft;

import com.google.common.collect.Streams;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public record class_8440(OrderedText contents, long index, Text original) {
	public static Stream<class_8440> method_50950(TextRenderer textRenderer, Stream<Text> stream, int i) {
		return stream.flatMap(text -> Streams.mapWithIndex(textRenderer.wrapLines(text, i).stream(), (orderedText, l) -> new class_8440(orderedText, l, text)));
	}
}
