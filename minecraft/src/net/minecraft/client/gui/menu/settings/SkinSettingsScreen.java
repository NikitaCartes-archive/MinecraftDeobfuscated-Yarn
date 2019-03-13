package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class SkinSettingsScreen extends Screen {
	private final Screen parent;
	private String title;

	public SkinSettingsScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		int i = 0;
		this.title = I18n.translate("options.skinCustomisation.title");

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			this.addButton(new SkinSettingsScreen.class_441(this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 + 24 * (i >> 1), 150, 20, playerModelPart));
			i++;
		}

		this.addButton(
			new OptionButtonWidget(
				this.screenWidth / 2 - 155 + i % 2 * 160,
				this.screenHeight / 6 + 24 * (i >> 1),
				150,
				20,
				GameOption.field_18193,
				GameOption.field_18193.method_18501(this.client.field_1690)
			) {
				@Override
				public void method_1826() {
					GameOption.field_18193.method_18500(SkinSettingsScreen.this.client.field_1690, 1);
					SkinSettingsScreen.this.client.field_1690.write();
					this.setText(GameOption.field_18193.method_18501(SkinSettingsScreen.this.client.field_1690));
					SkinSettingsScreen.this.client.field_1690.onPlayerModelPartChange();
				}
			}
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 6 + 24 * (i >> 1), I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				SkinSettingsScreen.this.client.method_1507(SkinSettingsScreen.this.parent);
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
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 20, 16777215);
		super.draw(i, j, f);
	}

	private String method_2248(PlayerModelPart playerModelPart) {
		String string;
		if (this.client.field_1690.getEnabledPlayerModelParts().contains(playerModelPart)) {
			string = I18n.translate("options.on");
		} else {
			string = I18n.translate("options.off");
		}

		return playerModelPart.method_7428().getFormattedText() + ": " + string;
	}

	@Environment(EnvType.CLIENT)
	class class_441 extends class_4185 {
		private final PlayerModelPart field_2579;

		private class_441(int i, int j, int k, int l, PlayerModelPart playerModelPart) {
			super(i, j, k, l, SkinSettingsScreen.this.method_2248(playerModelPart));
			this.field_2579 = playerModelPart;
		}

		@Override
		public void method_1826() {
			SkinSettingsScreen.this.client.field_1690.togglePlayerModelPart(this.field_2579);
			this.setText(SkinSettingsScreen.this.method_2248(this.field_2579));
		}
	}
}
