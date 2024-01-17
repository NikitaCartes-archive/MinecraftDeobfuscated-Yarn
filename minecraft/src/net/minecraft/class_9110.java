package net.minecraft;

import java.time.Duration;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.FocusedTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class class_9110 {
	@Nullable
	private Tooltip field_48390;
	private Duration field_48391 = Duration.ZERO;
	private long field_48392;
	private boolean field_48393;

	public void method_56141(Duration duration) {
		this.field_48391 = duration;
	}

	public void method_56138(@Nullable Tooltip tooltip) {
		this.field_48390 = tooltip;
	}

	@Nullable
	public Tooltip method_56137() {
		return this.field_48390;
	}

	public void method_56142(boolean bl, boolean bl2, ScreenRect screenRect) {
		if (this.field_48390 == null) {
			this.field_48393 = false;
		} else {
			boolean bl3 = bl || bl2 && MinecraftClient.getInstance().getNavigationType().isKeyboard();
			if (bl3 != this.field_48393) {
				if (bl3) {
					this.field_48392 = Util.getMeasuringTimeMs();
				}

				this.field_48393 = bl3;
			}

			if (bl3 && Util.getMeasuringTimeMs() - this.field_48392 > this.field_48391.toMillis()) {
				Screen screen = MinecraftClient.getInstance().currentScreen;
				if (screen != null) {
					screen.setTooltip(this.field_48390, this.method_56140(screenRect, bl, bl2), bl2);
				}
			}
		}
	}

	private TooltipPositioner method_56140(ScreenRect screenRect, boolean bl, boolean bl2) {
		return (TooltipPositioner)(!bl && bl2 && MinecraftClient.getInstance().getNavigationType().isKeyboard()
			? new FocusedTooltipPositioner(screenRect)
			: new WidgetTooltipPositioner(screenRect));
	}

	public void method_56139(NarrationMessageBuilder narrationMessageBuilder) {
		if (this.field_48390 != null) {
			this.field_48390.appendNarrations(narrationMessageBuilder);
		}
	}
}
