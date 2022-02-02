package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class OnlineOptionsScreen extends SimpleOptionsScreen {
	private static final Option[] OPTIONS = new Option[]{Option.REALMS_NOTIFICATIONS, Option.ALLOW_SERVER_LISTING};

	public OnlineOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, new TranslatableText("options.online.title"), OPTIONS);
	}

	@Override
	protected void initFooter() {
		if (this.client.world != null) {
			CyclingButtonWidget<Difficulty> cyclingButtonWidget = this.addDrawableChild(
				OptionsScreen.createDifficultyButtonWidget(OPTIONS.length, this.width, this.height, "options.difficulty.online", this.client)
			);
			cyclingButtonWidget.active = false;
		}

		super.initFooter();
	}
}
