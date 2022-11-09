package net.minecraft.client.gui.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Tooltip implements Narratable {
	private static final int ROW_LENGTH = 170;
	private final Text content;
	@Nullable
	private List<OrderedText> lines;
	@Nullable
	private final Text narration;

	private Tooltip(Text content, @Nullable Text narration) {
		this.content = content;
		this.narration = narration;
	}

	public static Tooltip of(Text content, @Nullable Text narration) {
		return new Tooltip(content, narration);
	}

	public static Tooltip of(Text content) {
		return new Tooltip(content, content);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		if (this.narration != null) {
			builder.put(NarrationPart.HINT, this.narration);
		}
	}

	public List<OrderedText> getLines(MinecraftClient client) {
		if (this.lines == null) {
			this.lines = wrapLines(client, this.content);
		}

		return this.lines;
	}

	public static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
		return client.textRenderer.wrapLines(text, 170);
	}
}
