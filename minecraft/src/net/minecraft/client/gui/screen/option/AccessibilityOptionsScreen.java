package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;

@Environment(EnvType.CLIENT)
public class AccessibilityOptionsScreen extends GameOptionsScreen {
	public static final Text TITLE_TEXT = Text.translatable("options.accessibility.title");

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{
			gameOptions.getNarrator(),
			gameOptions.getShowSubtitles(),
			gameOptions.getHighContrast(),
			gameOptions.getAutoJump(),
			gameOptions.getMenuBackgroundBlurriness(),
			gameOptions.getTextBackgroundOpacity(),
			gameOptions.getBackgroundForChatOnly(),
			gameOptions.getChatOpacity(),
			gameOptions.getChatLineSpacing(),
			gameOptions.getChatDelay(),
			gameOptions.getNotificationDisplayTime(),
			gameOptions.getBobView(),
			gameOptions.getSneakToggled(),
			gameOptions.getSprintToggled(),
			gameOptions.getDistortionEffectScale(),
			gameOptions.getFovEffectScale(),
			gameOptions.getDarknessEffectScale(),
			gameOptions.getDamageTiltStrength(),
			gameOptions.getGlintSpeed(),
			gameOptions.getGlintStrength(),
			gameOptions.getHideLightningFlashes(),
			gameOptions.getMonochromeLogo(),
			gameOptions.getPanoramaSpeed(),
			gameOptions.getHideSplashTexts(),
			gameOptions.getNarratorHotkey()
		};
	}

	public AccessibilityOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Override
	protected void init() {
		super.init();
		ClickableWidget clickableWidget = this.body.getWidgetFor(this.gameOptions.getHighContrast());
		if (clickableWidget != null && !this.client.getResourcePackManager().getIds().contains("high_contrast")) {
			clickableWidget.active = false;
			clickableWidget.setTooltip(Tooltip.of(Text.translatable("options.accessibility.high_contrast.error.tooltip")));
		}
	}

	@Override
	protected void addOptions() {
		this.body.addAll(getOptions(this.gameOptions));
	}

	@Override
	protected void initFooter() {
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget.add(
			ButtonWidget.builder(Text.translatable("options.accessibility.link"), ConfirmLinkScreen.opening(this, Urls.JAVA_ACCESSIBILITY)).build()
		);
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).build());
	}
}
