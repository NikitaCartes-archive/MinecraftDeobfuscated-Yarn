package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class AccessibilityScreen extends Screen {
	private static final GameOption[] OPTIONS = new GameOption[]{
		GameOption.NARRATOR, GameOption.SUBTITLES, GameOption.TEXT_BACKGROUND_OPACITY, GameOption.TEXT_BACKGROUND, GameOption.CHAT_OPACITY, GameOption.AUTO_JUMP
	};
	private final Screen parent;
	private final GameOptions gameOptions;
	private AbstractButtonWidget narratorButton;

	public AccessibilityScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableTextComponent("options.accessibility.title"));
		this.parent = screen;
		this.gameOptions = gameOptions;
	}

	@Override
	protected void init() {
		int i = 0;

		for (GameOption gameOption : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(gameOption.createOptionButton(this.minecraft.options, j, k, 150));
			if (gameOption == GameOption.NARRATOR) {
				this.narratorButton = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 6 + 144, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
	}

	@Override
	public void removed() {
		this.minecraft.options.write();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
	}

	public void method_19366() {
		this.narratorButton.setMessage(GameOption.NARRATOR.get(this.gameOptions));
	}
}
