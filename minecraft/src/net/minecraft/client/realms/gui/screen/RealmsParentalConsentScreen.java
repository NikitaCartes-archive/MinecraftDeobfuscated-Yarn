package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
	private static final Text PRIVACY_INFO_TEXT = Text.translatable("mco.account.privacyinfo");
	private final Screen parent;
	private MultilineText privacyInfoText = MultilineText.EMPTY;

	public RealmsParentalConsentScreen(Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
	}

	@Override
	public void init() {
		Text text = Text.translatable("mco.account.update");
		Text text2 = ScreenTexts.BACK;
		int i = Math.max(this.textRenderer.getWidth(text), this.textRenderer.getWidth(text2)) + 30;
		Text text3 = Text.translatable("mco.account.privacy.info");
		int j = (int)((double)this.textRenderer.getWidth(text3) * 1.2);
		this.addDrawableChild(
			ButtonWidget.builder(text3, button -> Util.getOperatingSystem().open("https://aka.ms/MinecraftGDPR"))
				.dimensions(this.width / 2 - j / 2, row(11), j, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(text, button -> Util.getOperatingSystem().open("https://aka.ms/UpdateMojangAccount"))
				.dimensions(this.width / 2 - (i + 5), row(13), i, 20)
				.build()
		);
		this.addDrawableChild(ButtonWidget.builder(text2, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 5, row(13), i, 20).build());
		this.privacyInfoText = MultilineText.create(this.textRenderer, PRIVACY_INFO_TEXT, (int)Math.round((double)this.width * 0.9));
	}

	@Override
	public Text getNarratedTitle() {
		return PRIVACY_INFO_TEXT;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		this.privacyInfoText.drawCenterWithShadow(context, this.width / 2, 15, 15, 16777215);
		super.render(context, mouseX, mouseY, delta);
	}
}
