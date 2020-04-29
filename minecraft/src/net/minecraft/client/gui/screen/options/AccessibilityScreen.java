package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class AccessibilityScreen extends GameOptionsScreen {
	private static final Option[] OPTIONS = new Option[]{
		Option.NARRATOR,
		Option.SUBTITLES,
		Option.TEXT_BACKGROUND_OPACITY,
		Option.TEXT_BACKGROUND,
		Option.CHAT_OPACITY,
		Option.CHAT_LINE_SPACING,
		Option.CHAT_DELAY_INSTANT,
		Option.AUTO_JUMP,
		Option.SNEAK_TOGGLED,
		Option.SPRINT_TOGGLED
	};
	private AbstractButtonWidget narratorButton;

	public AccessibilityScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, new TranslatableText("options.accessibility.title"));
	}

	@Override
	protected void init() {
		int i = 0;

		for (Option option : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(option.createButton(this.client.options, j, k, 150));
			if (option == Option.NARRATOR) {
				this.narratorButton = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 144, 200, 20, ScreenTexts.DONE, buttonWidget -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawStringWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	public void setNarratorMessage() {
		this.narratorButton.setMessage(Option.NARRATOR.getMessage(this.gameOptions));
	}
}
