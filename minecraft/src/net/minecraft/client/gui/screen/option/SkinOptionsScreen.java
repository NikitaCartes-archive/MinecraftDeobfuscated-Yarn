package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.skinCustomisation.title");

	public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Override
	protected void init() {
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			adder.add(
				CyclingButtonWidget.onOffBuilder(this.gameOptions.isPlayerModelPartEnabled(playerModelPart))
					.build(playerModelPart.getOptionName(), (button, enabled) -> this.gameOptions.togglePlayerModelPart(playerModelPart, enabled))
			);
		}

		adder.add(this.gameOptions.getMainArm().createWidget(this.gameOptions));
		this.layout.addBody(gridWidget);
		super.init();
	}
}
