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
public class ChatOptionsScreen extends GameOptionsScreen {
	private static final Option[] OPTIONS = new Option[]{
		Option.VISIBILITY,
		Option.CHAT_COLOR,
		Option.CHAT_LINKS,
		Option.CHAT_LINKS_PROMPT,
		Option.CHAT_OPACITY,
		Option.TEXT_BACKGROUND_OPACITY,
		Option.CHAT_SCALE,
		Option.field_23930,
		Option.CHAT_WIDTH,
		Option.CHAT_HEIGHT_FOCUSED,
		Option.SATURATION,
		Option.NARRATOR,
		Option.AUTO_SUGGESTIONS,
		Option.REDUCED_DEBUG_INFO
	};
	private AbstractButtonWidget narratorOptionButton;

	public ChatOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("options.chat.title"));
	}

	@Override
	protected void init() {
		int i = 0;

		for (Option option : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(option.createButton(this.client.options, j, k, 150));
			if (option == Option.NARRATOR) {
				this.narratorOptionButton = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (i + 1) / 2, 200, 20, ScreenTexts.DONE, buttonWidget -> this.client.openScreen(this.parent))
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.method_27534(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	public void setNarratorMessage() {
		this.narratorOptionButton.setMessage(Option.NARRATOR.getMessage(this.gameOptions));
	}
}
