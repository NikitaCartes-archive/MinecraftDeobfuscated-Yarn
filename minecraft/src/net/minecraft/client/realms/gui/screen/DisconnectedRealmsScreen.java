package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
	private final Text reason;
	private MultilineText lines = MultilineText.EMPTY;
	private final Screen parent;
	private int textHeight;

	public DisconnectedRealmsScreen(Screen parent, Text title, Text reason) {
		super(title);
		this.parent = parent;
		this.reason = reason;
	}

	@Override
	public void init() {
		this.client.getServerResourcePackProvider().clear();
		this.lines = MultilineText.create(this.textRenderer, this.reason, this.width - 50);
		this.textHeight = this.lines.count() * 9;
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, buttonWidget -> this.client.setScreen(this.parent))
				.dimensions(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20)
				.build()
		);
	}

	@Override
	public Text getNarratedTitle() {
		return Text.empty().append(this.title).append(": ").append(this.reason);
	}

	@Override
	public void close() {
		MinecraftClient.getInstance().setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
		this.lines.drawCenterWithShadow(context, this.width / 2, this.height / 2 - this.textHeight / 2);
	}
}
