package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.skinCustomisation.title");

	public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Override
	protected void addOptions() {
		List<ClickableWidget> list = new ArrayList();

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			list.add(
				CyclingButtonWidget.onOffBuilder(this.gameOptions.isPlayerModelPartEnabled(playerModelPart))
					.build(playerModelPart.getOptionName(), (cyclingButtonWidget, boolean_) -> this.gameOptions.togglePlayerModelPart(playerModelPart, boolean_))
			);
		}

		list.add(this.gameOptions.getMainArm().createWidget(this.gameOptions));
		this.body.addAll(list);
	}
}
