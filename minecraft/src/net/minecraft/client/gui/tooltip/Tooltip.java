package net.minecraft.client.gui.tooltip;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class Tooltip implements Narratable {
	private static final int ROW_LENGTH = 170;
	private final Text content;
	@Nullable
	private List<OrderedText> lines;
	@Nullable
	private final Text narration;
	private int delay;
	private long renderCheckTime;
	private boolean prevShouldRender;

	private Tooltip(Text content, @Nullable Text narration) {
		this.content = content;
		this.narration = narration;
	}

	public void setDelay(int delay) {
		this.delay = delay;
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

	public void render(boolean hovered, boolean focused, ScreenRect focus) {
		boolean bl = hovered || focused && MinecraftClient.getInstance().getNavigationType().isKeyboard();
		if (bl != this.prevShouldRender) {
			if (bl) {
				this.renderCheckTime = Util.getMeasuringTimeMs();
			}

			this.prevShouldRender = bl;
		}

		if (bl && Util.getMeasuringTimeMs() - this.renderCheckTime > (long)this.delay) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			if (screen != null) {
				screen.setTooltip(this, this.createPositioner(hovered, focused, focus), focused);
			}
		}
	}

	protected TooltipPositioner createPositioner(boolean hovered, boolean focused, ScreenRect focus) {
		return (TooltipPositioner)(!hovered && focused && MinecraftClient.getInstance().getNavigationType().isKeyboard()
			? new FocusedTooltipPositioner(focus)
			: new WidgetTooltipPositioner(focus));
	}
}
