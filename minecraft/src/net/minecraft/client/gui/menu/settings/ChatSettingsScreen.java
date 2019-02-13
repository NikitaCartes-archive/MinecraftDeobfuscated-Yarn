package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GameOptionSliderWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;

@Environment(EnvType.CLIENT)
public class ChatSettingsScreen extends Screen {
	private static final GameOptions.Option[] SETTINGS = new GameOptions.Option[]{
		GameOptions.Option.VISIBILITY,
		GameOptions.Option.CHAT_COLOR,
		GameOptions.Option.CHAT_LINKS,
		GameOptions.Option.CHAT_OPACITY,
		GameOptions.Option.CHAT_LINKS_PROMPT,
		GameOptions.Option.CHAT_SCALE,
		GameOptions.Option.CHAT_HEIGHT_FOCUSED,
		GameOptions.Option.CHAT_HEIGHT_UNFOCUSED,
		GameOptions.Option.CHAT_WIDTH,
		GameOptions.Option.REDUCED_DEBUG_INFO,
		GameOptions.Option.NARRATOR,
		GameOptions.Option.AUTO_SUGGEST_COMMANDS
	};
	private final Screen parent;
	private final GameOptions settings;
	private String field_2353;
	private OptionButtonWidget field_2355;

	public ChatSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.field_2353 = I18n.translate("options.chat.title");
		int i = 0;

		for (GameOptions.Option option : SETTINGS) {
			if (option.isSlider()) {
				this.addButton(new GameOptionSliderWidget(this.client, option.getId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option));
			} else {
				OptionButtonWidget optionButtonWidget = new OptionButtonWidget(
					option.getId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option, this.settings.getTranslatedName(option)
				) {
					@Override
					public void onPressed(double d, double e) {
						ChatSettingsScreen.this.settings.setInteger(this.getOption(), 1);
						this.setText(ChatSettingsScreen.this.settings.getTranslatedName(GameOptions.Option.byId(this.id)));
					}
				};
				this.addButton(optionButtonWidget);
				if (option == GameOptions.Option.NARRATOR) {
					this.field_2355 = optionButtonWidget;
					optionButtonWidget.enabled = NarratorManager.INSTANCE.isActive();
				}
			}

			i++;
		}

		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 144, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				ChatSettingsScreen.this.client.options.write();
				ChatSettingsScreen.this.client.openScreen(ChatSettingsScreen.this.parent);
			}
		});
	}

	@Override
	public void close() {
		this.client.options.write();
		super.close();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2353, this.width / 2, 20, 16777215);
		super.draw(i, j, f);
	}

	public void method_2096() {
		this.field_2355.setText(this.settings.getTranslatedName(GameOptions.Option.byId(this.field_2355.id)));
	}
}
