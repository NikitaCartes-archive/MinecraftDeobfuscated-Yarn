package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
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
		Option.CHAT_WIDTH,
		Option.CHAT_HEIGHT_FOCUSED,
		Option.SATURATION,
		Option.REDUCED_DEBUG_INFO,
		Option.AUTO_SUGGESTIONS,
		Option.NARRATOR
	};
	private AbstractButtonWidget narratorOptionButton;

	public ChatOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(screen, gameOptions, new TranslatableText("options.chat.title"));
	}

	@Override
	protected void init() {
		int i = 0;

		for (Option option : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(option.createButton(this.minecraft.options, j, k, 150));
			if (option == Option.NARRATOR) {
				this.narratorOptionButton = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 6 + 24 * (i + 1) / 2, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.field_21335)
			)
		);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
	}

	public void setNarratorMessage() {
		this.narratorOptionButton.setMessage(Option.NARRATOR.getMessage(this.field_21336));
	}
}
