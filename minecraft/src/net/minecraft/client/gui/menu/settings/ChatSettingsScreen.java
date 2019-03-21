package net.minecraft.client.gui.menu.settings;

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
public class ChatSettingsScreen extends Screen {
	private static final GameOption[] SETTINGS = new GameOption[]{
		GameOption.VISIBILITY,
		GameOption.CHAT_COLOR,
		GameOption.CHAT_LINKS,
		GameOption.CHAT_LINKS_PROMPT,
		GameOption.CHAT_OPACITY,
		GameOption.TEXT_BACKGROUND_OPACITY,
		GameOption.CHAT_SCALE,
		GameOption.CHAT_WIDTH,
		GameOption.CHAT_HEIGHT_FOCUSED,
		GameOption.SATURATION,
		GameOption.REDUCED_DEBUG_INFO,
		GameOption.AUTO_SUGGESTIONS,
		GameOption.NARRATOR
	};
	private final Screen parent;
	private final GameOptions settings;
	private AbstractButtonWidget field_2355;

	public ChatSettingsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableTextComponent("options.chat.title"));
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		int i = 0;

		for (GameOption gameOption : SETTINGS) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = this.addButton(gameOption.createOptionButton(this.client.options, j, k, 150));
			if (gameOption == GameOption.NARRATOR) {
				this.field_2355 = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100,
				this.screenHeight / 6 + 24 * (i + 1) / 2,
				200,
				20,
				I18n.translate("gui.done"),
				buttonWidget -> this.client.openScreen(this.parent)
			)
		);
	}

	@Override
	public void onClosed() {
		this.client.options.write();
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 20, 16777215);
		super.render(i, j, f);
	}

	public void method_2096() {
		this.field_2355.setMessage(GameOption.NARRATOR.method_18501(this.settings));
	}
}
