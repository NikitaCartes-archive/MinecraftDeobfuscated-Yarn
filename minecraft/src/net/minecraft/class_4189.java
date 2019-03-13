package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;

@Environment(EnvType.CLIENT)
public class class_4189 extends Screen {
	private static final GameOption[] field_18730 = new GameOption[]{
		GameOption.field_18194, GameOption.SUBTITLES, GameOption.field_18723, GameOption.field_18724, GameOption.field_1921
	};
	private final Screen field_18731;
	private final GameOptions field_18732;
	private String field_18733;
	private ButtonWidget field_18734;

	public class_4189(Screen screen, GameOptions gameOptions) {
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
			ButtonWidget buttonWidget = gameOption.method_18520(this.client.field_1690, j, k, 150);
			this.addButton(buttonWidget);
			if (gameOption == GameOption.field_18194) {
				this.field_18734 = buttonWidget;
				buttonWidget.enabled = NarratorManager.INSTANCE.isActive();
			}

			i++;
		}

		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 6 + 144, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				class_4189.this.client.method_1507(class_4189.this.field_18731);
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
		this.drawStringCentered(this.fontRenderer, this.field_18733, this.screenWidth / 2, 20, 16777215);
		super.draw(i, j, f);
	}

	public void method_19366() {
		this.field_18734.setText(GameOption.field_18194.method_18501(this.field_18732));
	}
}
