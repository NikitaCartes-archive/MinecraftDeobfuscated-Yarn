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
public class AccessibilityScreen extends Screen {
	private static final Option[] OPTIONS = new Option[]{
		Option.NARRATOR, Option.SUBTITLES, Option.field_18723, Option.TEXT_BACKGROUND, Option.field_1921, Option.AUTO_JUMP
	};
	private final Screen field_18731;
	private final GameOptions gameOptions;
	private AbstractButtonWidget narratorButton;

	public AccessibilityScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableText("options.accessibility.title"));
		this.field_18731 = screen;
		this.gameOptions = gameOptions;
	}

	@Override
	protected void init() {
		int i = 0;

		for (Option option : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(option.method_18520(this.minecraft.field_1690, j, k, 150));
			if (option == Option.NARRATOR) {
				this.narratorButton = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 6 + 144, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.method_1507(this.field_18731)
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

	public void method_19366() {
		this.narratorButton.setMessage(Option.NARRATOR.method_18501(this.gameOptions));
	}
}
