package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class MultiplayerWarningScreen extends Screen {
	private final Screen parent;
	private static final Text header = new TranslatableText("multiplayerWarning.header").formatted(Formatting.field_1067);
	private static final Text message = new TranslatableText("multiplayerWarning.message");
	private static final Text checkMessage = new TranslatableText("multiplayerWarning.check");
	private static final Text proceedText = header.shallowCopy().append("\n").append(message);
	private CheckboxWidget checkbox;
	private class_5489 lines = class_5489.field_26528;

	public MultiplayerWarningScreen(Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.lines = class_5489.method_30890(this.textRenderer, message, this.width - 50);
		int i = (this.lines.method_30887() + 1) * 9;
		this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, ScreenTexts.PROCEED, buttonWidget -> {
			if (this.checkbox.isChecked()) {
				this.client.options.skipMultiplayerWarning = true;
				this.client.options.write();
			}

			this.client.openScreen(new MultiplayerScreen(this.parent));
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
		this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, checkMessage, false);
		this.addButton(this.checkbox);
	}

	@Override
	public String getNarrationMessage() {
		return proceedText.getString();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawCenteredText(matrices, this.textRenderer, header, this.width / 2, 30, 16777215);
		this.lines.method_30888(matrices, this.width / 2, 70);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
