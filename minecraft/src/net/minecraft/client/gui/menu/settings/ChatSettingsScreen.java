package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
		GameOption.CHAT_OPACITY,
		GameOption.CHAT_LINKS_PROMPT,
		GameOption.CHAT_SCALE,
		GameOption.CHAT_HEIGHT_FOCUSED,
		GameOption.SATURATION,
		GameOption.CHAT_WIDTH,
		GameOption.field_18187,
		GameOption.field_18194,
		GameOption.field_18196
	};
	private final Screen parent;
	private final GameOptions settings;
	private String field_2353;
	private ButtonWidget field_2355;

	public ChatSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.field_2353 = I18n.translate("options.chat.title");
		int i = 0;

		for (GameOption gameOption : SETTINGS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			ButtonWidget buttonWidget = gameOption.method_18520(this.client.options, j, k, 150);
			this.addButton(buttonWidget);
			if (gameOption == GameOption.field_18194) {
				this.field_2355 = buttonWidget;
				buttonWidget.enabled = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 144, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				ChatSettingsScreen.this.client.openScreen(ChatSettingsScreen.this.parent);
			}
		});
	}

	@Override
	public void onClosed() {
		this.client.options.write();
	}

	@Override
	public void method_18326(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2353, this.width / 2, 20, 16777215);
		super.method_18326(i, j, f);
	}

	public void method_2096() {
		this.field_2355.setText(GameOption.field_18194.method_18501(this.settings));
	}
}
