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

@Environment(EnvType.CLIENT)
public class AccessibilityScreen extends Screen {
	private static final GameOption[] field_18730 = new GameOption[]{
		GameOption.NARRATOR, GameOption.SUBTITLES, GameOption.TEXT_BACKGROUND_OPACITY, GameOption.TEXT_BACKGROUND, GameOption.CHAT_OPACITY, GameOption.AUTO_JUMP
	};
	private final Screen field_18731;
	private final GameOptions field_18732;
	private String field_18733;
	private AbstractButtonWidget field_18734;

	public AccessibilityScreen(Screen screen, GameOptions gameOptions) {
		this.field_18731 = screen;
		this.field_18732 = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.field_18733 = I18n.translate("options.accessibility.title");
		int i = 0;

		for (GameOption gameOption : field_18730) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 + 24 * (i >> 1);
			AbstractButtonWidget abstractButtonWidget = gameOption.createOptionButton(this.client.options, j, k, 150);
			this.addButton(abstractButtonWidget);
			if (gameOption == GameOption.NARRATOR) {
				this.field_18734 = abstractButtonWidget;
				abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 144, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				AccessibilityScreen.this.client.openScreen(AccessibilityScreen.this.field_18731);
			}
		});
	}

	@Override
	public void onClosed() {
		this.client.options.write();
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_18733, this.screenWidth / 2, 20, 16777215);
		super.render(i, j, f);
	}

	public void method_19366() {
		this.field_18734.setMessage(GameOption.NARRATOR.method_18501(this.field_18732));
	}
}
