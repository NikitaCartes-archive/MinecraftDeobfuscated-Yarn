package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class OnlineOptionsScreen extends SimpleOptionsScreen {
	public OnlineOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(
			parent,
			gameOptions,
			Text.translatable("options.online.title"),
			new SimpleOption[]{gameOptions.getRealmsNotifications(), gameOptions.getAllowServerListing()}
		);
	}

	@Override
	protected void initFooter() {
		if (this.client.world != null) {
			CyclingButtonWidget<Difficulty> cyclingButtonWidget = this.addDrawableChild(
				OptionsScreen.createDifficultyButtonWidget(this.options.length, this.width, this.height, "options.difficulty.online", this.client)
			);
			cyclingButtonWidget.active = false;
		}

		super.initFooter();
	}
}
