package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class SkinSettingsScreen extends Screen {
	private final Screen parent;

	public SkinSettingsScreen(Screen screen) {
		super(new TranslatableTextComponent("options.skinCustomisation.title"));
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		int i = 0;

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			this.addButton(
				new ButtonWidget(
					this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 + 24 * (i >> 1), 150, 20, this.method_2248(playerModelPart), buttonWidget -> {
						this.client.options.togglePlayerModelPart(playerModelPart);
						buttonWidget.setMessage(this.method_2248(playerModelPart));
					}
				)
			);
			i++;
		}

		this.addButton(
			new OptionButtonWidget(
				this.screenWidth / 2 - 155 + i % 2 * 160,
				this.screenHeight / 6 + 24 * (i >> 1),
				150,
				20,
				GameOption.MAIN_HAND,
				GameOption.MAIN_HAND.method_18501(this.client.options),
				buttonWidget -> {
					GameOption.MAIN_HAND.method_18500(this.client.options, 1);
					this.client.options.write();
					buttonWidget.setMessage(GameOption.MAIN_HAND.method_18501(this.client.options));
					this.client.options.onPlayerModelPartChange();
				}
			)
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100, this.screenHeight / 6 + 24 * (i >> 1), 200, 20, I18n.translate("gui.done"), buttonWidget -> this.client.openScreen(this.parent)
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

	private String method_2248(PlayerModelPart playerModelPart) {
		String string;
		if (this.client.options.getEnabledPlayerModelParts().contains(playerModelPart)) {
			string = I18n.translate("options.on");
		} else {
			string = I18n.translate("options.off");
		}

		return playerModelPart.getLocalizedName().getFormattedText() + ": " + string;
	}
}
