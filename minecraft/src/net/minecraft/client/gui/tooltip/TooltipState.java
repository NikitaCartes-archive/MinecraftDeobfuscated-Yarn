package net.minecraft.client.gui.tooltip;

import java.time.Duration;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class TooltipState {
	@Nullable
	private Tooltip tooltip;
	private Duration delay = Duration.ZERO;
	private long renderCheckTime;
	private boolean prevShouldRender;

	public void setDelay(Duration delay) {
		this.delay = delay;
	}

	public void setTooltip(@Nullable Tooltip tooltip) {
		this.tooltip = tooltip;
	}

	@Nullable
	public Tooltip getTooltip() {
		return this.tooltip;
	}

	public void render(boolean hovered, boolean focused, ScreenRect focus) {
		if (this.tooltip == null) {
			this.prevShouldRender = false;
		} else {
			boolean bl = hovered || focused && MinecraftClient.getInstance().getNavigationType().isKeyboard();
			if (bl != this.prevShouldRender) {
				if (bl) {
					this.renderCheckTime = Util.getMeasuringTimeMs();
				}

				this.prevShouldRender = bl;
			}

			if (bl && Util.getMeasuringTimeMs() - this.renderCheckTime > this.delay.toMillis()) {
				Screen screen = MinecraftClient.getInstance().currentScreen;
				if (screen != null) {
					screen.setTooltip(this.tooltip, this.createPositioner(focus, hovered, focused), focused);
				}
			}
		}
	}

	private TooltipPositioner createPositioner(ScreenRect focus, boolean hovered, boolean focused) {
		return (TooltipPositioner)(!hovered && focused && MinecraftClient.getInstance().getNavigationType().isKeyboard()
			? new FocusedTooltipPositioner(focus)
			: new WidgetTooltipPositioner(focus));
	}

	public void appendNarrations(NarrationMessageBuilder builder) {
		if (this.tooltip != null) {
			this.tooltip.appendNarrations(builder);
		}
	}
}
