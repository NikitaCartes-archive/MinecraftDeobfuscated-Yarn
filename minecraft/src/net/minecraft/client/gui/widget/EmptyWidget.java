package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class EmptyWidget extends ClickableWidget {
	public EmptyWidget(int width, int height) {
		this(0, 0, width, height);
	}

	public EmptyWidget(int x, int y, int width, int height) {
		super(x, y, width, height, Text.empty());
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	public static ClickableWidget ofWidth(int width) {
		return new EmptyWidget(width, 0);
	}

	public static ClickableWidget ofHeight(int height) {
		return new EmptyWidget(0, height);
	}
}
