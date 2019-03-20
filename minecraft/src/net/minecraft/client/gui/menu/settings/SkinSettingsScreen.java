package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
				GameOption.MAIN_HAND,
				GameOption.MAIN_HAND.method_18501(this.client.options)
			) {
				@Override
				public void onPressed() {
					GameOption.MAIN_HAND.method_18500(SkinSettingsScreen.this.client.options, 1);
					SkinSettingsScreen.this.client.options.write();
					this.setMessage(GameOption.MAIN_HAND.method_18501(SkinSettingsScreen.this.client.options));
					SkinSettingsScreen.this.client.options.onPlayerModelPartChange();
				}
			}
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 24 * (i >> 1), I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				SkinSettingsScreen.this.client.openScreen(SkinSettingsScreen.this.parent);
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
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 20, 16777215);
		super.render(i, j, f);
	}

	private String method_2248(PlayerModelPart playerModelPart) {
		String string;
		if (this.client.options.getEnabledPlayerModelParts().contains(playerModelPart)) {
			string = I18n.translate("options.on");
		} else {
			string = I18n.translate("options.off");
		}

		return playerModelPart.getLocalizedName().getFormattedText() + ": " + string;
	}

	@Environment(EnvType.CLIENT)
	class class_441 extends ButtonWidget {
		private final PlayerModelPart field_2579;

		private class_441(int i, int j, int k, int l, PlayerModelPart playerModelPart) {
			super(i, j, k, l, SkinSettingsScreen.this.method_2248(playerModelPart));
			this.field_2579 = playerModelPart;
		}

		@Override
		public void onPressed() {
			SkinSettingsScreen.this.client.options.togglePlayerModelPart(this.field_2579);
			this.setMessage(SkinSettingsScreen.this.method_2248(this.field_2579));
		}
	}
}
