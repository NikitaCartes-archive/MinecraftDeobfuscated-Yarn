package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
	private final Text title;
	private final Text reason;
	private class_5489 lines = class_5489.field_26528;
	private final Screen parent;
	private int textHeight;

	public DisconnectedRealmsScreen(Screen parent, Text text, Text reason) {
		this.parent = parent;
		this.title = text;
		this.reason = reason;
	}

	@Override
	public void init() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.setConnectedToRealms(false);
		minecraftClient.getResourcePackDownloader().clear();
		Realms.narrateNow(this.title.getString() + ": " + this.reason.getString());
		this.lines = class_5489.method_30890(this.textRenderer, this.reason, this.width - 50);
		this.textHeight = this.lines.method_30887() * 9;
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20, ScreenTexts.BACK, buttonWidget -> minecraftClient.openScreen(this.parent)
			)
		);
	}

	@Override
	public void onClose() {
		MinecraftClient.getInstance().openScreen(this.parent);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
		this.lines.method_30888(matrices, this.width / 2, this.height / 2 - this.textHeight / 2);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
