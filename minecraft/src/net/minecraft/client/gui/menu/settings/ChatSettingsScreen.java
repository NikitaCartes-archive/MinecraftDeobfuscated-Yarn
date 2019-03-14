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
	private String field_2353;
	private AbstractButtonWidget field_2355;

	public ChatSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.field_2353 = I18n.translate("options.chat.title");
		int i = 0;

		for (GameOption gameOption : SETTINGS) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = gameOption.method_18520(this.client.options, j, k, 150);
			this.addButton(abstractButtonWidget);
			if (gameOption == GameOption.NARRATOR) {
				this.field_2355 = abstractButtonWidget;
				abstractButtonWidget.enabled = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 24 * (i + 1) / 2, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				ChatSettingsScreen.this.client.openScreen(ChatSettingsScreen.this.parent);
			}
		});
	}

	@Override
	public void onClosed() {
		this.client.options.write();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2353, this.screenWidth / 2, 20, 16777215);
		super.draw(i, j, f);
	}

	public void method_2096() {
		this.field_2355.setText(GameOption.NARRATOR.method_18501(this.settings));
	}
}
