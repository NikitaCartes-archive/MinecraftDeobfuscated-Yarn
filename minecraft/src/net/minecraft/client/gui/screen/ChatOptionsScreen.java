package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ChatOptionsScreen extends Screen {
	private static final Option[] OPTIONS = new Option[]{
		Option.VISIBILITY,
		Option.CHAT_COLOR,
		Option.CHAT_LINKS,
		Option.CHAT_LINKS_PROMPT,
		Option.field_1921,
		Option.field_18723,
		Option.field_1946,
		Option.field_1941,
		Option.field_1940,
		Option.field_1939,
		Option.REDUCED_DEBUG_INFO,
		Option.AUTO_SUGGESTIONS,
		Option.NARRATOR
	};
	private final Screen field_2354;
	private final GameOptions options;
	private AbstractButtonWidget narratorOptionButton;

	public ChatOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableText("options.chat.title"));
		this.field_2354 = screen;
		this.options = gameOptions;
	}

	@Override
	protected void init() {
		int i = 0;

		for (Option option : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(option.method_18520(this.minecraft.field_1690, j, k, 150));
			if (option == Option.NARRATOR) {
				this.narratorOptionButton = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 6 + 24 * (i + 1) / 2, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.method_1507(this.field_2354)
			)
		);
	}

	@Override
	public void removed() {
		this.minecraft.field_1690.write();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
	}

	public void method_2096() {
		this.narratorOptionButton.setMessage(Option.NARRATOR.method_18501(this.options));
	}
}
