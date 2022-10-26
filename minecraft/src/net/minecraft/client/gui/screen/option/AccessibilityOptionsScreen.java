package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class AccessibilityOptionsScreen extends SimpleOptionsScreen {
	private static final String GUIDE_URL = "https://aka.ms/MinecraftJavaAccessibility";

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{
			gameOptions.getNarrator(),
			gameOptions.getShowSubtitles(),
			gameOptions.getTextBackgroundOpacity(),
			gameOptions.getBackgroundForChatOnly(),
			gameOptions.getChatOpacity(),
			gameOptions.getChatLineSpacing(),
			gameOptions.getChatDelay(),
			gameOptions.getAutoJump(),
			gameOptions.getSneakToggled(),
			gameOptions.getSprintToggled(),
			gameOptions.getDistortionEffectScale(),
			gameOptions.getFovEffectScale(),
			gameOptions.getMonochromeLogo(),
			gameOptions.getHideLightningFlashes(),
			gameOptions.getDarknessEffectScale(),
			gameOptions.getPanoramaSpeed()
		};
	}

	public AccessibilityOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, Text.translatable("options.accessibility.title"), getOptions(gameOptions));
	}

	@Override
	protected void initFooter() {
		this.addDrawableChild(
			ButtonWidget.createBuilder(Text.translatable("options.accessibility.link"), button -> this.client.setScreen(new ConfirmLinkScreen(openInBrowser -> {
					if (openInBrowser) {
						Util.getOperatingSystem().open("https://aka.ms/MinecraftJavaAccessibility");
					}

					this.client.setScreen(this);
				}, "https://aka.ms/MinecraftJavaAccessibility", true))).setPositionAndSize(this.width / 2 - 155, this.height - 27, 150, 20).build()
		);
		this.addDrawableChild(
			ButtonWidget.createBuilder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent))
				.setPositionAndSize(this.width / 2 + 5, this.height - 27, 150, 20)
				.build()
		);
	}
}
