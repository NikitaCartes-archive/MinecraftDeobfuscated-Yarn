package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class SkinSettingsGui extends Gui {
	private final Gui parent;
	private String title;

	public SkinSettingsGui(Gui gui) {
		this.parent = gui;
	}

	@Override
	protected void onInitialized() {
		int i = 0;
		this.title = I18n.translate("options.skinCustomisation.title");

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			this.addButton(
				new SkinSettingsGui.class_441(playerModelPart.getId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, playerModelPart)
			);
			i++;
		}

		this.addButton(
			new OptionButtonWidget(
				199,
				this.width / 2 - 155 + i % 2 * 160,
				this.height / 6 + 24 * (i >> 1),
				GameOptions.Option.MAIN_HAND,
				this.client.options.getTranslatedName(GameOptions.Option.MAIN_HAND)
			) {
				@Override
				public void onPressed(double d, double e) {
					SkinSettingsGui.this.client.options.updateOption(GameOptions.Option.MAIN_HAND, 1);
					this.text = SkinSettingsGui.this.client.options.getTranslatedName(GameOptions.Option.MAIN_HAND);
					SkinSettingsGui.this.client.options.onPlayerModelPartChange();
				}
			}
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				SkinSettingsGui.this.client.options.write();
				SkinSettingsGui.this.client.openGui(SkinSettingsGui.this.parent);
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
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
		super.draw(i, j, f);
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

		private class_441(int i, int j, int k, int l, int m, PlayerModelPart playerModelPart) {
			super(i, j, k, l, m, SkinSettingsGui.this.method_2248(playerModelPart));
			this.field_2579 = playerModelPart;
		}

		@Override
		public void onPressed(double d, double e) {
			SkinSettingsGui.this.client.options.togglePlayerModelPart(this.field_2579);
			this.text = SkinSettingsGui.this.method_2248(this.field_2579);
		}
	}
}
