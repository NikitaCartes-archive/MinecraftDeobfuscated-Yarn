package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
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
		GameOption.field_1921,
		GameOption.field_18723,
		GameOption.field_1946,
		GameOption.field_1941,
		GameOption.field_1940,
		GameOption.field_1939,
		GameOption.REDUCED_DEBUG_INFO,
		GameOption.AUTO_SUGGESTIONS,
		GameOption.field_18194
	};
	private final Screen field_2354;
	private final GameOptions settings;
	private String field_2353;
	private ButtonWidget field_2355;

	public ChatSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.field_2354 = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.field_2353 = I18n.translate("options.chat.title");
		int i = 0;

		for (GameOption gameOption : SETTINGS) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 + 24 * (i >> 1);
			ButtonWidget buttonWidget = gameOption.method_18520(this.client.field_1690, j, k, 150);
			this.addButton(buttonWidget);
			if (gameOption == GameOption.field_18194) {
				this.field_2355 = buttonWidget;
				buttonWidget.enabled = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 6 + 24 * (i + 1) / 2, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				ChatSettingsScreen.this.client.method_1507(ChatSettingsScreen.this.field_2354);
			}
		});
	}

	@Override
	public void onClosed() {
		this.client.field_1690.write();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2353, this.screenWidth / 2, 20, 16777215);
		super.draw(i, j, f);
	}

	public void method_2096() {
		this.field_2355.setText(GameOption.field_18194.method_18501(this.settings));
	}
}
