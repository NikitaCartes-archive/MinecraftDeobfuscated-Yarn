package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends GameOptionsScreen {
	public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, new TranslatableText("options.skinCustomisation.title"));
	}

	@Override
	protected void init() {
		int i = 0;

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			this.addButton(
				new ButtonWidget(
					this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.getPlayerModelPartDisplayString(playerModelPart), buttonWidget -> {
						this.field_21336.togglePlayerModelPart(playerModelPart);
						buttonWidget.setMessage(this.getPlayerModelPartDisplayString(playerModelPart));
					}
				)
			);
			i++;
		}

		this.addButton(
			new OptionButtonWidget(
				this.width / 2 - 155 + i % 2 * 160,
				this.height / 6 + 24 * (i >> 1),
				150,
				20,
				Option.MAIN_HAND,
				Option.MAIN_HAND.getMessage(this.field_21336),
				buttonWidget -> {
					Option.MAIN_HAND.cycle(this.field_21336, 1);
					this.field_21336.write();
					buttonWidget.setMessage(Option.MAIN_HAND.getMessage(this.field_21336));
					this.field_21336.onPlayerModelPartChange();
				}
			)
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.field_21335)
			)
		);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, delta);
	}

	private String getPlayerModelPartDisplayString(PlayerModelPart part) {
		String string;
		if (this.field_21336.getEnabledPlayerModelParts().contains(part)) {
			string = I18n.translate("options.on");
		} else {
			string = I18n.translate("options.off");
		}

		return part.getOptionName().asFormattedString() + ": " + string;
	}
}
